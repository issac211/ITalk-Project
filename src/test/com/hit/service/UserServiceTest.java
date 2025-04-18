package com.hit.service;

import com.hit.dao.UserDaoImpl;
import com.hit.dm.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    @TempDir
    Path tempDir;  // Temporary directory for file-based tests

    private UserDaoImpl userDao;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        // Create a file path in the temporary directory for the user database
        String userFilePath = tempDir.resolve("users.json").toString();
        // Instantiate the concrete DAO implementation that persists to file
        userDao = new UserDaoImpl(userFilePath);
        // Create the UserService using the file-based DAO
        userService = new UserService(userDao);
    }

    @Test
    void testCreateUserSuccess() throws IOException {
        boolean created = userService.createUser("user1", "password123", User.Role.USER);
        assertTrue(created, "User should be created successfully");

        User user = userDao.find("user1");
        assertNotNull(user, "User should be found in the database");
        assertTrue(user.checkPassword("password123"), "Password should match");
        assertEquals(User.Role.USER, user.getRole(), "User role should be set to USER");
    }

    @Test
    void testCreateUserDuplicate() throws IOException {
        boolean created1 = userService.createUser("user1", "password123", User.Role.USER);
        assertTrue(created1, "First user creation should succeed");

        // Attempt to create a duplicate user with the same userName
        boolean created2 = userService.createUser("user1", "password456", User.Role.USER);
        assertFalse(created2, "Creating a duplicate user should fail");
    }

    @Test
    void testEditUserSuccess() throws IOException {
        // Create a user first
        boolean created = userService.createUser("user1", "password123", User.Role.USER);
        assertTrue(created, "User creation should succeed");

        // Edit the user with the correct old password
        boolean edited = userService.editUser(
                "user1", "user1", "password123", "newPassword", User.Role.USER);
        assertTrue(edited, "Editing user should succeed with correct password");

        User user = userDao.find("user1");
        assertTrue(user.checkPassword("newPassword"), "New password should match");
    }

    @Test
    void testEditUserWrongPassword() throws IOException {
        // Create a user first
        userService.createUser("user1", "password123", User.Role.USER);

        // Attempt to edit the user with an incorrect old password
        boolean edited = userService.editUser(
                "user1", "user1", "wrongPassword", "newPassword", User.Role.USER);
        assertFalse(edited, "Editing should fail with an incorrect old password");

        User user = userDao.find("user1");
        assertTrue(user.checkPassword("password123"), "Old password should match");
        assertFalse(user.checkPassword("newPassword"), "New password should not match");
    }

    @Test
    void testEditUserAuthorization() throws IOException {
        // Create users
        userService.createUser("user1", "password123", User.Role.USER);
        userService.createUser("user2", "password123", User.Role.USER);
        // Create moderator
        userService.createUser("moderator", "IamMODERATOR", User.Role.MODERATOR);
        // Create admin
        userService.createUser("admin", "IamADMIN", User.Role.ADMIN);

        // Attempt to edit the user with an Unauthorized user
        boolean edited = userService.editUser(
                "user2", "user1", "password123", "newPassword", User.Role.USER);
        assertFalse(edited, "Editing should fail with an Unauthorized user");

        User user = userDao.find("user1");
        assertTrue(user.checkPassword("password123"), "Old password should match");
        assertFalse(user.checkPassword("newPassword"), "New password should not match");

        // Attempt to edit the user with an Unauthorized moderator
        edited = userService.editUser(
                "moderator", "user1", "password123", "newPassword", User.Role.USER);
        assertFalse(edited, "Editing should fail with an Unauthorized moderator");

        user = userDao.find("user1");
        assertTrue(user.checkPassword("password123"), "Old password should match");
        assertFalse(user.checkPassword("newPassword"), "New password should not match");

        // Edit the user with an authorized user (admin) and with wrong password
        edited = userService.editUser(
                "admin", "user1", "wrongPassword", "newPassword", User.Role.USER);
        assertTrue(edited, "Editing user should succeed with an authorized user (admin)");

        user = userDao.find("user1");
        assertFalse(user.checkPassword("password123"), "Old password should not match");
        assertTrue(user.checkPassword("newPassword"), "New password should match");
    }

    @Test
    void testRemoveUserSuccess() throws IOException {
        // Create a user
        userService.createUser("user1", "password123", User.Role.USER);

        // Remove the user using the correct password
        boolean removed = userService.removeUser("user1", "user1", "password123");
        assertTrue(removed, "User should be removed successfully");

        User user = userDao.find("user1");
        assertNull(user, "User should no longer exist in the database");
    }

    @Test
    void testRemoveUserWrongPassword() throws IOException {
        // Create a user
        userService.createUser("user1", "password123", User.Role.USER);

        // Attempt to remove the user with an incorrect password
        boolean removed = userService.removeUser("user1", "user1", "wrongPassword");
        assertFalse(removed, "User removal should fail with the wrong password");

        User user = userDao.find("user1");
        assertNotNull(user, "User should still exist in the database");
        assertTrue(user.checkPassword("password123"), "User should still have the same password");
    }

    @Test
    void testRemoveUserAuthorization() throws IOException {
        // Create users
        userService.createUser("user1", "password123", User.Role.USER);
        userService.createUser("user2", "password123", User.Role.USER);
        // Create moderator
        userService.createUser("moderator", "IamMODERATOR", User.Role.MODERATOR);
        // Create admin
        userService.createUser("admin", "IamADMIN", User.Role.ADMIN);

        // Attempt to remove the user with an Unauthorized user
        boolean removed = userService.removeUser("user2", "user1", "password123");
        assertFalse(removed, "User removal should fail with an Unauthorized user");

        User user = userDao.find("user1");
        assertNotNull(user, "User should still exist in the database");
        assertTrue(user.checkPassword("password123"), "User should still have the same password");

        // Attempt to remove the user with an Unauthorized moderator
        removed = userService.removeUser("moderator", "user1", "password123");
        assertFalse(removed, "User removal should fail with an Unauthorized moderator");

        user = userDao.find("user1");
        assertNotNull(user, "User should still exist in the database");
        assertTrue(user.checkPassword("password123"), "User should still have the same password");

        // Remove the user with an authorized user (admin) and with wrong password
        removed = userService.removeUser("admin", "user1", "wrongPassword");
        assertTrue(removed, "User removal should succeed with an authorized user (admin)");

        user = userDao.find("user1");
        assertNull(user, "User should not exist in the database");
    }

    @Test
    void testAuthenticateSuccess() throws IOException {
        userService.createUser("user1", "password123", User.Role.USER);

        boolean auth = userService.authenticate("user1", "password123");
        assertTrue(auth, "Authentication should succeed with correct credentials");
    }

    @Test
    void testAuthenticateFailure() throws IOException {
        userService.createUser("user1", "password123", User.Role.USER);

        boolean auth = userService.authenticate("user1", "wrongPassword");
        assertFalse(auth, "Authentication should fail with incorrect credentials");
    }

    @Test
    void testGetUserSuccess() throws IOException {
        userService.createUser("user1", "password123", User.Role.USER);

        User user = userService.getUser("user1", "password123");
        assertNotNull(user, "getUser should return a valid user when credentials are correct");
    }

    @Test
    void testGetUserFailure() throws IOException {
        userService.createUser("user1", "password123", User.Role.USER);

        User user = userService.getUser("user1", "wrongPassword");
        assertNull(user, "getUser should return null when authentication fails");
    }
}
