package ru.practicum.timeformatvalidation;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exceptions.TimeFormatException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@RestControllerAdvice
public class TimeFormatValidator {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public LocalDateTime parseToLocalDateTime(String time) {
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(TIME_FORMAT));
        } catch (DateTimeParseException e) {
            throw new TimeFormatException("Time format is incorrect.");
        }
    }

}