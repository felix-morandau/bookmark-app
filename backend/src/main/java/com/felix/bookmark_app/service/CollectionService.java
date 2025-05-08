package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoCollectionFoundException;
import com.felix.bookmark_app.dto.CollectionCreateDTO;
import com.felix.bookmark_app.dto.CollectionUpdateDTO;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.Collection;
import com.felix.bookmark_app.model.Category;
import com.felix.bookmark_app.repository.CollectionRepository;
import com.felix.bookmark_app.repository.BookmarkRepository;
import com.felix.bookmark_app.service.strategy.ExportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserService userService;
    private final BookmarkRepository bookmarkRepository;
    private final Map<String, ExportStrategy> exportStrategies;

    public CollectionService(CollectionRepository collectionRepository, UserService userService, BookmarkRepository bookmarkRepository, Map<String, ExportStrategy> exportStrategies) {
        this.collectionRepository = collectionRepository;
        this.userService = userService;
        this.bookmarkRepository = bookmarkRepository;
        this.exportStrategies = exportStrategies;
    }

    public List<Collection> getCollections(Boolean visible, String category, String creator) {
        return collectionRepository.findAll().stream()
                .filter(c -> visible == null || c.isVisible() == visible)
                .filter(c -> category == null || category.trim().isEmpty() ||
                        (c.getCategory() != null && c.getCategory().toString().equalsIgnoreCase(category)))
                .filter(c -> creator == null || creator.trim().isEmpty() ||
                        (c.getCreator() != null && c.getCreator().equalsIgnoreCase(creator)))
                .collect(Collectors.toList());
    }

    public Collection addCollection(CollectionCreateDTO collectionCreateDTO, String username) {
        userService.getUserByUsername(username);
        Collection collection = new Collection();
        collection.setName(collectionCreateDTO.getName());
        collection.setCategory(collectionCreateDTO.getCategory());
        collection.setDescription(collectionCreateDTO.getDescription());
        collection.setCreator(username);
        collection.setVisible(collectionCreateDTO.isVisible());
        return collectionRepository.save(collection);
    }

    public Collection updateCollection(UUID id, CollectionUpdateDTO collectionUpdateDTO, String username) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(NoCollectionFoundException::new);
        if (!collection.getCreator().equals(username)) {
            throw new IllegalStateException("No permission");
        }
        if (collectionUpdateDTO.getName() != null) {
            collection.setName(collectionUpdateDTO.getName());
        }
        if (collectionUpdateDTO.getVisible() != null) {
            collection.setVisible(collectionUpdateDTO.getVisible());
        }
        if (collectionUpdateDTO.getCategory() != null) {
            collection.setCategory(collectionUpdateDTO.getCategory());
        }
        if (collectionUpdateDTO.getDescription() != null) {
            collection.setDescription(collectionUpdateDTO.getDescription());
        }
        return collectionRepository.save(collection);
    }

    public void deleteCollection(UUID id, String username) {
        Collection collection = collectionRepository.findById(id)
                .orElseThrow(NoCollectionFoundException::new);
        if (!collection.getCreator().equals(username)) {
            throw new IllegalStateException("No permission");
        }
        collectionRepository.deleteById(id);
    }

    public Collection addBookmarkToCollection(UUID collectionId, List<UUID> bookmarkIds) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(NoCollectionFoundException::new);

        for (UUID bookmarkId : bookmarkIds) {
            Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                    .orElseThrow();
            collection.getBookmarks().add(bookmark);
        }
        return collectionRepository.save(collection);
    }

    public List<Collection> getCollectionsByCreator(String creator) {

        return collectionRepository.findByCreator(creator);
    }

    public List<Bookmark> getBookmarksForCollection(UUID collectionId) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(NoCollectionFoundException::new);
        return collection.getBookmarks();
    }


    public List<Collection> getCollectionsByCategory(Category category) {
        return collectionRepository.findByCategory(category);
    }

    public String exportCollection(UUID collectionId, String format) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Collection not found: " + collectionId
                ));

        return Optional.ofNullable(exportStrategies.get(format.toLowerCase()))
                .map(strategy -> strategy.exportData(collection))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported export format: " + format
                ));
    }

    public List<Collection> getPublicCollections() {
        return collectionRepository.findCollectionsByVisible(true);
    }
}
