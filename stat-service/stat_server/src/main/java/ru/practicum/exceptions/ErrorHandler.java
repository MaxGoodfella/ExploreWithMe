package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({TimeFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTimeFormatException(TimeFormatException e) {
        log.debug("Time format validation failed. 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}