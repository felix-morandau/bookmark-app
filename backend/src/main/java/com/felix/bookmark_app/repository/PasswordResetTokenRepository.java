package com.felix.bookmark_app.repository;

import com.felix.bookmark_app.model.PasswordResetToken;
import com.felix.bookmark_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
