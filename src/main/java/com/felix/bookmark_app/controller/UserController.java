package com.felix.bookmark_app.controller;

import com.felix.bookmark_app.dto.UserCreateDTO;
import com.felix.bookmark_app.dto.UserUpdateDTO;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.model.UserType;
import com.felix.bookmark_app.repository.UserRepository;
import com.felix.bookmark_app.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @GetMapping
    public List<User> getUsers(
            @RequestParam(required = false) UserType type
    ) {
        return userService.getUsers(type);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PostMapping
    public User addUser( @Valid @RequestBody UserCreateDTO userCreateDTO) {
        return userService.addUser(userCreateDTO);
    }

    @PostMapping("/{username}")
    public User updateUser(@PathVariable String username, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(username, userUpdateDTO);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }
}
