package com.felix.bookmark_app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.bookmark_app.dto.LoginRequest;
import com.felix.bookmark_app.dto.LoginResponse;
import com.felix.bookmark_app.model.UserType;
import com.felix.bookmark_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(LoginControllerTest.MockedUserServiceConfig.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private LoginRequest loginRequest;
    private LoginResponse validLoginResponse;
    private LoginResponse invalidLoginResponse;

    @BeforeEach
    void setUp() {
        loginRequest         = new LoginRequest("testuser", "testpassword");
        validLoginResponse   = new LoginResponse(true,  UserType.CLIENT, null, null);
        invalidLoginResponse = new LoginResponse(false, null,           "Invalid username or password", null);
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        when(userService.login("testuser", "testpassword"))
                .thenReturn(validLoginResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.role").value("CLIENT"))
                .andExpect(jsonPath("$.errorMessage").isEmpty());
    }

    @Test
    void testFailedLogin() throws Exception {
        when(userService.login("testuser", "testpassword"))
                .thenReturn(invalidLoginResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.role").isEmpty())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Invalid username or password"));
    }

    static class MockedUserServiceConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
}
