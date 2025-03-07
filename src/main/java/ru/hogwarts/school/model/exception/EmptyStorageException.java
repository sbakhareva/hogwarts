package ru.hogwarts.school.model.exception;

import java.util.NoSuchElementException;

public class EmptyStorageException extends NoSuchElementException {
    public EmptyStorageException() {
        super();
    }

    public EmptyStorageException(String message) {
        super(message);
    }

    public EmptyStorageException(Throwable t) {
        super(t);
    }
    public EmptyStorageException(String message, Throwable t) {
        super(message, t);
    }
}
