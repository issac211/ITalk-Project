package com.hit.service;

import com.hit.dao.UserDaoImpl;
import com.hit.dm.User;

import java.io.IOException;

public class UserService {
    private final UserDaoImpl userDao;

    public UserService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    public boolean createUser(String userName, String password, User.Role role) throws IOException {
        User user = userDao.find(userName);
        if (user == null) {
            userDao.save(new User(userName, password, role));
            return true;
        }
        return false;
    }

    public boolean editUser(String editorName, String userName, String oldPassword,
            String newPassword, User.Role newRole) throws IOException {

        User editor = userDao.find(editorName);
        User user = userDao.find(userName);
        boolean authorizedEditor = false;
        boolean specialAuthorizedUser = false;

        if (user != null) {
            if (editor != null) {
                specialAuthorizedUser = editor.getRole() == User.Role.ADMIN;
                authorizedEditor = editor.getUsername().equals(userName);
            }

            if ((user.checkPassword(oldPassword) && authorizedEditor) || specialAuthorizedUser) {
                userDao.save(new User(userName, newPassword, newRole));
                return true;
            }
        }

        return false;
    }

    public boolean removeUser(String removerName, String userName, String password) throws IOException {
        User remover = userDao.find(removerName);
        User user = userDao.find(userName);
        boolean authorizedUser = false;
        boolean specialAuthorizedUser = false;

        if (user != null) {
            if (remover != null) {
                specialAuthorizedUser = remover.getRole() == User.Role.ADMIN;
                authorizedUser = remover.getUsername().equals(userName);
            }

            if ((user.checkPassword(password) && authorizedUser) || specialAuthorizedUser) {
                userDao.delete(user);
                return true;
            }
        }

        return false;
    }

    public boolean authenticate(String userName, String password) throws IOException {
        User user = userDao.find(userName);
        if (user == null)
            return false;
        return user.checkPassword(password);
    }

    public User getUser(String userName, String password) throws IOException {
        if (!authenticate(userName, password)) {
            return null;
        }

        return userDao.find(userName);
    }
}
