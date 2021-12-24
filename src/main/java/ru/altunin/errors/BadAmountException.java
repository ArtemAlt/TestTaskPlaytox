package ru.altunin.errors;

public class BadAmountException extends Exception {
    public BadAmountException(String message) {
        super(message);
    }
}
