package ru.hogwarts.school.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoMatchingResultsException extends RuntimeException {
    public NoMatchingResultsException() {
        super("Поиск по параметрам не дал результата");
    }
}
