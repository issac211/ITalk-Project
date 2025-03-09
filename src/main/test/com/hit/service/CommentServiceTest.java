package com.hit.service;

import com.hit.dao.CommentDaoImpl;
import com.hit.dao.UserDaoImpl;
import com.hit.dm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {
    @TempDir
    Path tempDir;  // Temporary directory provided by JUnit for file-based tests

    private CommentDaoImpl commentDao;
    private UserDaoImpl userDao;
    private CommentService commentService;

    @BeforeEach
    void setUp() throws IOException {
        // Create file paths in the temporary directory for each DAO
        String commentFilePath = tempDir.resolve("comments.json").toString();
        String userFilePath = tempDir.resolve("users.json").toString();

        // Instantiate the concrete DAO implementations that persist to file
        commentDao = new CommentDaoImpl(commentFilePath);
        userDao = new UserDaoImpl(userFilePath);

        // Create the service instance using the DAOs
        commentService = new CommentService(commentDao, userDao);
    }

    @Test
    void testCreateCommentDatabase() throws IOException {
        // Create a new comment using the service method
        commentService.createComment(1L, "user1", "This is a comment");

        // Retrieve all comments from the file-backed DAO
        List<Comment> comments = commentDao.getAll();

        // Verify that one comment has been persisted to the "database"
        assertEquals(1, comments.size(), "There should be one comment created.");
        Comment comment = comments.getFirst();
        assertEquals("user1", comment.getUserName(), "Username should match.");
        assertEquals("This is a comment", comment.getContent(), "Content should match.");
    }

    @Test
    void testEditCommentDatabase() throws IOException {
        // Create a comment first
        commentService.createComment(1L, "user1", "Original comment");
        Comment comment = commentDao.getAll().getFirst();

        // Edit the comment using the service method
        boolean edited = commentService.editComment(comment.getId(), "user1", "Edited comment");
        assertTrue(edited, "Editing should succeed.");

        // Reload the comment from the DAO to ensure changes are persisted
        Comment editedComment = commentDao.find(comment.getId());
        assertNotNull(editedComment, "Edited comment should be found in the database.");
        assertEquals("Edited comment", editedComment.getContent(), "Content should be updated.");
        assertTrue(editedComment.getEdited(), "Edited flag should be true.");
    }

    @Test
    void testRemoveCommentDatabase() throws IOException {
        // Create a comment using the service
        commentService.createComment(1L, "user1", "Comment to remove");
        Comment comment = commentDao.getAll().getFirst();

        // Try to remove the comment not as owner
        boolean removed = commentService.removeComment(comment.getId(), "user2");
        assertFalse(removed, "Not owner should not be able to remove this comment.");

        // Verify the comment still exists in the persistent store
        Comment NotYetRemovedComment = commentDao.find(comment.getId());
        assertEquals(1L, NotYetRemovedComment.getPostId(), "PostID should match.");
        assertEquals("user1", NotYetRemovedComment.getUserName(), "Username should match.");
        assertEquals("Comment to remove", NotYetRemovedComment.getContent(), "Content should match.");

        // Remove the comment as its owner
        removed = commentService.removeComment(comment.getId(), "user1");
        assertTrue(removed, "Owner should be able to remove their comment.");

        // Verify the comment no longer exists in the persistent store
        Comment removedComment = commentDao.find(comment.getId());
        assertNull(removedComment, "Comment should be removed from the database.");
    }

    @Test
    void testRemoveCommentByAdminDatabase() throws IOException {
        // Create a comment by "user1"
        commentService.createComment(1L, "user1", "Comment to remove by admin");
        Comment comment = commentDao.getAll().getFirst();

        // Create an admin user in the user DAO
        User admin = new User("admin", "pass", User.Role.ADMIN);
        userDao.save(admin);

        // Remove the comment using an admin account
        boolean removed = commentService.removeComment(comment.getId(), "admin");
        assertTrue(removed, "Admin should be able to remove any comment.");

        // Verify that the comment has been removed from the persistent store
        Comment removedComment = commentDao.find(comment.getId());
        assertNull(removedComment, "Comment should be removed from the database by admin.");
    }

    @Test
    void testGetCommentById() throws IOException {
        // Create a comment
        commentService.createComment(1L, "user1", "Test comment for getCommentById");
        Comment createdComment = commentDao.getAll().getFirst();

        // Retrieve the comment by its ID using the service
        Comment fetchedComment = commentService.getCommentById(createdComment.getId());
        assertNotNull(fetchedComment, "Fetched comment should not be null.");
        assertEquals(createdComment.getId(), fetchedComment.getId(), "The IDs should match.");
        assertEquals(createdComment.getContent(), fetchedComment.getContent(), "The content should match.");
    }

    @Test
    void testGetAllComments() throws IOException {
        // Create multiple comments
        commentService.createComment(1L, "user1", "First comment");
        commentService.createComment(1L, "user2", "Second comment");
        commentService.createComment(1L, "user3", "Third comment");

        // Retrieve all comments using the service
        List<Comment> allComments = commentService.getAllComments();
        assertNotNull(allComments, "The returned list should not be null.");
        assertEquals(3, allComments.size(), "There should be three comments in total.");
    }

    @Test
    void testStringMatchingSearchContents() throws IOException {
        // Create comments that include a common search pattern
        commentService.createComment(1L, "user1", "This is a test comment with pattern");
        commentService.createComment(1L, "user2", "Another comment without it");
        commentService.createComment(1L, "user3", "Yet another test comment for testing");

        String searchPattern = "test";
        List<Comment> comments = commentService.getAllComments();

        Map<Long, int[]> expectedMatches = new HashMap<>();

        for (Comment comment : comments) {
            if (comment.getUserName().equals("user1")) {
                expectedMatches.put(comment.getId(), new int[]{10});
            } else if (comment.getUserName().equals("user3")) {
                expectedMatches.put(comment.getId(), new int[]{12, 29});
            }
        }

        // Execute the search
        SearchResult<Comment> searchResult = commentService.stringMatchingSearchContents(searchPattern);

        // The search result should include matches from at least the two comments that contain "test"
        assertNotNull(searchResult, "Search result should not be null.");
        assertTrue(searchResult.hasMatches(), "There should be at least one match found.");
        assertEquals(3, searchResult.countMatches(), "There should be 3 matches found.");

        // Extract actual matches using comment IDs
        Map<Long, int[]> actualMatches = new HashMap<>();
        for (MatchResult<Comment> matchResult : searchResult.getMatches()) {
            actualMatches.put(matchResult.getItem().getId(), matchResult.getIndexes());
        }

        // Compare comments found in both results
        assertEquals(expectedMatches.keySet(), actualMatches.keySet(),
                "Matched comment IDs should be the same.");

        // Checking if actualMatches match positions is contained in expectedMatches match positions.
        for (Long commentId : expectedMatches.keySet()) {
            assertArrayEquals(expectedMatches.get(commentId), actualMatches.get(commentId),
                    "Match positions should be identical for comment ID: " + commentId);
        }

        // Checking if expectedMatches match positions is contained in actualMatches match positions.
        for (Long commentId : actualMatches.keySet()) {
            assertArrayEquals(expectedMatches.get(commentId), actualMatches.get(commentId),
                    "Match positions should be identical for comment ID: " + commentId);
        }
    }
}
