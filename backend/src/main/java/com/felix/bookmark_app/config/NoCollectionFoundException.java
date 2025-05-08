package com.felix.bookmark_app.config;

public class NoCollectionFoundException extends RuntimeException{

    public NoCollectionFoundException() {
        super("Collection not found");
    }
}
