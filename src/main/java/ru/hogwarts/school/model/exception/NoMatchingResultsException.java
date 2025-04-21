package ru.hogwarts.school.model.exception;

import java.util.NoSuchElementException;

public class NoMatchingResultsException extends RuntimeException {
    public NoMatchingResultsException() {
        super();
    }

    public NoMatchingResultsException(String message) {
        super(message);
    }

    public NoMatchingResultsException(Throwable t) {
        super(t);
    }

    public NoMatchingResultsException(String message, Throwable t) {
        super(message, t);
    }
}
