package com.server.warehouse.exception;

public class ArticleQuantityException extends Exception{

    public ArticleQuantityException(String errorMessage) {
        super(errorMessage);
    }
}
