package com.hit.controller;

import com.hit.dm.Comment;
import com.hit.dm.SearchResult;
import com.hit.service.CommentService;

import java.io.IOException;
import java.util.List;

/**
 * The CommentController exposes API endpoints for operations on Comments.
 * It acts as a façade between the networking layer and the business logic in CommentService.
 */
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Creates a new comment.
     *
     * @param postId   the ID of the post to which the comment belongs.
     * @param userName the username of the comment creator.
     * @param content  the text content of the comment.
     * @throws IOException if an I/O error occurs.
     */
    public void createComment(Long postId, String userName, String content) throws IOException {
        commentService.createComment(postId, userName, content);
    }

    /**
     * Edits an existing comment.
     *
     * @param commentId the ID of the comment to edit.
     * @param userName  the username of the editor.
     * @param content   the new text content for the comment.
     * @return true if the comment was edited successfully; false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean editComment(Long commentId, String userName, String content) throws IOException {
        return commentService.editComment(commentId, userName, content);
    }

    /**
     * Removes a comment.
     *
     * @param commentId the ID of the comment to remove.
     * @param userName  the username of the requester.
     * @return true if the comment was removed successfully; false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean removeComment(Long commentId, String userName) throws IOException {
        return commentService.removeComment(commentId, userName);
    }

    /**
     * Retrieves a comment by its ID.
     *
     * @param commentId the ID of the comment.
     * @return the Comment object, or null if not found.
     * @throws IOException if an I/O error occurs.
     */
    public Comment getCommentById(Long commentId) throws IOException {
        return commentService.getCommentById(commentId);
    }

    /**
     * Retrieves all comments.
     *
     * @return a list of all comments.
     * @throws IOException if an I/O error occurs.
     */
    public List<Comment> getAllComments() throws IOException {
        return commentService.getAllComments();
    }

    /**
     * Searches the contents of comments for a given search pattern.
     *
     * @param searchPattern the pattern to search for.
     * @return a SearchResult containing comments and the matching indexes.
     * @throws IOException if an I/O error occurs.
     */
    public SearchResult<Comment> searchContents(String searchPattern) throws IOException {
        return commentService.stringMatchingSearchContents(searchPattern);
    }
}
