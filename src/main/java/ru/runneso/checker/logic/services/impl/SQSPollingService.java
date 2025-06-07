package ru.runneso.checker.logic.services.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import ru.runneso.checker.domain.entities.Submission;
import ru.runneso.checker.domain.exceptions.UndefinedException;
import ru.runneso.checker.infra.repositories.inter.ReaderRepository;
import ru.runneso.checker.settings.aop.HandleServiceExceptions;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SQSPollingService {
    private final ReaderRepository readerRepository;

    private final ThreadPoolTaskExecutor sqsExecutor;

    private final JudgeService judgeService;

    @PostConstruct
    public void startPolling() {
        Thread poller = new Thread(this::pollLoop, "sqs-poller");
        poller.setDaemon(true);
        poller.start();
    }

    @HandleServiceExceptions
    private void pollLoop() {
        while (true) {
            List<Submission> submissions = readerRepository.getSubmissions();
            if (submissions.isEmpty()) {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException error) {
                    throw new UndefinedException(error.getMessage(), error.getCause());
                }
                continue;
            }
            for (Submission submission : submissions) {
                sqsExecutor.submit(
                        () -> {
                            judgeService.judge(submission);
                            readerRepository.deleteSubmission(submission);
                        }
                );
            }
        }
    }

    @PreDestroy
    public void stopPolling() {
        sqsExecutor.shutdown();
    }
}
