package ru.hogwarts.school.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HogwartsExceptionHandler {
    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<String> handleInvalidValueException() {
        return ResponseEntity.badRequest().body("Переданы некорректные данные!");
    }

    @ExceptionHandler(EmptyStorageException.class)
    public ResponseEntity<String> handleEmptyStorageException() {
        return new ResponseEntity<>("В хранилище нет данных!", HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoMatchingResultsException.class)
    public ResponseEntity<String> handleNoMatchingResultsException() {
        return new ResponseEntity<>("Поиск по параметрам не дал результата", HttpStatus.NOT_FOUND);
    }
}
