package com.server.warehouse.exception;

public class ProductCountException extends Exception{
    public ProductCountException(String errorMessage) {
        super(errorMessage);
    }
}
