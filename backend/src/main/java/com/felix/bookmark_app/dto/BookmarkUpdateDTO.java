package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.Category;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookmarkUpdateDTO {
    @Size(max = 100, message = "Title must be shorter than 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    private Category category;
}
