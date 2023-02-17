package ru.practicum.core.exceptions;

public class NotOwnerException extends RuntimeException {

    public NotOwnerException(String message) {
        super(message);
    }

}
