package ru.practicum.exceptions;

public class DataConflictException extends RuntimeException {

    public DataConflictException(Class<?> entityClass, String entityId, String message) {
        super(String.format("Data conflict found for Entity '%s' with ID '%s'. '%s'",
                entityClass.getSimpleName(), entityId, message));
    }

}