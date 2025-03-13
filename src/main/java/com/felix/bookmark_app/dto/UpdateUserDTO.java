package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.User;

import java.util.UUID;

public interface UpdateUserDTO {
    public User update(UUID id);
}
