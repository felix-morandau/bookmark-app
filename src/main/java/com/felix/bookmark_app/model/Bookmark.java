package com.felix.bookmark_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "bookmark")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "URL", nullable = false, unique = true)
    private String url;

    private String description;

    private boolean secure;

    private
}
