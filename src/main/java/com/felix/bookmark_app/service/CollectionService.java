package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoCollectionFoundException;
import com.felix.bookmark_app.dto.CollectionCreateDTO;
import com.felix.bookmark_app.dto.CollectionUpdateDTO;
import com.felix.bookmark_app.model.Category;
import com.felix.bookmark_app.model.Collection;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.CollectionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Data
@AllArgsConstructor
public class CollectionService {
    private CollectionRepository collectionRepository;
    private UserService userService;

    public List<Collection> getCollections(Boolean visible) {

        if (visible != null) {
            return collectionRepository.findCollectionsByVisible(visible);
        }

        return collectionRepository.findAll();
    }

    public Collection addCollection(CollectionCreateDTO collectionCreateDTO, String username) {
        User user = userService.getUserByUsername(username);

        Collection collection = new Collection();

        collection.setName(collectionCreateDTO.getName());
        collection.setCategory(collectionCreateDTO.getCategory());
        collection.setDescription(collectionCreateDTO.getDescription());
        collection.setCreator(username);
        collection.setVisible(collectionCreateDTO.isVisible());

        collection.setCreator(username);
        user.getSavedCollections().add(collection);

        return collectionRepository.save(collection);
    }

    public Collection updateCollection(UUID id, CollectionUpdateDTO collectionUpdateDTO, String username) {
        Collection collection = collectionRepository.findById(id).orElseThrow(
                NoCollectionFoundException::new
        );

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
        Collection collection = collectionRepository.findById(id).orElseThrow(
                NoCollectionFoundException::new
        );

        if (!collection.getCreator().equals(username)) {
            throw new IllegalStateException("No permission");
        }

        collectionRepository.deleteById(id);
    }

    public List<Collection> getCollectionsByCategory(Category category) {
        return collectionRepository.findByCategory(category);
    }

    public List<Collection> getSavedCollectionsByUser(String username) {
        User user = userService.getUserByUsername(username);
        return collectionRepository.findSavedCollectionsByUserId(user.getId());
    }

    public List<Collection> getPublicCollections() {
        return collectionRepository.findCollectionsByVisible(true);
    }
}
