package ru.runneso.checker.infra.repositories.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.runneso.checker.domain.entities.Submission;
import ru.runneso.checker.domain.entities.Test;
import ru.runneso.checker.domain.exceptions.UndefinedException;
import ru.runneso.checker.domain.values.CodeCompiler;
import ru.runneso.checker.domain.values.MemoryLimit;
import ru.runneso.checker.domain.values.TimeLimit;
import ru.runneso.checker.entry.dto.schemas.RequestSubmissionDTO;
import ru.runneso.checker.infra.repositories.inter.ReaderRepository;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SQSReaderRepository extends BaseQueueRepository implements ReaderRepository {
    private final SqsClient sqsClient;
    private final ObjectMapper mapper;

    @Value("${sqs.listener.url}")
    private String queueUrl;

    @Value("${sqs.listener.max-messages:10}")
    private Integer maxMessages;

    @Value("${sqs.listener.wait-time-seconds:20}")
    private Integer waitTimeSeconds;

    @Value("${sqs.listener.visibility-timeout:100}")
    private Integer visibilityTimeout;

    @Override
    public List<Submission> getSubmissions() {

        ReceiveMessageResponse resp = sqsClient.receiveMessage(r -> r
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(waitTimeSeconds)
                .visibilityTimeout(visibilityTimeout)
        );

        List<Message> messages = resp.messages();

        return messages.stream().map(
                (msg) -> {
                    String messageBody = msg.body();
                    String receiptHandle = msg.receiptHandle();

                    RequestSubmissionDTO dto;
                    try {
                        dto = mapper.readValue(messageBody, RequestSubmissionDTO.class);
                    } catch (JsonProcessingException error) {
                        throw new UndefinedException(error.getMessage(), error.getCause());
                    }

                    List<Test> tests = new ArrayList<>();
                    for (Map<String, String> test : dto.getTests()) {
                        tests.add(
                                Test
                                        .builder()
                                        .input(test.get("input"))
                                        .output(test.get("output"))
                                        .build()
                        );
                    }

                    return Submission
                            .builder()
                            .code(dto.getCode())
                            .submissionId(dto.getSubmissionId())
                            .receiptHandle(receiptHandle)
                            .codeCompiler(new CodeCompiler(dto.getCodeCompiler()))
                            .timeLimit(new TimeLimit(dto.getTimeLimitMS()))
                            .memoryLimit(new MemoryLimit(dto.getMemoryLimitKB()))
                            .tests(tests)
                            .build();
                }
        ).toList();
    }

    @Override
    public void deleteSubmission(Submission submission) {
        sqsClient.deleteMessage(d -> d
                .queueUrl(queueUrl)
                .receiptHandle(submission.getReceiptHandle())
        );
    }

}


