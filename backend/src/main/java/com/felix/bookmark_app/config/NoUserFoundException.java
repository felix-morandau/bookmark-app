package com.felix.bookmark_app.config;

public class NoUserFoundException extends RuntimeException{

    public NoUserFoundException() {
        super("User not found.");
    }

}
