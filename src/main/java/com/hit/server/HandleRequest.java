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
                                    boolean result = userController.createUser(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "edit" -> {
                                    boolean result = userController.editUser(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "remove" -> {
                                    boolean result = userController.removeUser(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "authenticate" -> {
                                    boolean result = userController.authenticate(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "get" -> {
                                    User userResult = userController.getUser(body);
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
                                    postController.createPost(body);
                                    response = new Response(200, Map.of("result", "Post created successfully"));
                                }
                                case "edit" -> {
                                    boolean result = postController.editPost(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "remove" -> {
                                    boolean result = postController.removePost(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "get" -> {
                                    Post postResult = postController.getPostById(body);
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
                                    List<Comment> comments = postController.getPostComments(body);
                                    response = new Response(200, Map.of("result", comments));
                                }
                                case "search-titles" -> {
                                    SearchResult<Post> searchResult = postController.searchTitles(body);
                                    response = new Response(200, Map.of("result", searchResult));
                                }
                                case "search-contents" -> {
                                    SearchResult<Post> searchResult = postController.searchContents(body);
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
                                    commentController.createComment(body);
                                    response = new Response(200, Map.of("result", "Comment created successfully"));
                                }
                                case "edit" -> {
                                    boolean result = commentController.editComment(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "remove" -> {
                                    boolean result = commentController.removeComment(body);
                                    response = new Response(200, Map.of("result", result));
                                }
                                case "get" -> {
                                    Comment commentResult = commentController.getCommentById(body);
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
                                    SearchResult<Comment> searchResult = commentController.searchContents(body);
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
