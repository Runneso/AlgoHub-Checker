package ru.runneso.checker.domain.entities;


import lombok.*;
import ru.runneso.checker.domain.values.CodeCompiler;
import ru.runneso.checker.domain.values.MemoryLimit;
import ru.runneso.checker.domain.values.TimeLimit;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission extends BaseEntity{
    private UUID submissionId;
    private String receiptHandle;
    private String code;
    private CodeCompiler codeCompiler;
    private TimeLimit timeLimit;
    private MemoryLimit memoryLimit;
    private List<Test> tests;
}
