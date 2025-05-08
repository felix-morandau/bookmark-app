package com.felix.bookmark_app.dto;

public record ResetPasswordRequest(String token, String newPassword) {
}
