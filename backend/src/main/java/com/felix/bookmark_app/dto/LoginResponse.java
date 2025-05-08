package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.UserType;
import lombok.Data;

public record LoginResponse(
        boolean valid,
        UserType role,
        String errorMessage,
        String token
) {
}
