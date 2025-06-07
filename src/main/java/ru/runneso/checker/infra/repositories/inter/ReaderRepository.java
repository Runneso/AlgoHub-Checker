package ru.runneso.checker.infra.repositories.inter;

import ru.runneso.checker.domain.entities.Submission;

import java.util.List;

public interface ReaderRepository {
    List<Submission> getSubmissions();
    void deleteSubmission(Submission submission);
}
