package com.felix.bookmark_app.repository;

import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.Category;
import com.felix.bookmark_app.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {
    Optional<Collection> findById(UUID id);

    List<Collection> findByCreator(String creator);

    List<Collection> findByCategory(Category category);

    List<Collection> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Collection c JOIN c.bookmarks b WHERE b.id = :bookmarkId")
    List<Collection> findCollectionsByBookmarkId(UUID bookmarkId);

    List<Collection> findCollectionsByVisible(Boolean visible);
}
