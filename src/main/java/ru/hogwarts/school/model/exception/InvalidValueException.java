package ru.hogwarts.school.model.exception;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException() {
        super();
    }

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(Throwable t) {
        super(t);
    }

    public InvalidValueException(String message, Throwable t) {
        super(message, t);
    }
}
