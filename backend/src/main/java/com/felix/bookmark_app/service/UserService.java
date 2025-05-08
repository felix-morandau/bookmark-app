package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoUserFoundException;
import com.felix.bookmark_app.dto.LoginResponse;
import com.felix.bookmark_app.dto.UserCreateDTO;
import com.felix.bookmark_app.dto.UserUpdateDTO;
import com.felix.bookmark_app.model.Collection;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.model.UserType;
import com.felix.bookmark_app.repository.CollectionRepository;
import com.felix.bookmark_app.repository.UserRepository;
import com.felix.bookmark_app.util.JwtUtil;
import com.felix.bookmark_app.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CollectionRepository collectionRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public List<User> getUsers(UserType type) {
        if (type != null) {
            return userRepository.findUsersByTypeOrderByUsername(type);
        }
        return userRepository.findAll();
    }

    public User addUser(UserCreateDTO userDTO) {
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordUtil.hashPassword(userDTO.getPassword()));
        newUser.setEmail(userDTO.getEmail());
        newUser.setType(userDTO.getType());
        newUser.setBio(userDTO.getBio());
        newUser.setProfilePhotoURL(userDTO.getProfilePhotoURL());
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
            user.get().setPassword(passwordUtil.hashPassword(userDTO.getPassword()));
        }
        if (userDTO.getBio() != null) {
            user.get().setBio(userDTO.getBio());
        }
        if (userDTO.getProfilePhotoURL() != null) {
            user.get().setProfilePhotoURL(userDTO.getProfilePhotoURL());
        }
        return userRepository.save(user.get());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        userOpt.ifPresent(user -> userRepository.deleteById(user.getId()));
    }

    public LoginResponse login(String username, String password) {
        Optional<User> user = userRepository.findUserByUsername(username);

        if (user.isEmpty()) {
            return new LoginResponse(false, null, "User with username " + username + " not found", null);
        }

        String token = jwtUtil.generateToken(user.get());

        if (passwordUtil.checkPassword(password, user.get().getPassword())) {
            return new LoginResponse(true, user.get().getType(), null, token);
        } else {
            return new LoginResponse(false, null, "Incorrect password", null);
        }
    }

    public User getUserById(UUID id) {
        return userRepository.findUserById(id).orElseThrow(NoUserFoundException::new);
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(NoUserFoundException::new);
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(NoUserFoundException::new);
    }

    public List<Collection> getSavedCollectionsByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getSavedCollections();
    }

    public User saveCollection(String username, UUID collectionId) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(NoUserFoundException::new);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow();
        user.getSavedCollections().add(collection);
        return userRepository.save(user);
    }

    public void removeSavedCollection(String username, UUID collectionId) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(NoUserFoundException::new);
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow();
        user.getSavedCollections().remove(collection);
        userRepository.save(user);
    }
}
