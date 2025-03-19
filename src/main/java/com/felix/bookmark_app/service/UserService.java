package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoUserFoundException;
import com.felix.bookmark_app.dto.UserCreateDTO;
import com.felix.bookmark_app.dto.UserUpdateDTO;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.model.UserType;
import com.felix.bookmark_app.repository.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Data
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers(UserType type) {

        if (type != null) {
            return userRepository.findUsersByTypeOrderByUsername(type);
        }

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

    public User updateUser(String username, UserUpdateDTO userDTO) {
        Optional<User> user = userRepository.findUserByUsername(username);

        if (user.isEmpty()) {
            throw new NoUserFoundException();
        }

        if (userDTO.getEmail() != null) {
            user.get().setEmail(userDTO.getEmail());
        }

        if (userDTO.getPassword() != null) {
            user.get().setPassword(userDTO.getPassword());
        }

        if (userDTO.getBio() != null) {
            user.get().setBio(userDTO.getBio());
        }

        return userRepository.save(user.get());
    }

    public void deleteUser(String username) {

        if (userRepository.findUserByUsername(username).isPresent()) {
            UUID id = userRepository.findUserByUsername(username).get().getId();
            userRepository.deleteById(id);
        }
    }

    public User getUserById(UUID id) {
        return userRepository.findUserById(id).orElseThrow(
                NoUserFoundException::new
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
                NoUserFoundException::new
        );
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(
                NoUserFoundException::new
        );
    }
}
