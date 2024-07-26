package ru.practicum.exceptions;

public class IllegalArgumentException extends RuntimeException {

    public IllegalArgumentException(String parameterName, String message) {
        super(String.format("Invalid value for parameter '%s': %s", parameterName, message));
    }

}