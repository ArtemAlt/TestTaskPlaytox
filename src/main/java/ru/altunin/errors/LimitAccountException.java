package ru.altunin.errors;

public class LimitAccountException extends Exception {
    public LimitAccountException(String message) {
        super(message);
    }
}
