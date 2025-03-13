package com.felix.bookmark_app.service;

import com.felix.bookmark_app.dto.UserCreateDTO;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User addUser(UserCreateDTO userDTO) {
        User newUser = new User();

        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(userDTO.getPassword());
        newUser.setEmail(userDTO.getEmail());
        newUser.setType(userDTO.getType());
        newUser.setBio(userDTO.getBio());

        return userRepository.save(newUser);
    }

    public User updateUser(String username, ) {

    }
}
