package com.hit.controller;

import com.hit.dm.User;
import com.hit.service.UserService;

import java.io.IOException;
import java.util.Map;

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
     * <h5> Creates a new user based on the provided request body. </h5>
     * This method extracts the `userName`, `password`, and `role` from the given
     * request body map, converts the `role` to an enum value, and delegates the
     * user creation process to the `userService`.
     *
     * @param requestBody a map containing the user details:<br>
     *                    <ul>
     *                      <li> "userName": the username of the new user (String) </li>
     *                      <li> "password": the raw password of the new user (String) </li>
     *                      <li> "role": the role to assign to the new user (String) </li>
     *                    </ul>
     * @return true if the user was created successfully; false if a user with the
     *         same username already exists
     * @throws IOException if an I/O error occurs during user creation
     */
    public boolean createUser(Map<String, Object> requestBody) throws IOException {
        String userName = (String) requestBody.get("userName");
        String password = (String) requestBody.get("password");
        String roleStr = (String) requestBody.get("role");
        return userService.createUser(userName, password,
                User.Role.valueOf(roleStr.toUpperCase()));
    }

    /**
     * <h5> Edits an existing user based on the provided request body. </h5>
     * This method extracts the `editorName`, `userName`, `oldPassword`, `newPassword`,
     * and `newRole` from the given request body map, converts the `newRole` to an
     * enum value, and delegates the user editing process to the `userService`.
     *
     * @param requestBody a map containing the user details:<br>
     *                    <ul>
     *                      <li> "editorName": the username of the user making the edit request (String) </li>
     *                      <li> "userName": the username of the user to edit (String) </li>
     *                      <li> "oldPassword": the current password of the user (String) </li>
     *                      <li> "newPassword": the new password for the user (String) </li>
     *                      <li> "newRole": the new role to assign to the user (String) </li>
     *                    </ul>
     * @return true if the user was edited successfully; false otherwise
     * @throws IOException if an I/O error occurs during the update
     */
    public boolean editUser(Map<String, Object> requestBody) throws IOException {
        String editorName = (String) requestBody.get("editorName");
        String userName = (String) requestBody.get("userName");
        String oldPassword = (String) requestBody.get("oldPassword");
        String newPassword = (String) requestBody.get("newPassword");
        String newRole = (String) requestBody.get("newRole");
        return userService.editUser(editorName, userName, oldPassword, newPassword,
                User.Role.valueOf(newRole.toUpperCase()));
    }

    /**
     * <h5> Removes an existing user based on the provided request body. </h5>
     * This method extracts the `removerName`, `userName`, and `password` from the given
     * request body map and delegates the user removal process to the `userService`.
     *
     * @param requestBody a map containing the user details:<br>
     *                    <ul>
     *                      <li> "removerName": the username of the user requesting the removal (String) </li>
     *                      <li> "userName": the username of the user to remove (String) </li>
     *                      <li> "password": the password of the user to remove (String) </li>
     *                    </ul>
     * @return true if the user was removed successfully; false otherwise
     * @throws IOException if an I/O error occurs during the removal
     */
    public boolean removeUser(Map<String, Object> requestBody) throws IOException {
        String removerName = (String) requestBody.get("removerName");
        String userName = (String) requestBody.get("userName");
        String password = (String) requestBody.get("password");
        return userService.removeUser(removerName, userName, password);
    }

    /**
     * <h5> Authenticates a user based on the provided request body. </h5>
     * This method extracts the `userName` and `password` from the given
     * request body map and delegates the authentication process to the `userService`.
     *
     * @param requestBody a map containing the user details:<br>
     *                    <ul>
     *                      <li> "userName": the username to authenticate (String) </li>
     *                      <li> "password": the password to check (String) </li>
     *                    </ul>
     * @return true if the user is authenticated; false otherwise
     * @throws IOException if an I/O error occurs during authentication
     */
    public boolean authenticate(Map<String, Object> requestBody) throws IOException {
        String userName = (String) requestBody.get("userName");
        String password = (String) requestBody.get("password");
        return userService.authenticate(userName, password);
    }

    /**
     * <h5> Retrieves a user by username and password from the provided request body. </h5>
     * This method extracts the `userName` and `password` from the given
     * request body map and delegates the retrieval process to the `userService`.
     *
     * @param requestBody a map containing the user details:<br>
     *                    <ul>
     *                      <li> "userName": the username of the user to retrieve (String) </li>
     *                      <li> "password": the password of the user to retrieve (String) </li>
     *                    </ul>
     * @return the User object if authentication is successful; null otherwise
     * @throws IOException if an I/O error occurs during retrieval
     */
    public User getUser(Map<String, Object> requestBody) throws IOException {
        String userName = (String) requestBody.get("userName");
        String password = (String) requestBody.get("password");
        return userService.getUser(userName, password);
    }
}
