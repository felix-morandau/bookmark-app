package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class UpdatePasswordDTO implements UpdateUserDTO{
    private UserRepository userRepository;

    public User update(UUID id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()) {
            throw new NoSuchElementException();
        }

        user.
    }
}
