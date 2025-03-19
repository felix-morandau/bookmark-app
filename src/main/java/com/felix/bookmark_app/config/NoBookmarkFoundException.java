package com.felix.bookmark_app.config;

public class NoBookmarkFoundException extends RuntimeException{

    public NoBookmarkFoundException() {
        super("No bookmark was found");
    }
}
