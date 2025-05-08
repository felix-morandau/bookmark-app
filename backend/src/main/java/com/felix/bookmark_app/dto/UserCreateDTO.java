package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "Username required")
    private String username;

    @NotBlank(message = "Password required")
    @Size(min = 8, message = "Password must have at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-z\\d!@#$%^&*()_+]{8,}$",
            message = "Password must contain at least: one uppercase character, one lowercase character, one digit, one special character"
    )
    private String password;

    @NotBlank(message = "Email required")
    private String email;

    @NotNull(message = "Choose an option")
    private UserType type;

    private String profilePhotoURL;

    private String bio;
}
