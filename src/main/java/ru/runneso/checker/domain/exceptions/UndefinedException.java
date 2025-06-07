package ru.runneso.checker.domain.exceptions;

public class UndefinedException extends BaseApplicationException {
    public UndefinedException(String message, Throwable cause) {
        super(message, cause);
    }
}
