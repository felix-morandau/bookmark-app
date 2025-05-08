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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final LinkRepository linkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository, UserService userService, LinkRepository linkRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.userService = userService;
        this.linkRepository = linkRepository;
    }

    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    public Bookmark addBookmark(BookmarkCreateDTO bookmarkDTO, String username) {
        User user = userService.getUserByUsername(username);

        Bookmark bookmark = new Bookmark();

        Link link = linkRepository.findByUrl(bookmarkDTO.getUrl())
                .orElseGet(() -> linkRepository.save(new Link(bookmarkDTO.getUrl())));
        bookmark.setLink(link);

        bookmark.setTitle(bookmarkDTO.getTitle());
        bookmark.setDescription(bookmarkDTO.getDescription());
        bookmark.setCategory(bookmarkDTO.getCategory());
        bookmark.setCreatedAt(LocalDateTime.now());

        user.getBookmarks().add(bookmark);

        userService.saveUser(user);

        return bookmark;
    }


    public Bookmark updateBookmark(UUID id, BookmarkUpdateDTO bookmarkDTO, String username) {
        User user = userService.getUserByUsername(username);

        Bookmark bookmark = user.getBookmarks().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(NoBookmarkFoundException::new);

        if (bookmarkDTO.getTitle() != null) {
            bookmark.setTitle(bookmarkDTO.getTitle());
        }
        if (bookmarkDTO.getDescription() != null) {
            bookmark.setDescription(bookmarkDTO.getDescription());
        }
        if (bookmarkDTO.getCategory() != null) {
            bookmark.setCategory(bookmarkDTO.getCategory());
        }

        userService.saveUser(user);

        return bookmark;
    }

    public void deleteBookmark(UUID id, String username) {
        User user = userService.getUserByUsername(username);

        Bookmark bookmark = user.getBookmarks().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(NoBookmarkFoundException::new);

        user.getBookmarks().remove(bookmark);

        userService.saveUser(user);
    }

    public Bookmark getBookmarkByIdAndUsername(UUID id, String username) {
        User user = userService.getUserByUsername(username);

        return user.getBookmarks().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(NoBookmarkFoundException::new);
    }

    public List<Bookmark> getBookmarksByCategory(Category category) {
        return bookmarkRepository.findByCategory(category);
    }

    public List<Bookmark> getBookmarksByTitle(String title) {
        return bookmarkRepository.findByTitle(title);
    }

    public List<Bookmark> getBookmarksByUser(String username) {
        User user = userService.getUserByUsername(username);

        return user.getBookmarks();
    }

    public List<Bookmark> getSecureBookmarks() {
        return bookmarkRepository.findBySecureLinks();
    }
}
