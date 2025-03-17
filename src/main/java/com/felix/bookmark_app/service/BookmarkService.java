package com.felix.bookmark_app.service;

import com.felix.bookmark_app.dto.BookmarkCreateDTO;
import com.felix.bookmark_app.dto.BookmarkUpdateDTO;
import com.felix.bookmark_app.model.*;
import com.felix.bookmark_app.repository.BookmarkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Data
@AllArgsConstructor
public class BookmarkService {
    private BookmarkRepository bookmarkRepository;
    private UserService userService;

    public List<Bookmark> getAllBookmarks(boolean visible) {

        if (visible) {
            return bookmarkRepository.findBookmarkByVisible(true);
        }

        return bookmarkRepository.findAll();
    }

    public Bookmark addBookmark(BookmarkCreateDTO bookmarkDTO, String username) {
        User user = userService.getUserByUsername(username);
        Bookmark bookmark = new Bookmark();

        bookmark.setCategory(bookmarkDTO.getCategory());
        bookmark.setLink(new Link(bookmarkDTO.getUrl()));
        bookmark.setUser(user);
        bookmark.setTitle(bookmark.getTitle());
        bookmark.setVisible(bookmarkDTO.isVisible());
        bookmark.setDescription(bookmark.getDescription());
        bookmark.setCreatedAt(LocalDateTime.now());
        user.getBookmarks().add(bookmark);

        return bookmark;
    }

    public Bookmark updateBookmark(UUID id, BookmarkUpdateDTO bookmarkDTO) {
        Bookmark bookmark = bookmarkRepository.findBookmarkById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found with ID: " + id));

        if (bookmarkDTO.getTitle() != null) {
            bookmark.setTitle(bookmarkDTO.getTitle());
        }

        if (bookmarkDTO.getDescription() != null) {
            bookmark.setDescription(bookmarkDTO.getDescription());
        }

        if (bookmarkDTO.getCategory() != null) {
            bookmark.setCategory(bookmarkDTO.getCategory());
        }

        if (bookmarkDTO.isVisible() != bookmark.isVisible()) {
            bookmark.setVisible(bookmarkDTO.isVisible());
        }

        return bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(UUID id) {
        bookmarkRepository.deleteById(id);
    }

    public Bookmark getBookmarkById(UUID id) {
        return bookmarkRepository.findBookmarkById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark with ID " + id + " not found."));
    }

    public List<Bookmark> getBookmarksByCategory(Category category) {
        return bookmarkRepository.findByCategory(category);
    }

    public List<Bookmark> getBookmarksByTitle(String title) {
        return bookmarkRepository.findByTitle(title);
    }

    public List<Bookmark> getBookmarksByUser(String username) {
        User user = userService.getUserByUsername(username);
        return bookmarkRepository.findByUser(user);
    }

    public List<Bookmark> getSecureBookmarks() {
        return bookmarkRepository.findBySecureLinks();
    }

    public Optional<Bookmark> getBookmarkByLink(Link link) {
        return bookmarkRepository.findByLink(link);
    }

    public List<Bookmark> getBookmarksByCollections(Set<Collection> collections) {
        return bookmarkRepository.findByCollections(collections);
    }

}
