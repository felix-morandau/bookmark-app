package com.felix.bookmark_app.controller;

import com.felix.bookmark_app.dto.BookmarkCreateDTO;
import com.felix.bookmark_app.dto.BookmarkUpdateDTO;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/bookmarks")
    public List<Bookmark> getAllBookmarks(
            @RequestParam(required = false) Boolean visible
    ) {
        return bookmarkService.getAllBookmarks(visible);
    }

    @GetMapping("{username}/bookmarks")
    public List<Bookmark> getAllUserBookmarks(
            @PathVariable String username,
            @RequestParam(required = false) Boolean visible
    ) {
        return bookmarkService.getBookmarksByUser(username, visible);
    }

    @GetMapping("{username}/bookmarks/bookmark/{id}")
    public Bookmark getBookmark(
            @PathVariable UUID id,
            @PathVariable String username) {
        return bookmarkService.getBookmarkByIdAndUsername(id, username);
    }

    @PostMapping("{username}/bookmarks/add_bookmark")
    public Bookmark createBookmark(
            @PathVariable String username,
            @Valid @RequestBody BookmarkCreateDTO bookmarkCreateDTO
    ) {
        return bookmarkService.addBookmark(bookmarkCreateDTO, username);
    }

    @PutMapping("{username}/bookmarks/bookmark/{bookmark_id}")
    public Bookmark updateBookmark(
            @PathVariable String username,
            @PathVariable UUID bookmark_id,
            @Valid @RequestBody BookmarkUpdateDTO bookmarkDTO
    ) {
        return bookmarkService.updateBookmark(bookmark_id, bookmarkDTO, username);
    }

    @DeleteMapping("{username}/bookmarks/bookmark/{bookmark_id}")
    public void deleteBookmark(
            @PathVariable UUID bookmark_id,
            @PathVariable String username) {
        bookmarkService.deleteBookmark(bookmark_id, username);
    }
}
