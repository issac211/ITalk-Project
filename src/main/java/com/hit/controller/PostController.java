package com.hit.controller;

import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;
import com.hit.service.PostService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The PostController exposes API endpoints for operations on Posts.
 * It acts as a façade between the networking layer and the business logic in PostService.
 */
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * <h5> Creates a new post based on the provided request body. </h5>
     * This method extracts the `title`, `userName`, and `content` from the given
     * request body map and delegates the post creation process to the `postService`.
     *
     * @param requestBody a map containing the post details:<br>
     *                    <ul>
     *                      <li> "title": the title of the post (String) </li>
     *                      <li> "userName": the username of the creator (String) </li>
     *                      <li> "content": the content of the post (String) </li>
     *                    </ul>
     * @throws IOException if an I/O error occurs during saving
     */
    public void createPost(Map<String, Object> requestBody) throws IOException {
        String title = (String) requestBody.get("title");
        String userName = (String) requestBody.get("userName");
        String content = (String) requestBody.get("content");
        postService.createPost(title, userName, content);
    }

    /**
     * <h5> Edits an existing post based on the provided request body. </h5>
     * This method extracts the `postId`, `title`, `userName`, and `content` from the given
     * request body map and delegates the post editing process to the `postService`.
     *
     * @param requestBody a map containing the post details:<br>
     *                    <ul>
     *                      <li> "postId": the ID of the post to edit (Long) </li>
     *                      <li> "title": the new title of the post (String) </li>
     *                      <li> "userName": the username of the editor (String) </li>
     *                      <li> "content": the new content of the post (String) </li>
     *                    </ul>
     * @return true if the post was edited successfully; false otherwise
     * @throws IOException if an I/O error occurs during the operation
     */
    public boolean editPost(Map<String, Object> requestBody) throws IOException {
        long postId = getLongFromBody(requestBody, "postId");
        String title = (String) requestBody.get("title");
        String userName = (String) requestBody.get("userName");
        String content = (String) requestBody.get("content");
        return postService.editPost(postId, title, userName, content);
    }

    /**
     * <h5> Removes a post based on the provided request body. </h5>
     * This method extracts the `postId` and `userName` from the given
     * request body map and delegates the post removal process to the `postService`.
     *
     * @param requestBody a map containing the post details:<br>
     *                    <ul>
     *                      <li> "postId": the ID of the post to remove (Long) </li>
     *                      <li> "userName": the username of the requester (String) </li>
     *                    </ul>
     * @return true if the post was removed successfully; false otherwise
     * @throws IOException if an I/O error occurs during deletion
     */
    public boolean removePost(Map<String, Object> requestBody) throws IOException {
        long postId = getLongFromBody(requestBody, "postId");
        String userName = (String) requestBody.get("userName");
        return postService.removePost(postId, userName);
    }

    /**
     * <h5> Retrieves a post by its ID from the provided request body. </h5>
     * This method extracts the `postId` from the given request body map
     * and delegates the retrieval process to the `postService`.
     *
     * @param requestBody a map containing the post details:<br>
     *                    <ul>
     *                      <li> "postId": the ID of the post to retrieve (Long) </li>
     *                    </ul>
     * @return the Post object with the specified ID
     * @throws IOException if an I/O error occurs during retrieval
     */
    public Post getPostById(Map<String, Object> requestBody) throws IOException {
        long postId = getLongFromBody(requestBody, "postId");
        return postService.getPostById(postId);
    }

    /**
     * Retrieves all posts.
     *
     * @return A list of all posts.
     * @throws IOException If an I/O error occurs during retrieval.
     */
    public List<Post> getAllPosts() throws IOException {
        return postService.getAllPosts();
    }

    /**
     * <h5> Retrieves all comments associated with a given post. </h5>
     * This method extracts the `postId` from the provided request body map
     * and delegates the retrieval of comments to the `postService`.
     *
     * @param requestBody a map containing the post details:
     *                    <ul>
     *                      <li> "postId": the ID of the post to retrieve comments for (Long) </li>
     *                    </ul>
     * @return a list of comments associated with the specified post
     * @throws IOException if an I/O error occurs during retrieval
     */
    public List<Comment> getPostComments(Map<String, Object> requestBody) throws IOException {
        long postId = getLongFromBody(requestBody, "postId");
        return postService.getPostComments(postId);
    }

    /**
     * <h5> Searches for posts by their title using a string matching algorithm. </h5>
     * This method retrieves the search pattern from the request body and
     * delegates the search operation to the `postService`.
     *
     * @param requestBody a map containing the search details:
     *                    <ul>
     *                      <li> "searchPattern": the pattern to search for in post titles (String) </li>
     *                    </ul>
     * @return a `SearchResult` containing posts whose titles match the search pattern
     * @throws IOException if an I/O error occurs during the search
     */
    public SearchResult<Post> searchTitles(Map<String, Object> requestBody) throws IOException {
        String searchPattern = (String) requestBody.get("searchPattern");
        return postService.stringMatchingSearchTitles(searchPattern);
    }

    /**
     * <h5> Searches posts by content using a string matching algorithm. </h5>
     * This method retrieves the search pattern from the request body and
     * delegates the search operation to the `postService`.
     *
     * @param requestBody a map containing the search details:
     *                    <ul>
     *                      <li> "searchPattern": the pattern to search for in post contents (String) </li>
     *                    </ul>
     * @return a `SearchResult` containing posts whose contents match the search pattern
     * @throws IOException if an I/O error occurs during the search
     */
    public SearchResult<Post> searchContents(Map<String, Object> requestBody) throws IOException {
        String searchPattern = (String) requestBody.get("searchPattern");
        return postService.stringMatchingSearchContents(searchPattern);
    }

    private long getLongFromBody(Map<String, Object> body, String paramName) {
        Object paramObj = body.get(paramName);
        long paramLong;
        if (paramObj instanceof Number) {
            paramLong = ((Number) paramObj).longValue();
        } else if (paramObj instanceof String) {
            paramLong = Long.parseLong((String) paramObj);
        } else {
            throw new IllegalArgumentException("Invalid type for " + paramName + ": " + paramObj);
        }

        return paramLong;
    }
}
