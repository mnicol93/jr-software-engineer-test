package com.adobe.error;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(String id) {
    super("Order " + id + " not found");
  }
}
