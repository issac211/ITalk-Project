package com.hit.dm;

import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;

public class User implements Serializable {
    public enum Role {
        ADMIN,
        MODERATOR,
        USER
    }

    private String username;
    private String password;
    private Role role;

    public User() {
    }

    public User(String username, String password, Role role) {
        setUsername(username);
        setPassword(password);
        setRole(role);
    }

    // Constructor without role parameter (defaults to USER)
    public User(String username, String password) {
        this(username, password, Role.USER);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean checkPassword(String rawPassword) {
        return BCrypt.checkpw(rawPassword, getPassword()); // Verify password
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
