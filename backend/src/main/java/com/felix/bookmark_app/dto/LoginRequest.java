package com.felix.bookmark_app.dto;

public record LoginRequest(
        String username,
        String password
) {
}
