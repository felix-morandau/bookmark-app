package com.felix.bookmark_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "bookmark")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "link_id", nullable = false)
    private Link link;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "description")
    private String description;

    @Column(name = "secure", nullable = false)
    private boolean secure = false;

    @ManyToMany
    @JoinTable(
            name = "category_bookmark",
            joinColumns =  @JoinColumn(name = "bookmark_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
