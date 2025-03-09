package com.hit.dao;

import com.hit.dm.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements IDao<String, User> {
    private final JsonFileManager<String, User> jsonFileManager;

    public UserDaoImpl(String pathFile) {
        jsonFileManager = new JsonFileManager<>(pathFile, String.class, User.class);
    }

    @Override
    public void delete(User userEntity) throws IOException {
        HashMap<String, User> users = jsonFileManager.getFileData();
        if (users != null && users.containsKey(userEntity.getUsername())) {
            User userFromDB = users.get(userEntity.getUsername());
            if (userEntity.getPassword().equals(userFromDB.getPassword())) {
                users.remove(userEntity.getUsername());
                jsonFileManager.setFileData(users);
            }
        }
    }

    @Override
    public User find(String userName) throws IOException {
        HashMap<String, User> users = jsonFileManager.getFileData();
        if (users == null)
            users = new HashMap<String, User>();

        return users.get(userName);
    }

    @Override
    public void save(User user) throws IOException {
        HashMap<String, User> users = jsonFileManager.getFileData();
        if (users == null)
            users = new HashMap<String, User>();

        users.put(user.getUsername(), user);
        jsonFileManager.setFileData(users);
    }

    @Override
    public List<User> getAll() throws IOException {
        HashMap<String, User> users = jsonFileManager.getFileData();
        if (users == null)
            return new ArrayList<User>();

        return new ArrayList<>(users.values());
    }
}
