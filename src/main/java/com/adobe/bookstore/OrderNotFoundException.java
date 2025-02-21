package com.adobe.bookstore;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Order " + id + " not found");
    }
}
