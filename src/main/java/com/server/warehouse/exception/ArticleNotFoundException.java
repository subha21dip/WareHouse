package com.server.warehouse.exception;

public class ArticleNotFoundException extends Exception{
    public ArticleNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
