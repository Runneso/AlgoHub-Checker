package ru.runneso.checker.entry.dto.schemas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSubmissionDTO {
    private UUID submissionId;
    private String status;
}
