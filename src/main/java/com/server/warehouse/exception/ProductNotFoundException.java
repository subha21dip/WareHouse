package com.server.warehouse.exception;

public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
