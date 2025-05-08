package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookmarkCreateDTO {
    @Size(max = 100, message = "Title must be shorter than 100 characters")
    @NotBlank
    @NotNull(message = "Title is required")
    private String title;

    @NotNull(message = "URL is required")
    private String url;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Category is required")
    private Category category;
}

