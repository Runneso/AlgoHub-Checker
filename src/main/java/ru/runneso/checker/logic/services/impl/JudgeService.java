package ru.runneso.checker.logic.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.runneso.checker.domain.entities.Submission;
import ru.runneso.checker.domain.entities.Test;
import ru.runneso.checker.domain.exceptions.UndefinedException;
import ru.runneso.checker.domain.exceptions.ValidationException;
import ru.runneso.checker.domain.values.CodeCompiler;
import ru.runneso.checker.entry.dto.schemas.ResponseSubmissionDTO;
import ru.runneso.checker.infra.repositories.inter.SenderRepository;
import ru.runneso.checker.settings.aop.HandleServiceExceptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JudgeService {
    private final SenderRepository senderRepository;

    private static final Path TMP_DIR;


    static {
        try {
            TMP_DIR = Paths.get(System.getProperty("java.io.tmpdir"))
                    .resolve("checker");
            Files.createDirectories(TMP_DIR);
        } catch (IOException e) {
            throw new UndefinedException(e.getMessage(), e.getCause());
        }
    }

    @HandleServiceExceptions
    public void judge(Submission submission) {
        UUID id = submission.getSubmissionId();
        CodeCompiler codeCompiler = submission.getCodeCompiler();
        String ext = codeCompiler.getExtension();

        Path srcFile = TMP_DIR.resolve(id + ext);

        Path script;
        try {
            script = Files.writeString(srcFile, submission.getCode(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException error) {
            throw new UndefinedException(error.getMessage(), error.getCause());
        }

        String status;
        switch (codeCompiler.getAsGenericType()) {
            case "python3.10" -> {
                status = runPython(script, submission);
            }
            default -> {
                throw new ValidationException("Invalid code compiler " + codeCompiler.getAsGenericType());
            }
        }

        ResponseSubmissionDTO dto = ResponseSubmissionDTO
                .builder()
                .submissionId(submission.getSubmissionId())
                .status(status)
                .build();

        try {
            senderRepository.sendSubmission(dto);
        }catch (JsonProcessingException error){
            throw new UndefinedException(error.getMessage(), error.getCause());
        }

        try {
            Files.deleteIfExists(script);
        }catch (IOException error){
            throw new UndefinedException(error.getMessage(), error.getCause());
        }
    }

    private String runPython(
            Path script,
            Submission submission
    ) {
        Integer testCount = submission.getTests().size();
        String status = "OK";
        for (int index = 0; index < testCount; index++) {
            Test test = submission.getTests().get(index);
            String statusPerTest;
            try {
                statusPerTest = runOnePythonTest(
                        script,
                        test.getInput(),
                        test.getOutput(),
                        submission.getTimeLimit().getAsGenericType(),
                        submission.getMemoryLimit().getAsGenericType()
                );
            } catch (IOException | InterruptedException error) {
                throw new UndefinedException(error.getMessage(), error.getCause());
            }

            if (!statusPerTest.equals("OK")) {
                status = statusPerTest + (index + 1);
                break;
            }
        }

        return status;
    }


    private String runOnePythonTest(
            Path script,
            String input,
            String expected,
            Integer timeLimitMS,
            Integer memoryLimitKB
    ) throws IOException, InterruptedException {
        Path workDir = script.getParent();
        UUID id = UUID.randomUUID();
        Path inFile = Files.createTempFile(workDir, "stdin-" + id, ".txt");
        Files.writeString(inFile, input, StandardCharsets.UTF_8);

        String timeoutSec = String.format(Locale.US, "%.3f", timeLimitMS / 1000.0);
        String cmd = String.format(
                "ulimit -v %d; timeout %s python3 %s < %s",
                memoryLimitKB,
                timeoutSec,
                script,
                inFile);

        Process proc = new ProcessBuilder("sh", "-c", cmd)
                .directory(workDir.toFile())
                .redirectErrorStream(true)
                .start();

        String output = new String(proc.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Integer exit = proc.waitFor();

        Files.deleteIfExists(inFile);
        if (exit == 124) return "TL";
        if (exit == 137) return "ML";
        if (exit != 0 && output.contains("MemoryError")) return "ML";
        if (exit != 0) return "RE";

        return output.trim().equals(expected.trim()) ? "OK" : "WA";
    }
}
