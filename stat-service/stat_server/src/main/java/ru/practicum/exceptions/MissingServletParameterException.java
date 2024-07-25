package ru.practicum.exceptions;

public class MissingServletParameterException extends RuntimeException {

    public MissingServletParameterException(String parameterName) {
        super(String.format("Required request parameter '%s' is not present", parameterName));
    }

}