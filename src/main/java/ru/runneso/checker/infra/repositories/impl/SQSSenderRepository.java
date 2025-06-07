package ru.runneso.checker.infra.repositories.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.runneso.checker.entry.dto.schemas.ResponseSubmissionDTO;
import ru.runneso.checker.infra.repositories.inter.SenderRepository;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@Repository
@RequiredArgsConstructor
public class SQSSenderRepository extends BaseQueueRepository implements SenderRepository {
    private final SqsClient sqsClient;
    private final ObjectMapper mapper;

    @Value("${sqs.writer.url}")
    private String queueUrl;


    @Override
    public void sendSubmission(ResponseSubmissionDTO submission) throws JsonProcessingException {
        String messageBody= mapper.writeValueAsString(submission);

        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        sqsClient.sendMessage(sendMsgRequest);
    }
}
