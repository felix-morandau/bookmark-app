package com.felix.bookmark_app.repository;

import com.felix.bookmark_app.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    Optional<Link> findByUrl(String url);
}
