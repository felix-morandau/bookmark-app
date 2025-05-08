package com.felix.bookmark_app.service;

import com.felix.bookmark_app.config.NoUserFoundException;
import com.felix.bookmark_app.dto.LoginResponse;
import com.felix.bookmark_app.dto.UserCreateDTO;
import com.felix.bookmark_app.dto.UserUpdateDTO;
import com.felix.bookmark_app.model.Collection;
import com.felix.bookmark_app.model.User;
import com.felix.bookmark_app.model.UserType;
import com.felix.bookmark_app.repository.CollectionRepository;
import com.felix.bookmark_app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollectionRepository collectionRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers() {
        List<User> adminUsers = List.of(new User(), new User());
        when(userRepository.findUsersByTypeOrderByUsername(UserType.ADMIN)).thenReturn(adminUsers);
        List<User> resultByType = userService.getUsers(UserType.ADMIN);
        assertEquals(2, resultByType.size());
        verify(userRepository, times(1)).findUsersByTypeOrderByUsername(UserType.ADMIN);

        List<User> allUsers = List.of(new User(), new User(), new User());
        when(userRepository.findAll()).thenReturn(allUsers);
        List<User> resultAll = userService.getUsers(null);
        assertEquals(3, resultAll.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser() {
        UserCreateDTO dto = new UserCreateDTO("testuser", "password", "test@example.com", UserType.CLIENT, "photo", "bio");

        User userToSave = new User();
        userToSave.setUsername(dto.getUsername());
        userToSave.setPassword(dto.getPassword());
        userToSave.setEmail(dto.getEmail());
        userToSave.setType(dto.getType());
        userToSave.setBio(dto.getBio());

        User savedUser = new User();
        savedUser.setUsername(userToSave.getUsername());
        savedUser.setPassword(userToSave.getPassword());
        savedUser.setEmail(userToSave.getEmail());
        savedUser.setType(userToSave.getType());
        savedUser.setBio(userToSave.getBio());
        savedUser.setId(UUID.randomUUID());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.addUser(dto);
        assertNotNull(result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        String username = "testuser";
        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldPassword");
        existingUser.setBio("old bio");

        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("new@example.com");
        updateDTO.setPassword("newPassword");
        updateDTO.setBio("new bio");

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updated = userService.updateUser(username, updateDTO);
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("newPassword", updated.getPassword());
        assertEquals("new bio", updated.getBio());
        verify(userRepository, times(1)).findUserByUsername(username);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void deleteUser() {
        String username = "testuser";
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(id);

        userService.deleteUser(username);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void login() {
        String username = "testuser";
        String correctPassword = "password";
        String wrongPassword = "wrongPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(correctPassword);
        user.setType(UserType.ADMIN);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        LoginResponse successResponse = userService.login(username, correctPassword);
        assertTrue(successResponse.valid());
        assertEquals(UserType.ADMIN, successResponse.role());
        assertNull(successResponse.errorMessage());

        User userWithDifferentPassword = new User();
        userWithDifferentPassword.setUsername(username);
        userWithDifferentPassword.setPassword(wrongPassword);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(userWithDifferentPassword));

        LoginResponse wrongPasswordResponse = userService.login(username, correctPassword);
        assertFalse(wrongPasswordResponse.valid());
        assertEquals("Incorrect password", wrongPasswordResponse.errorMessage());

        when(userRepository.findUserByUsername("nouser")).thenReturn(Optional.empty());
        LoginResponse notFoundResponse = userService.login("nouser", correctPassword);
        assertFalse(notFoundResponse.valid());
        assertEquals("User with username nouser not found", notFoundResponse.errorMessage());
    }

    @Test
    void getUserById() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        when(userRepository.findUserById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);
        assertEquals(user, result);
        verify(userRepository, times(1)).findUserById(id);

        when(userRepository.findUserById(id)).thenReturn(Optional.empty());
        assertThrows(NoUserFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void getUserByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);
        assertEquals(user, result);
        verify(userRepository, times(1)).findUserByEmail(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        assertThrows(NoUserFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    void getUserByUsername() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername(username);
        assertEquals(user, result);
        verify(userRepository, times(1)).findUserByUsername(username);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
        assertThrows(NoUserFoundException.class, () -> userService.getUserByUsername(username));
    }

    @Test
    void saveCollection() {
        String username = "testuser";
        UUID collectionId = UUID.randomUUID();
        User user = new User();
        user.setUsername(username);
        user.setSavedCollections(new ArrayList<>());
        Collection collection = new Collection();
        collection.setId(collectionId);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveCollection(username, collectionId);
        assertTrue(result.getSavedCollections().contains(collection));
        verify(userRepository, times(1)).findUserByUsername(username);
        verify(collectionRepository, times(1)).findById(collectionId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void removeSavedCollection() {
        String username = "testuser";
        UUID collectionId = UUID.randomUUID();
        Collection collection = new Collection();
        collection.setId(collectionId);
        List<Collection> savedCollections = new ArrayList<>();
        savedCollections.add(collection);
        User user = new User();
        user.setUsername(username);
        user.setSavedCollections(savedCollections);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(collectionRepository.findById(collectionId)).thenReturn(Optional.of(collection));
        when(userRepository.save(user)).thenReturn(user);

        userService.removeSavedCollection(username, collectionId);
        assertFalse(user.getSavedCollections().contains(collection));
        verify(userRepository, times(1)).findUserByUsername(username);
        verify(collectionRepository, times(1)).findById(collectionId);
        verify(userRepository, times(1)).save(user);
    }
}
