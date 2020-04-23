package ru.restaurant;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException() {

    }
}
