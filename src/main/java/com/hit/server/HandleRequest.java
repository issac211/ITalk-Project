package com.hit.server;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.hit.controller.*;
import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;
import com.hit.dm.User;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * The HandleRequest class is responsible for handling a single client connection.
 * It reads a JSON request from the socket using Gson directly from the Reader,
 * dispatches the request to the appropriate controller (using ControllerFactory),
 * and writes a JSON-formatted Response back to the client.
 */
public class HandleRequest implements Runnable {
    private final Socket clientSocket;
    private final ControllerFactory controllerFactory;
    private final Gson gson = new Gson();

    public HandleRequest(Socket clientSocket, ControllerFactory controllerFactory) {
        this.clientSocket = clientSocket;
        this.controllerFactory = controllerFactory;
    }

    @Override
    public void run() {
        try (
                JsonReader reader = new JsonReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)
        ) {
            // Directly deserialize the JSON request from the Reader into a Request object.
            Request request = gson.fromJson(reader, Request.class);
            System.out.println("Received request");

            // Retrieve action and body from the request.
            String action = request.getAction(); // e.g., "user/create" or "post/edit"
            Map<String, Object> body = request.getBody();

            // Prepare a Response variable to hold our reply.
            Response response;

            // Determine which controller should handle the request.
            // We expect the action to be in the format "controllerName/actionName".
            String[] parts = action.split("/");
            if (parts.length < 2) {
                response = new Response(400, Map.of("error", "Invalid action format."));
            } else {
                String controllerName = parts[0].toLowerCase(); // e.g., "user"
                String methodAction = parts[1].toLowerCase();   // e.g., "create"

                // Retrieve the proper controller from the factory.
                Object controller = controllerFactory.getController(controllerName);

                // Dispatch the request based on the controller name and action.
                switch (controllerName) {
                    case "user": {
                        // Cast to UserController.
                        UserController userController = (UserController) controller;
                        try {
                            switch (methodAction) {
                                case "create" -> {
                                    String userName = (String) body.get("userName");
                                    String password = (String) body.get("password");
                                    String roleStr = (String) body.get("role");
                                    boolean result = userController.createUser(userName, password,
                                            User.Role.valueOf(roleStr.toUpperCase()));
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "edit" -> {
                                    String editorName = (String) body.get("editorName");
                                    String userName = (String) body.get("userName");
                                    String oldPassword = (String) body.get("oldPassword");
                                    String newPassword = (String) body.get("newPassword");
                                    String newRole = (String) body.get("newRole");
                                    boolean result = userController.editUser(editorName, userName, oldPassword,
                                            newPassword, User.Role.valueOf(newRole.toUpperCase()));
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "remove" -> {
                                    String removerName = (String) body.get("removerName");
                                    String userName = (String) body.get("userName");
                                    String password = (String) body.get("password");
                                    boolean result = userController.removeUser(removerName, userName, password);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "authenticate" -> {
                                    String userName = (String) body.get("userName");
                                    String password = (String) body.get("password");
                                    boolean result = userController.authenticate(userName, password);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "get" -> {
                                    String userName = (String) body.get("userName");
                                    String password = (String) body.get("password");
                                    User userResult = userController.getUser(userName, password);
                                    if (userResult == null) {
                                        response = new Response(404, Map.of("error", "User Not Found"));
                                    } else {
                                        response = new Response(200, Map.of("result", userResult));
                                    }
                                }
                                default -> response = new Response(400, Map.of(
                                        "error", "Unknown action for user controller."));
                            }
                        } catch (IllegalArgumentException e) {
                            response = new Response(400, Map.of(
                                    "error", "Invalid request message for user."));
                        }
                        break;
                    }
                    case "post": {
                        // Cast to PostController.
                        PostController postController = (PostController) controller;
                        try {
                            switch (methodAction) {
                                case "create" -> {
                                    String title = (String) body.get("title");
                                    String userName = (String) body.get("userName");
                                    String content = (String) body.get("content");
                                    postController.createPost(title, userName, content);
                                    response = new Response(200, Map.of("result", "Post created successfully"));
                                }
                                case "edit" -> {
                                    long postId = getLongFromBody(body, "postId");
                                    String title = (String) body.get("title");
                                    String userName = (String) body.get("userName");
                                    String content = (String) body.get("content");
                                    boolean result = postController.editPost(postId, title, userName, content);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "remove" -> {
                                    long postId = getLongFromBody(body, "postId");
                                    String userName = (String) body.get("userName");
                                    boolean result = postController.removePost(postId, userName);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "get" -> {
                                    long postId = getLongFromBody(body, "postId");
                                    Post postResult = postController.getPostById(postId);
                                    if (postResult == null) {
                                        response = new Response(404, Map.of("error", "Post Not Found"));
                                    } else {
                                        response = new Response(200, Map.of("result", postResult));
                                    }
                                }
                                case "get-all" -> {
                                    List<Post> posts = postController.getAllPosts();
                                    response = new Response(200, Map.of("result", posts));
                                }
                                case "get-comments" -> {
                                    long postId = getLongFromBody(body, "postId");
                                    List<Comment> comments = postController.getPostComments(postId);
                                    response = new Response(200, Map.of("result", comments));
                                }
                                case "search-titles" -> {
                                    String searchPattern = (String) body.get("searchPattern");
                                    SearchResult<Post> searchResult = postController.searchTitles(searchPattern);
                                    response = new Response(200, Map.of("result", searchResult));
                                }
                                case "search-contents" -> {
                                    String searchPattern = (String) body.get("searchPattern");
                                    SearchResult<Post> searchResult = postController.searchContents(searchPattern);
                                    response = new Response(200, Map.of("result", searchResult));
                                }
                                default -> response = new Response(
                                        400, Map.of("error", "Unknown action for post controller."));
                            }
                        } catch (IllegalArgumentException e) {
                            response = new Response(400, Map.of(
                                    "error", "Invalid request message for post."));
                        }
                        break;
                    }
                    case "comment": {
                        // Cast to CommentController.
                        CommentController commentController = (CommentController) controller;
                        try {
                            switch (methodAction) {
                                case "create" -> {
                                    long postId = getLongFromBody(body, "postId");
                                    String userName = (String) body.get("userName");
                                    String content = (String) body.get("content");
                                    commentController.createComment(postId, userName, content);
                                    response = new Response(200, Map.of("result", "Comment created successfully"));
                                }
                                case "edit" -> {
                                    long commentId = getLongFromBody(body, "commentId");
                                    String userName = (String) body.get("userName");
                                    String content = (String) body.get("content");
                                    boolean result = commentController.editComment(commentId, userName, content);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "remove" -> {
                                    long commentId = getLongFromBody(body, "commentId");
                                    String userName = (String) body.get("userName");
                                    boolean result = commentController.removeComment(commentId, userName);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "get" -> {
                                    long commentId = getLongFromBody(body, "commentId");
                                    Comment commentResult = commentController.getCommentById(commentId);
                                    if (commentResult == null) {
                                        response = new Response(404, Map.of("error", "Comment Not Found"));
                                    } else {
                                        response = new Response(200, Map.of("result", commentResult));
                                    }
                                }
                                case "get-all" -> {
                                    List<Comment> comments = commentController.getAllComments();
                                    response = new Response(200, Map.of("result", comments));
                                }
                                case "search-contents" -> {
                                    String searchPattern = (String) body.get("searchPattern");
                                    SearchResult<Comment> searchResult = commentController.searchContents(searchPattern);
                                    response = new Response(200, Map.of("result", searchResult));
                                }
                                default -> response = new Response(
                                        400, Map.of("error", "Unknown action for comment controller."));
                            }
                        } catch (IllegalArgumentException e) {
                            response = new Response(400, Map.of(
                                    "error", "Invalid request message for comment."));
                        }
                        break;
                    }
                    default:
                        response = new Response(
                                400, Map.of("error", "Unknown controller: " + controllerName));
                }
            }
            // Write the response back to the client as a JSON string.
            writer.println(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
            try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
                Response errorResponse = new Response(500, Map.of("error", "Internal server error."));
                writer.println(gson.toJson(errorResponse));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private long getLongFromBody(Map<String, Object> body, String paramName) {
        Object paramObj = body.get(paramName);
        long paramLong;
        if (paramObj instanceof Number) {
            paramLong = ((Number) paramObj).longValue();
        } else if (paramObj instanceof String) {
            paramLong = Long.parseLong((String) paramObj);
        } else {
            throw new IllegalArgumentException("Invalid type for postId: " + paramObj);
        }

        return paramLong;
    }
}
