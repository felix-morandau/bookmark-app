package com.felix.bookmark_app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "URL", nullable = false, unique = true)
    private String url;

    @Column(name = "secure")
    private boolean secure = false;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Bookmark> bookmarks = new ArrayList<>();

    public Link(String url) {
        String regex = "^(https://)(.)*";

        this.url = url;
        this.secure = url.matches(regex);
    }
}