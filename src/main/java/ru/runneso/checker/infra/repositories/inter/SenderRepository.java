package ru.runneso.checker.infra.repositories.inter;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.runneso.checker.entry.dto.schemas.ResponseSubmissionDTO;

public interface SenderRepository {
    void sendSubmission(ResponseSubmissionDTO submission) throws JsonProcessingException;
}
