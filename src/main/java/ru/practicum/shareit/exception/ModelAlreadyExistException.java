package ru.practicum.shareit.exception;

public class ModelAlreadyExistException extends RuntimeException {
    public ModelAlreadyExistException(String s) {
        super(s);
    }
}