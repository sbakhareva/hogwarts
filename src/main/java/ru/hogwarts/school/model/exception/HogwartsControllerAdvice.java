package ru.hogwarts.school.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class HogwartsControllerAdvice {
    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<String> handleInvalidValueException() {
        return ResponseEntity.badRequest().body("Переданы некорректные данные!");
    }

    @ExceptionHandler(EmptyStorageException.class)
    public ResponseEntity<String> handleEmptyStorageException() {
        return ResponseEntity.badRequest().body("В хранилище нет данных!");
    }
}
