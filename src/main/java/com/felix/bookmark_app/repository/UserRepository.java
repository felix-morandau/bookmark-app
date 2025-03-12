package com.felix.bookmark_app.repository;

import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findUserByUsername(String username);

    List<User> findUsersByTypeOrderByUsername(UserType type);

    @Query("SELECT u FROM User u WHERE SIZE(u.collections) > 20")
    List<User> findSuperUsers();
}
