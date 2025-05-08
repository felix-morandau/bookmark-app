package com.felix.bookmark_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(min = 8, message = "Password must have at least 8 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-z\\d!@#$%^&*()_+]{8,}$",
            message = "Password must contain at least: one uppercase character, one lowercase character, one digit, one special character"
    )
    private String password;

    @Email(message = "Invalid email format.")
    private String email;

    private String profilePhotoURL;

    private String bio;
}
