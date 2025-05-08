package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoCollectionFoundException;
import com.felix.bookmark_app.dto.CollectionCreateDTO;
import com.felix.bookmark_app.dto.CollectionUpdateDTO;
import com.felix.bookmark_app.model.Category;
import com.felix.bookmark_app.model.Collection;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.repository.BookmarkRepository;
import com.felix.bookmark_app.repository.CollectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.print.Book;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CollectionService collectionService;

    private Collection testCollection;
    private Bookmark testBookmark;
    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCollection = new Collection();
        testCollection.setId(UUID.randomUUID());
        testCollection.setName("Test Collection");
        testCollection.setCategory(Category.TECHNOLOGY);
        testCollection.setDescription("A sample collection");
        testCollection.setCreator(testUsername);
        testCollection.setVisible(true);
        testCollection.setBookmarks(new ArrayList<>());

        testBookmark = new Bookmark();
        testBookmark.setId(UUID.randomUUID());
        testBookmark.setTitle("Test Bookmark");
    }

    @Test
    void getCollections() {
        when(collectionRepository.findAll()).thenReturn(List.of(testCollection));

        List<Collection> result1 = collectionService.getCollections(true, "TECHNOLOGY", testUsername);
        assertEquals(1, result1.size());

        List<Collection> result2 = collectionService.getCollections(false, "TECHNOLOGY", testUsername);
        assertEquals(0, result2.size());

        List<Collection> result3 = collectionService.getCollections(true, "", "");
        assertEquals(1, result3.size());

        List<Collection> result4 = collectionService.getCollections(null, null, null);
        assertEquals(1, result4.size());

        verify(collectionRepository, times(4)).findAll();
    }

    @Test
    void addCollection() {
        CollectionCreateDTO dto = new CollectionCreateDTO("New Collection", Category.TECHNOLOGY, "A new tech collection", true);
        when(userService.getUserByUsername(testUsername)).thenReturn(new com.felix.bookmark_app.model.User());
        Collection savedCollection = new Collection();
        savedCollection.setId(UUID.randomUUID());
        savedCollection.setName(dto.getName());
        savedCollection.setCategory(Category.valueOf(dto.getCategory().toString().toUpperCase()));
        savedCollection.setDescription(dto.getDescription());
        savedCollection.setVisible(dto.isVisible());
        savedCollection.setCreator(testUsername);

        when(collectionRepository.save(any(Collection.class))).thenReturn(savedCollection);

        Collection result = collectionService.addCollection(dto, testUsername);
        assertNotNull(result.getId());
        assertEquals("New Collection", result.getName());
        verify(userService, times(1)).getUserByUsername(testUsername);
        verify(collectionRepository, times(1)).save(any(Collection.class));
    }

    @Test
    void updateCollection() {
        UUID collectionId = testCollection.getId();
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(testCollection));

        // Create update DTO.
        CollectionUpdateDTO updateDTO = new CollectionUpdateDTO();
        updateDTO.setName("Updated Collection");
        updateDTO.setVisible(false);
        updateDTO.setCategory(Category.DESIGN);
        updateDTO.setDescription("Updated description");

        when(collectionRepository.save(testCollection)).thenReturn(testCollection);

        testCollection.setCreator(testUsername);
        Collection updated = collectionService.updateCollection(collectionId, updateDTO, testUsername);

        assertEquals("Updated Collection", updated.getName());
        assertFalse(updated.isVisible());
        assertEquals(Category.DESIGN.toString().toUpperCase(), updated.getCategory().name());
        assertEquals("Updated description", updated.getDescription());
        verify(collectionRepository, times(1)).findById(collectionId);
        verify(collectionRepository, times(1)).save(testCollection);
    }

    @Test
    void deleteCollection() {
        UUID collectionId = testCollection.getId();
        testCollection.setCreator(testUsername);
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(testCollection));
        doNothing().when(collectionRepository).deleteById(collectionId);

        collectionService.deleteCollection(collectionId, testUsername);
        verify(collectionRepository, times(1)).deleteById(collectionId);
    }

    @Test
    void addBookmarkToCollection() {
        List<Bookmark> testBookmarks = new ArrayList<>();
        testBookmarks.add(testBookmark);

        testCollection.setBookmarks(new ArrayList<>());
        testCollection.setCreator(testUsername);
        when(collectionRepository.findById(testCollection.getId())).thenReturn(Optional.of(testCollection));

        Collection result = collectionService.addBookmarkToCollection(testCollection.getId(), testBookmarks.stream().map(Bookmark::getId).collect(Collectors.toCollection(ArrayList::new)));

        assertEquals(1, result.getBookmarks().size());
        assertEquals(testBookmark.getId(), result.getBookmarks().get(0).getId());
        verify(collectionRepository, times(1)).findById(testCollection.getId());
        verify(collectionRepository, times(1)).save(any(Collection.class));
    }

    @Test
    void getCollectionsByCategory() {
        Category category = Category.TECHNOLOGY;
        List<Collection> collections = List.of(testCollection);
        when(collectionRepository.findByCategory(category)).thenReturn(collections);

        List<Collection> result = collectionService.getCollectionsByCategory(category);
        assertEquals(1, result.size());
        verify(collectionRepository, times(1)).findByCategory(category);
    }

    @Test
    void getPublicCollections() {
        List<Collection> publicCollections = List.of(testCollection);
        when(collectionRepository.findCollectionsByVisible(true)).thenReturn(publicCollections);

        List<Collection> result = collectionService.getPublicCollections();
        assertEquals(1, result.size());
        verify(collectionRepository, times(1)).findCollectionsByVisible(true);
    }
}
