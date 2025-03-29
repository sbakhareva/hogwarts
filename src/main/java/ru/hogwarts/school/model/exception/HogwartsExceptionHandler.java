package ru.hogwarts.school.model.exception;

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
        return ResponseEntity.badRequest().body("В хранилище нет данных!");
    }
}
