package com.hit.controller;

import com.hit.dao.CommentDaoImpl;
import com.hit.dao.PostDaoImpl;
import com.hit.dao.UserDaoImpl;
import com.hit.service.CommentService;
import com.hit.service.PostService;
import com.hit.service.UserService;

import java.io.IOException;
import java.util.HashMap;

public class ControllerFactory {
    private final HashMap<String, Object> Controllers = new HashMap<>();

    public ControllerFactory() throws IOException {
        UserDaoImpl userDao = new UserDaoImpl("src/main/resources/user.json");
        CommentDaoImpl commentDao = new CommentDaoImpl("src/main/resources/comment.json");
        PostDaoImpl postDao = new PostDaoImpl("src/main/resources/post.json");
        UserService userService = new UserService(userDao);
        CommentService commentService = new CommentService(commentDao, userDao);
        PostService postService = new PostService(postDao, commentDao, userDao);

        Controllers.put("user", new UserController(userService));
        Controllers.put("comment", new CommentController(commentService));
        Controllers.put("post", new PostController(postService));
    }

    public Object getController(String controllerName) {
        return Controllers.get(controllerName);
    }
}
