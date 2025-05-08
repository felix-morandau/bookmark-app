package com.felix.bookmark_app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/user/";

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        userRepository.flush();
        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJSON = loadFixture("user_seed.json");
        List<User> users = objectMapper.readValue(seedDataJSON, new TypeReference<List<User>>() {});
        userRepository.saveAll(users);
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }

    @Test
    void testAddValidUser() throws Exception {
        String validClient = loadFixture("valid_user.json");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validClient))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("alice.smith@example.com"))
                .andExpect(jsonPath("$.type").value("CLIENT"));
    }

    @Test
    void testAddInvalidUserPassword() throws Exception{
        String invalidClientJson = loadFixture("invalid_user.json");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidClientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must contain at least: one uppercase character, one lowercase character, one digit, one special character"));
    }

    @Test
    void testAddUserDuplicateInfo() throws Exception {
        String invalidClientJson = loadFixture("valid_user2.json");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidClientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unique constraint violation"));
    }

    @Test
    void testUpdateValidUser() throws Exception{
        String validClientJson = loadFixture("new_valid_user.json");

        User existingUser = userRepository.findUserByUsername("client1").orElseThrow(
                () -> new RuntimeException("User not found")
        );

        mockMvc.perform(put("/users/{username}", existingUser.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validClientJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio").value("updated"))
                .andExpect(jsonPath("$.password").value("UserUpdate1234!"));
    }

    @Test
    void testUpdateInvalidUser() throws Exception {
        String invalidUserJson = loadFixture("new_invalid_user.json");

        User existingUser = userRepository.findUserByUsername("client1").orElseThrow(
                () -> new RuntimeException("User not found")
        );

        mockMvc.perform(put("/users/{username}", existingUser.getUsername())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password")
                        .value("Password must contain at least: one uppercase character, one lowercase character, one digit, one special character"));
    }

    @Test
    void testDeleteUser() throws Exception{
        User existingUser = userRepository.findUserByUsername("client2").orElseThrow(
                () -> new RuntimeException("User not found")
        );

        mockMvc.perform(delete("/users/{username}", existingUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].username", Matchers.containsInAnyOrder("client1", "client2", "admin1")))
                .andExpect(jsonPath("$[*].email", Matchers.containsInAnyOrder(
                        "client1@example.com",
                        "client2@example.com",
                        "admin1@example.com")))
                .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder("CLIENT", "CLIENT", "ADMIN")));
    }

    @Test
    void testGetAdmins() throws Exception {
        mockMvc.perform(get("/users?type=ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*].username", Matchers.containsInAnyOrder("admin1")))
                .andExpect(jsonPath("$[*].email", Matchers.containsInAnyOrder(
                        "admin1@example.com")))
                .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder("ADMIN")));
    }
}