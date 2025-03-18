package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoBookmarkFoundException;
import com.felix.bookmark_app.dto.BookmarkCreateDTO;
import com.felix.bookmark_app.dto.BookmarkUpdateDTO;
import com.felix.bookmark_app.model.*;
import com.felix.bookmark_app.repository.BookmarkRepository;
import com.felix.bookmark_app.repository.LinkRepository;
import com.felix.bookmark_app.repository.UserRepository;
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
    private LinkRepository linkRepository;

    public List<Bookmark> getAllBookmarks(Boolean visible) {

        if (visible != null) {
            return bookmarkRepository.findBookmarkByVisible(visible);
        }

        return bookmarkRepository.findAll();
    }

    public Bookmark addBookmark(BookmarkCreateDTO bookmarkDTO, String username) {
        User user = userService.getUserByUsername(username);
        Bookmark bookmark = new Bookmark();

        Link link = linkRepository.findByUrl(bookmarkDTO.getUrl())
                .orElseGet(() -> linkRepository.save(new Link(bookmarkDTO.getUrl())));

        bookmark.setCategory(bookmarkDTO.getCategory());
        bookmark.setLink(link);
        bookmark.setUser(user);
        bookmark.setTitle(bookmarkDTO.getTitle());
        bookmark.setVisible(bookmarkDTO.isVisible());
        bookmark.setDescription(bookmarkDTO.getDescription());
        bookmark.setCreatedAt(LocalDateTime.now());
        user.getBookmarks().add(bookmark);

        return bookmarkRepository.save(bookmark);
    }

    public Bookmark updateBookmark(UUID id, BookmarkUpdateDTO bookmarkDTO, String username) {
        Bookmark bookmark = bookmarkRepository.findBookmarkById(id)
                .orElseThrow(NoBookmarkFoundException::new);

        if (!bookmark.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Unauthorized to delete this bookmark");
        }

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

    public void deleteBookmark(UUID id, String username) {
        Bookmark bookmark = bookmarkRepository.findBookmarkById(id)
                .orElseThrow(NoBookmarkFoundException::new);

        if (!bookmark.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Unauthorized to delete this bookmark");
        }

        bookmarkRepository.deleteById(id);
    }

    public Bookmark getBookmarkByIdAndUsername(UUID id, String username) {
        User user = userService.getUserByUsername(username);

        return user.getBookmarks().stream().filter(bookmark -> bookmark.getId().equals(id)).findFirst().orElseThrow(NoBookmarkFoundException::new);
    }

    public List<Bookmark> getBookmarksByCategory(Category category) {
        return bookmarkRepository.findByCategory(category);
    }

    public List<Bookmark> getBookmarksByTitle(String title) {
        return bookmarkRepository.findByTitle(title);
    }

    public List<Bookmark> getBookmarksByUser(String username, Boolean visible) {
        User user = userService.getUserByUsername(username);

        if (visible != null) {
            return bookmarkRepository.findByUserAndVisible(user, visible);
        }
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
