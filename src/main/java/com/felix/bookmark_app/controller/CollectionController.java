package com.felix.bookmark_app.controller;

import com.felix.bookmark_app.dto.CollectionCreateDTO;
import com.felix.bookmark_app.dto.CollectionUpdateDTO;
import com.felix.bookmark_app.model.Category;
import com.felix.bookmark_app.model.Collection;
import com.felix.bookmark_app.service.CollectionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/collections")
    public ResponseEntity<List<Collection>> getCollections(
            @RequestParam(required = false) Boolean visible
    ) {
        List<Collection> collections = collectionService.getCollections(visible);
        return ResponseEntity.ok(collections);
    }

    @PostMapping("{username}/collections/add_collection/")
    public ResponseEntity<Collection> addCollection(
            @PathVariable String username,
            @RequestBody CollectionCreateDTO collectionCreateDTO
    ) {
        Collection newCollection = collectionService.addCollection(collectionCreateDTO, username);
        return new ResponseEntity<>(newCollection, HttpStatus.CREATED);
    }

    @PutMapping("{username}/collections/collection/update/{id}")
    public ResponseEntity<Collection> updateCollection(
            @PathVariable UUID id,
            @PathVariable String username,
            @RequestBody CollectionUpdateDTO collectionUpdateDTO
    ) {
        Collection updatedCollection = collectionService.updateCollection(id, collectionUpdateDTO, username);
        return ResponseEntity.ok(updatedCollection);
    }

    @DeleteMapping("{username}/collections/collection/delete/{id}")
    public ResponseEntity<Void> deleteCollection(
            @PathVariable UUID id,
            @PathVariable String username
    ) {
        collectionService.deleteCollection(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/collections/category")
    public ResponseEntity<List<Collection>> getCollectionsByCategory(
            @RequestParam Category category
    ) {
        List<Collection> collections = collectionService.getCollectionsByCategory(category);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{username}/collections/saved")
    public ResponseEntity<List<Collection>> getSavedCollectionsByUser(
            @PathVariable String username
    ) {
        List<Collection> savedCollections = collectionService.getSavedCollectionsByUser(username);
        return ResponseEntity.ok(savedCollections);
    }

    @GetMapping("collections/public")
    public ResponseEntity<List<Collection>> getPublicCollections() {
        List<Collection> publicCollections = collectionService.getPublicCollections();
        return ResponseEntity.ok(publicCollections);
    }
}
