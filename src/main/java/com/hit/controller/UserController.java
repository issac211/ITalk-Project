package com.hit.controller;

import com.hit.dm.User;
import com.hit.service.UserService;

import java.io.IOException;

/**
 * The UserController exposes API endpoints for user operations.
 * It acts as a separation layer between the networking layer and the business logic provided by UserService.
 */
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param userName the username of the new user
     * @param password the raw password (which will be encrypted by the service)
     * @param role     the role to assign to the new user
     * @return true if the user was created successfully; false if a user with the same username already exists
     * @throws IOException if an I/O error occurs during user creation
     */
    public boolean createUser(String userName, String password, User.Role role) throws IOException {
        return userService.createUser(userName, password, role);
    }

    /**
     * Edits an existing user.
     *
     * @param editorName  the username of the user making the edit request
     * @param userName    the username of the user to edit
     * @param oldPassword the current password of the user
     * @param newPassword the new password for the user
     * @param newRole     the new role to assign to the user
     * @return true if the user was edited successfully; false otherwise
     * @throws IOException if an I/O error occurs during the update
     */
    public boolean editUser(
            String editorName, String userName, String oldPassword, String newPassword, User.Role newRole) throws IOException {
        return userService.editUser(editorName, userName, oldPassword, newPassword, newRole);
    }

    /**
     * Removes an existing user.
     *
     * @param removerName the username of the user requesting the removal
     * @param userName    the username of the user to remove
     * @param password    the password of the user to remove
     * @return true if the user was removed successfully; false otherwise
     * @throws IOException if an I/O error occurs during the removal
     */
    public boolean removeUser(String removerName, String userName, String password) throws IOException {
        return userService.removeUser(removerName, userName, password);
    }

    /**
     * Authenticates a user.
     *
     * @param userName the username to authenticate
     * @param password the password to check
     * @return true if the user is authenticated; false otherwise
     * @throws IOException if an I/O error occurs during authentication
     */
    public boolean authenticate(String userName, String password) throws IOException {
        return userService.authenticate(userName, password);
    }

    /**
     * Retrieves a user by username and password.
     *
     * @param userName the username of the user
     * @param password the password of the user
     * @return the User if authentication is successful; null otherwise
     * @throws IOException if an I/O error occurs during retrieval
     */
    public User getUser(String userName, String password) throws IOException {
        return userService.getUser(userName, password);
    }
}
