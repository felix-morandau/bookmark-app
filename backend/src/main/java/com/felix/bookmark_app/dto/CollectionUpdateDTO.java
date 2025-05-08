package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.Category;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollectionUpdateDTO {

    @Size(max = 100, message = "Name must shorter than 100 characters")
    private String name;

    private Category category;

    private String description;

    private Boolean visible;
}
