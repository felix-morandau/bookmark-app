package com.felix.bookmark_app.repository;

import com.felix.bookmark_app.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    Optional<Bookmark> findBookmarkById(UUID id);

    List<Bookmark> findByCategory(Category category);

    List<Bookmark> findByTitle(String title);

    @Query("SELECT b FROM Bookmark b JOIN Link l WHERE l.secure = true")
    List<Bookmark> findBySecureLinks();

    Optional<Bookmark> findByLink(Link link);
}
