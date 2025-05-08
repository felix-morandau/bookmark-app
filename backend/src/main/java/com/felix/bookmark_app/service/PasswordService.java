package com.felix.bookmark_app.service;

import com.felix.bookmark_app.dto.ForgotPasswordRequest;
import com.felix.bookmark_app.dto.ResetPasswordRequest;
import com.felix.bookmark_app.model.PasswordResetToken;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.PasswordResetTokenRepository;
import com.felix.bookmark_app.repository.UserRepository;
import com.felix.bookmark_app.util.PasswordUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PasswordService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final EmailService emailService;
    private final PasswordUtil passwordUtil;

    @Transactional
    public void sendResetLink(ForgotPasswordRequest req, String appUrl) {
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("No user with that email"));

        tokenRepo.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setToken(token);
        prt.setExpiryDate(Instant.now().plusSeconds(300));
        tokenRepo.save(prt);

        String resetUrl = appUrl + "/reset-password?token=" + token;
        String subject = "Bookmark App Password Reset";
        String body = "To reset your password, click the link below:\n" + resetUrl;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetToken prt = tokenRepo.findByToken(req.token())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (prt.getExpiryDate().isBefore(Instant.now())) {
            tokenRepo.delete(prt);
            throw new IllegalArgumentException("Reset token has expired");
        }

        User user = prt.getUser();
        user.setPassword(passwordUtil.hashPassword(req.newPassword()));
        userRepo.save(user);

        tokenRepo.delete(prt);
    }
}
