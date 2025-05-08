package com.felix.bookmark_app.controller;

import com.felix.bookmark_app.dto.LoginRequest;
import com.felix.bookmark_app.dto.LoginResponse;
import com.felix.bookmark_app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@AllArgsConstructor
@CrossOrigin
public class LoginController {
    private UserService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest.username(), loginRequest.password());

        if (loginResponse.valid()) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(UNAUTHORIZED.value()).body(loginResponse);
        }
    }
}
