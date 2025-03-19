package com.felix.bookmark_app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
@Table(name = "collection")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "Category", nullable = false)
    private Category category;

    @ManyToMany(mappedBy = "collections")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Column(name = "creator", nullable = false)
    private String creator;

    @Column(name = "public", nullable = false)
    private boolean visible;

    @ManyToMany(mappedBy = "savedCollections", cascade = CascadeType.ALL)
    private Set<User> savedByUsers = new HashSet<>();
}
