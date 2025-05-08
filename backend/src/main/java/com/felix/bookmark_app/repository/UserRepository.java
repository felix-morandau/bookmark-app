package com.felix.bookmark_app.repository;

import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserById(UUID id);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    List<User> findUsersByTypeOrderByUsername(UserType type);

    Optional<User> findByEmail(String email);
}
