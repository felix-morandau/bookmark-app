package com.felix.bookmark_app.controller;

import com.felix.bookmark_app.dto.ForgotPasswordRequest;
import com.felix.bookmark_app.dto.ResetPasswordRequest;
import com.felix.bookmark_app.service.PasswordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String appUrl = "http://localhost:5173";
        passwordService.sendResetLink(request, appUrl);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
