package ru.hogwarts.school.model.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.hogwarts.school.service.StudentService;

@ControllerAdvice
public class HogwartsExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HogwartsExceptionHandler.class);

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<String> handleInvalidValueException() {
        return ResponseEntity.badRequest().body("Переданы некорректные данные!");
    }

    @ExceptionHandler(EmptyStorageException.class)
    public ResponseEntity<String> handleEmptyStorageException() {
        return new ResponseEntity<>("В хранилище нет данных!", HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NoMatchingResultsException.class)
    public ResponseEntity<String> handleNoMatchingResultsException(NoMatchingResultsException e) {
        logger.warn(e.getMessage(), e);
        ResponseStatus status = e.getClass().getAnnotation(ResponseStatus.class);
        return new ResponseEntity<>(e.getMessage(), status.code());
    }
}
