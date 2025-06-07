package ru.runneso.checker.entry.dto.schemas;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSubmissionDTO {
    private UUID submissionId;
    private String codeCompiler;
    private String code;
    private Integer timeLimitMS;
    private Integer memoryLimitKB;
    private List<Map<String,String>> tests;
}
