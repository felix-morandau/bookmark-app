package com.felix.bookmark_app.controller;

import com.felix.bookmark_app.dto.BookmarkCreateDTO;
import com.felix.bookmark_app.dto.BookmarkUpdateDTO;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.service.BookmarkService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping()
    public List<Bookmark> getAllBookmarks(
            @RequestParam(required = false) boolean visible
    ) {
        return bookmarkService.getAllBookmarks(visible);
    }

    @PostMapping
    public Bookmark createBookmark(
            @RequestBody BookmarkCreateDTO bookmarkCreateDTO,
            @RequestParam String username
    ) {
        return bookmarkService.addBookmark(bookmarkCreateDTO, username);
    }

    @PutMapping("/{id}")
    public Bookmark updateBookmark(
            @PathVariable UUID id,
            @RequestBody BookmarkUpdateDTO bookmarkDTO
    ) {
        return bookmarkService.updateBookmark(id, bookmarkDTO);
    }

    @DeleteMapping
    public void deleteBookmark(UUID id) {
        bookmarkService.deleteBookmark(id);
    }
}
