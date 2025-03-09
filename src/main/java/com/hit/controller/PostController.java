package com.hit.controller;

import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;
import com.hit.service.PostService;

import java.io.IOException;
import java.util.List;

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
     * Creates a new post.
     *
     * @param title    The title of the post.
     * @param userName The username of the creator.
     * @param content  The content of the post.
     * @throws IOException If an I/O error occurs during saving.
     */
    public void createPost(String title, String userName, String content) throws IOException {
        postService.createPost(title, userName, content);
    }

    /**
     * Edits an existing post.
     *
     * @param postId   The ID of the post to edit.
     * @param title    The new title.
     * @param userName The username of the editor.
     * @param content  The new content.
     * @return true if the post was edited successfully; false otherwise.
     * @throws IOException If an I/O error occurs during the operation.
     */
    public boolean editPost(Long postId, String title, String userName, String content) throws IOException {
        return postService.editPost(postId, title, userName, content);
    }

    /**
     * Removes a post.
     *
     * @param postId   The ID of the post to remove.
     * @param userName The username of the requester.
     * @return true if the post was removed successfully; false otherwise.
     * @throws IOException If an I/O error occurs during deletion.
     */
    public boolean removePost(Long postId, String userName) throws IOException {
        return postService.removePost(postId, userName);
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId The ID of the post.
     * @return The Post with the specified ID.
     * @throws IOException If an I/O error occurs during retrieval.
     */
    public Post getPostById(Long postId) throws IOException {
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
     * Retrieves all comments associated with a given post.
     *
     * @param postId The ID of the post.
     * @return A list of comments for the post.
     * @throws IOException If an I/O error occurs during retrieval.
     */
    public List<Comment> getPostComments(Long postId) throws IOException {
        return postService.getPostComments(postId);
    }

    /**
     * Searches posts by title using a string matching algorithm.
     *
     * @param searchPattern The search pattern.
     * @return A SearchResult containing posts whose titles match the search pattern.
     * @throws IOException If an I/O error occurs during the search.
     */
    public SearchResult<Post> searchTitles(String searchPattern) throws IOException {
        return postService.stringMatchingSearchTitles(searchPattern);
    }

    /**
     * Searches posts by content using a string matching algorithm.
     *
     * @param searchPattern The search pattern.
     * @return A SearchResult containing posts whose contents match the search pattern.
     * @throws IOException If an I/O error occurs during the search.
     */
    public SearchResult<Post> searchContents(String searchPattern) throws IOException {
        return postService.stringMatchingSearchContents(searchPattern);
    }
}
