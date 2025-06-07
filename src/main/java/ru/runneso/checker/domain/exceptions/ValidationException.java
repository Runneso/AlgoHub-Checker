package ru.runneso.checker.domain.exceptions;

public class ValidationException extends BaseApplicationException {
    public ValidationException(String message) {
        super(message);
    }
}
