package com.felix.bookmark_app.dto;

import com.felix.bookmark_app.model.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CollectionCreateDTO {

    @NotNull(message = "Name can not be empty")
    @Size(max = 100, message = "Name must shorter than 100 characters")
    private String name;

    @NotNull(message = "Choose a category")
    private Category category;

    private String description;

    @NotNull(message = "Choose an option")
    private boolean visible;
}
