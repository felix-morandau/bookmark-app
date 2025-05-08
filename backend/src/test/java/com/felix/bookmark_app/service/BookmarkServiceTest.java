package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoBookmarkFoundException;
import com.felix.bookmark_app.dto.BookmarkCreateDTO;
import com.felix.bookmark_app.dto.BookmarkUpdateDTO;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.Category;
import com.felix.bookmark_app.model.Link;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.BookmarkRepository;
import com.felix.bookmark_app.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookmarkService bookmarkService;

    private User testUser;
    private Link testLink;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setSavedCollections(new ArrayList<>());
        testUser.setBookmarks(new ArrayList<>());

        testLink = new Link();
        testLink.setId(UUID.randomUUID());
        testLink.setUrl("http://example.com");
    }

    @Test
    void getAllBookmarks() {
        List<Bookmark> bookmarks = List.of(new Bookmark(), new Bookmark(), new Bookmark());
        when(bookmarkRepository.findAll()).thenReturn(bookmarks);

        List<Bookmark> result = bookmarkService.getAllBookmarks();
        assertEquals(3, result.size());
        verify(bookmarkRepository, times(1)).findAll();
    }

    @Test
    void addBookmark() {
        BookmarkCreateDTO dto = new BookmarkCreateDTO("Test Bookmark", "http://example.com", "Description", Category.TECHNOLOGY);

        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(linkRepository.findByUrl(dto.getUrl())).thenReturn(Optional.empty());
        when(linkRepository.save(any(Link.class))).thenAnswer(invocation -> {
            Link link = invocation.getArgument(0);
            link.setId(UUID.randomUUID());
            return link;
        });
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bookmark result = bookmarkService.addBookmark(dto, "testuser");

        assertNotNull(result.getId());
        assertEquals("Test Bookmark", result.getTitle());
        assertEquals("Description", result.getDescription());
        assertNotNull(result.getLink());
        assertEquals(dto.getUrl(), result.getLink().getUrl());

        assertTrue(testUser.getBookmarks().contains(result));

        verify(userService, times(1)).getUserByUsername("testuser");
        verify(linkRepository, times(1)).findByUrl(dto.getUrl());
        verify(linkRepository, times(1)).save(any(Link.class));
        verify(userService, times(1)).saveUser(testUser);
    }

    @Test
    void updateBookmark() {
        UUID bookmarkId = UUID.randomUUID();
        Bookmark existingBookmark = new Bookmark();
        existingBookmark.setId(bookmarkId);
        existingBookmark.setTitle("Old Title");
        existingBookmark.setDescription("Old Description");
        existingBookmark.setCategory(Category.DESIGN);
        testUser.getBookmarks().add(existingBookmark);

        BookmarkUpdateDTO updateDTO = new BookmarkUpdateDTO();
        updateDTO.setTitle("New Title");
        updateDTO.setDescription("New Description");
        updateDTO.setCategory(Category.LEARNING);

        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bookmark updated = bookmarkService.updateBookmark(bookmarkId, updateDTO, "testuser");
        assertEquals("New Title", updated.getTitle());
        assertEquals("New Description", updated.getDescription());
        assertEquals(Category.LEARNING, updated.getCategory());

        verify(userService, times(1)).getUserByUsername("testuser");
        verify(userService, times(1)).saveUser(testUser);
    }

    @Test
    void deleteBookmark() {
        UUID bookmarkId = UUID.randomUUID();
        Bookmark bookmark = new Bookmark();
        bookmark.setId(bookmarkId);
        testUser.getBookmarks().add(bookmark);

        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(userService.saveUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookmarkService.deleteBookmark(bookmarkId, "testuser");

        assertFalse(testUser.getBookmarks().stream().anyMatch(b -> b.getId().equals(bookmarkId)));
        verify(userService, times(1)).getUserByUsername("testuser");
        verify(userService, times(1)).saveUser(testUser);
    }

    @Test
    void getBookmarkByIdAndUsername() {
        UUID bookmarkId = UUID.randomUUID();
        Bookmark bookmark = new Bookmark();
        bookmark.setId(bookmarkId);

        testUser.getBookmarks().add(bookmark);

        when(userService.getUserByUsername("testuser")).thenReturn(testUser);

        Bookmark result = bookmarkService.getBookmarkByIdAndUsername(bookmarkId, "testuser");
        assertEquals(bookmarkId, result.getId());
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    void getBookmarksByCategory() {
        Category category = Category.TECHNOLOGY;
        List<Bookmark> bookmarks = List.of(new Bookmark(), new Bookmark());
        when(bookmarkRepository.findByCategory(category)).thenReturn(bookmarks);

        List<Bookmark> result = bookmarkService.getBookmarksByCategory(category);
        assertEquals(2, result.size());
        verify(bookmarkRepository, times(1)).findByCategory(category);
    }

    @Test
    void getBookmarksByTitle() {
        String title = "Some Title";
        List<Bookmark> bookmarks = List.of(new Bookmark(), new Bookmark(), new Bookmark());
        when(bookmarkRepository.findByTitle(title)).thenReturn(bookmarks);

        List<Bookmark> result = bookmarkService.getBookmarksByTitle(title);
        assertEquals(3, result.size());
        verify(bookmarkRepository, times(1)).findByTitle(title);
    }

    @Test
    void getBookmarksByUser() {

        List<Bookmark> bookmarkList = List.of(new Bookmark(), new Bookmark());
        testUser.setBookmarks(new ArrayList<>(bookmarkList));
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);

        List<Bookmark> result = bookmarkService.getBookmarksByUser("testuser");
        assertEquals(2, result.size());
        verify(userService, times(1)).getUserByUsername("testuser");
    }

    @Test
    void getSecureBookmarks() {
        List<Bookmark> secureBookmarks = List.of(new Bookmark(), new Bookmark(), new Bookmark());
        when(bookmarkRepository.findBySecureLinks()).thenReturn(secureBookmarks);

        List<Bookmark> result = bookmarkService.getSecureBookmarks();
        assertEquals(3, result.size());
        verify(bookmarkRepository, times(1)).findBySecureLinks();
    }
}
