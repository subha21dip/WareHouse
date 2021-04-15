package com.server.warehouse.exception;

public class ProductDetailsNotFoundException extends Exception {

    public ProductDetailsNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
