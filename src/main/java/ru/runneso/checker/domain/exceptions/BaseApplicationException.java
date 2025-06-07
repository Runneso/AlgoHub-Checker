package ru.runneso.checker.domain.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseApplicationException extends RuntimeException {

    public BaseApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseApplicationException(String message) {
        super(message);
    }
}

