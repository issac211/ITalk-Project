package com.hit.controller;

import com.hit.dm.Comment;
import com.hit.dm.SearchResult;
import com.hit.service.CommentService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * <h5> Creates a new comment based on the provided request body. </h5>
     * This method extracts the `postId`, `userName`, and `content` from the given
     * request body map and delegates the comment creation process to the `commentService`.
     *
     * @param requestBody a map containing the comment details:<br>
     *                    <ul>
     *                      <li> "postId": the ID of the post to which the comment belongs (Long) </li>
     *                      <li> "userName": the username of the comment creator (String) </li>
     *                      <li> "content": the text content of the comment (String) </li>
     *                    </ul>
     * @throws IOException if an I/O error occurs during saving
     */
    public void createComment(Map<String, Object> requestBody) throws IOException {
        long postId = getLongFromBody(requestBody, "postId");
        String userName = (String) requestBody.get("userName");
        String content = (String) requestBody.get("content");
        commentService.createComment(postId, userName, content);
    }

    /**
     * <h5> Edits an existing comment based on the provided request body. </h5>
     * This method extracts the `commentId`, `userName`, and `content` from the given
     * request body map and delegates the comment editing process to the `commentService`.
     *
     * @param requestBody a map containing the comment details:<br>
     *                    <ul>
     *                      <li> "commentId": the ID of the comment to edit (Long) </li>
     *                      <li> "userName": the username of the editor (String) </li>
     *                      <li> "content": the new text content for the comment (String) </li>
     *                    </ul>
     * @return true if the comment was edited successfully; false otherwise
     * @throws IOException if an I/O error occurs during the operation
     */
    public boolean editComment(Map<String, Object> requestBody) throws IOException {
        long commentId = getLongFromBody(requestBody, "commentId");
        String userName = (String) requestBody.get("userName");
        String content = (String) requestBody.get("content");
        return commentService.editComment(commentId, userName, content);
    }

    /**
     * <h5> Removes a comment based on the provided request body. </h5>
     * This method extracts the `commentId` and `userName` from the given
     * request body map and delegates the comment removal process to the `commentService`.
     *
     * @param requestBody a map containing the comment details:<br>
     *                    <ul>
     *                      <li> "commentId": the ID of the comment to remove (Long) </li>
     *                      <li> "userName": the username of the requester (String) </li>
     *                    </ul>
     * @return true if the comment was removed successfully; false otherwise
     * @throws IOException if an I/O error occurs during deletion
     */
    public boolean removeComment(Map<String, Object> requestBody) throws IOException {
        long commentId = getLongFromBody(requestBody, "commentId");
        String userName = (String) requestBody.get("userName");
        return commentService.removeComment(commentId, userName);
    }

    /**
     * <h5> Retrieves a comment by its ID from the provided request body. </h5>
     * This method extracts the `commentId` from the given request body map
     * and delegates the retrieval process to the `commentService`.
     *
     * @param requestBody a map containing the comment details:<br>
     *                    <ul>
     *                      <li> "commentId": the ID of the comment to retrieve (Long) </li>
     *                    </ul>
     * @return the Comment object with the specified ID, or null if not found.
     * @throws IOException if an I/O error occurs.
     */
    public Comment getCommentById(Map<String, Object> requestBody) throws IOException {
        long commentId = getLongFromBody(requestBody, "commentId");
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
     * <h5> Searches the contents of comments for a given search pattern. </h5>
     * This method retrieves the search pattern from the request body and
     * delegates the search operation to the `commentService`.
     *
     * @param requestBody a map containing the search details:<br>
     *                    <ul>
     *                      <li> "searchPattern": the pattern to search for in comment contents (String) </li>
     *                    </ul>
     * @return a `SearchResult` containing comments whose contents match the search pattern
     * @throws IOException if an I/O error occurs during the search
     */
    public SearchResult<Comment> searchContents(Map<String, Object> requestBody) throws IOException {
        String searchPattern = (String) requestBody.get("searchPattern");
        return commentService.stringMatchingSearchContents(searchPattern);
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
