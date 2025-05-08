package com.felix.bookmark_app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.bookmark_app.model.Bookmark;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.BookmarkRepository;
import com.felix.bookmark_app.repository.LinkRepository;
import com.felix.bookmark_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/bookmark/";

    @BeforeEach
    void setUp() throws Exception {
        bookmarkRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.flush();
        bookmarkRepository.flush();
        seedDatabase();
    }

    @Autowired
    private LinkRepository linkRepository;

    private void seedDatabase() throws Exception {
        String userSeedDataJSON = loadFixture("../user/user_seed.json");
        List<User> users = objectMapper.readValue(userSeedDataJSON, new TypeReference<List<User>>() {});
        userRepository.saveAll(users);

        String bookmarkSeedDataJSON = loadFixture("bookmark_seed.json");
        List<Bookmark> bookmarks = objectMapper.readValue(bookmarkSeedDataJSON, new TypeReference<List<Bookmark>>() {});

        bookmarks.forEach(bookmark -> {
            if (bookmark.getLink() != null) {
                linkRepository.save(bookmark.getLink());
            }
        });

        bookmarkRepository.saveAll(bookmarks);
    }


    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }

    @Test
    void testGetAllBookmarks() throws Exception {
        mockMvc.perform(get("/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Bookmark 1", "Bookmark 2", "Bookmark 3")))
                .andExpect(jsonPath("$[*].link.url", containsInAnyOrder(
                        "https://example.com/1",
                        "https://example.com/2",
                        "https://example.com/3")))
                .andExpect(jsonPath("$[*].category", containsInAnyOrder("TECHNOLOGY", "EDUCATION", "PRODUCTIVITY")));
    }

    @Test
    void testGetAllUserBookmarks() throws Exception {
        mockMvc.perform(get("/{username}/bookmarks", "client1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Bookmark 1", "Bookmark 2", "Bookmark 3")))
                .andExpect(jsonPath("$[*].link.url", containsInAnyOrder(
                        "https://example.com/1",
                        "https://example.com/2",
                        "https://example.com/3")))
                .andExpect(jsonPath("$[*].category", containsInAnyOrder("TECHNOLOGY", "EDUCATION", "PRODUCTIVITY")));
    }

    @Test
    void testGetBookmarkByIdAndUsername() throws Exception {
        Bookmark existingBookmark = bookmarkRepository.findAll().get(0);
        mockMvc.perform(get("/{username}/bookmarks/bookmark/{id}", "client1", existingBookmark.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(existingBookmark.getTitle()))
                .andExpect(jsonPath("$.link.url").value(existingBookmark.getLink().getUrl()));
    }

    @Test
    void testGetBookmarkInvalidUser() throws Exception {
        Bookmark existingBookmark = bookmarkRepository.findAll().get(0);
        mockMvc.perform(get("/{username}/bookmarks/bookmark/{id}", "nonexistent", existingBookmark.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateValidBookmark() throws Exception {
        String validBookmarkJson = loadFixture("valid_bookmark.json");

        mockMvc.perform(post("/{username}/bookmarks/add_bookmark", "client1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookmarkJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("New Bookmark"))
                .andExpect(jsonPath("$.link.url").value("https://newbookmark.com"))
                .andExpect(jsonPath("$.category").value("TECHNOLOGY"));
    }

    @Test
    void testCreateInvalidBookmark() throws Exception {
        String invalidBookmarkJson = loadFixture("invalid_bookmark.json");

        mockMvc.perform(post("/{username}/bookmarks/add_bookmark", "client1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBookmarkJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("URL must be a valid URL"));
    }

    @Test
    void testUpdateValidBookmark() throws Exception {
        String validBookmarkJson = loadFixture("valid_bookmark_update.json");
        Bookmark existingBookmark = bookmarkRepository.findAll().get(0);

        mockMvc.perform(put("/{username}/bookmarks/bookmark/{bookmark_id}", "client1", existingBookmark.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBookmarkJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Bookmark"))
                .andExpect(jsonPath("$.link.url").value("https://updatedbookmark.com"))
                .andExpect(jsonPath("$.category").value("EDUCATION"));
    }

    @Test
    void testUpdateInvalidBookmark() throws Exception {
        String invalidBookmarkJson = loadFixture("invalid_bookmark_update.json");
        Bookmark existingBookmark = bookmarkRepository.findAll().get(0);

        mockMvc.perform(put("/{username}/bookmarks/bookmark/{bookmark_id}", "client1", existingBookmark.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBookmarkJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Not null constraint violation."));
    }

    @Test
    void testDeleteBookmark() throws Exception {
        Bookmark existingBookmark = bookmarkRepository.findAll().get(0);

        mockMvc.perform(delete("/{username}/bookmarks/bookmark/{bookmark_id}", "client1", existingBookmark.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteBookmarkInvalidUser() throws Exception {
        Bookmark existingBookmark = bookmarkRepository.findAll().get(0);

        mockMvc.perform(delete("/{username}/bookmarks/bookmark/{bookmark_id}", "nonexistent", existingBookmark.getId()))
                .andExpect(status().isNotFound());
    }
}