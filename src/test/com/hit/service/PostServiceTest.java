package com.hit.service;

import com.hit.dao.CommentDaoImpl;
import com.hit.dao.PostDaoImpl;
import com.hit.dao.UserDaoImpl;
import com.hit.dm.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PostServiceTest {
    @TempDir
    Path tempDir;  // Temporary directory for file-based tests

    private PostDaoImpl postDao;
    private CommentDaoImpl commentDao;
    private UserDaoImpl userDao;
    private PostService postService;

    @BeforeEach
    void setUp() throws IOException {
        // Build file paths in the temporary directory for each DAO
        String postFilePath = tempDir.resolve("posts.json").toString();
        String commentFilePath = tempDir.resolve("comments.json").toString();
        String userFilePath = tempDir.resolve("users.json").toString();

        // Instantiate concrete DAO implementations that persist to file
        postDao = new PostDaoImpl(postFilePath);
        commentDao = new CommentDaoImpl(commentFilePath);
        userDao = new UserDaoImpl(userFilePath);

        // Create the PostService using the file-based DAO implementations
        postService = new PostService(postDao, commentDao, userDao);
    }

    @Test
    void testCreatePostDatabase() throws IOException {
        postService.createPost("First Post", "user1", "This is the content of the first post");

        List<Post> posts = postDao.getAll();
        assertEquals(1, posts.size(), "There should be one post created.");
        Post post = posts.getFirst();
        assertEquals("user1", post.getUserName(), "Username should match.");
        assertEquals("First Post", post.getTitle(), "Title should match.");
        assertEquals("This is the content of the first post", post.getContent(), "Content should match.");
    }

    @Test
    void testEditPostDatabase() throws IOException {
        // Create a post first
        postService.createPost("Original Title", "user1", "Original content");
        Post post = postDao.getAll().getFirst();

        // Edit the post as its owner
        boolean edited = postService.editPost(post.getId(), "Updated Title", "user1", "Updated content");
        assertTrue(edited, "Editing should succeed.");

        // Reload the post to ensure changes are persisted
        Post editedPost = postDao.find(post.getId());
        assertNotNull(editedPost, "Edited post should be found in the database.");
        assertEquals("Updated Title", editedPost.getTitle(), "Title should be updated.");
        assertEquals("Updated content", editedPost.getContent(), "Content should be updated.");
        assertTrue(editedPost.getEdited(), "Edited flag should be true.");
    }

    @Test
    void testRemovePostDatabase() throws IOException {
        // Create a post and some associated comments
        postService.createPost("Post To Remove", "user1", "Content to be removed");
        Post post = postDao.getAll().getFirst();
        // Create two comments associated with this post
        commentDao.save(new Comment(1L, post.getId(), "user1", "Comment 1", Instant.now().toEpochMilli()));
        commentDao.save(new Comment(2L, post.getId(), "user2", "Comment 2", Instant.now().toEpochMilli()));

        // Try to remove the post not as owner
        boolean removed = postService.removePost(post.getId(), "user2");
        assertFalse(removed, "Not owner should not be able to remove this comment.");

        Post NotYetRemovedPost = postDao.find(post.getId());
        // Verify the post still exists in the persistent store
        assertEquals("user1", NotYetRemovedPost.getUserName(), "Username should match.");
        assertEquals("Post To Remove", NotYetRemovedPost.getTitle(), "Title should match.");
        assertEquals("Content to be removed", NotYetRemovedPost.getContent(), "Content should match.");
        // Verify that associated comments are also still exists
        ArrayList<Comment> postsComments = new ArrayList<Comment>();
        for (Comment comment : commentDao.getAll()) {
            if (comment.getPostId() == NotYetRemovedPost.getId())
                postsComments.add(comment);
        }
        assertEquals(2, postsComments.size(), "There should be 2 Comments associated with the post.");

        // Remove the post as its owner
        removed = postService.removePost(post.getId(), "user1");
        assertTrue(removed, "Owner should be able to remove their post.");

        // Verify the post is removed
        assertNull(postDao.find(post.getId()), "Post should be removed from the database.");
        // Verify that associated comments are also removed
        for (Comment comment : commentDao.getAll()) {
            assertNotEquals(post.getId(), comment.getPostId(), "Comments associated with the removed post should be deleted.");
        }
    }

    @Test
    void testRemovePostByAdminDatabase() throws IOException {
        // Create a post and some associated comments
        postService.createPost("Post To Remove", "user1", "Content to be removed");
        Post post = postDao.getAll().getFirst();

        // Create an admin user in the user DAO
        User admin = new User("admin", "pass", User.Role.ADMIN);
        userDao.save(admin);

        // Remove the post using an admin account
        boolean removed = postService.removePost(post.getId(), "admin");
        assertTrue(removed, "admin should be able to remove this post.");

        // Verify the post is removed
        assertNull(postDao.find(post.getId()), "Post should be removed from the database.");
    }

    @Test
    void testRemovePostByModeratorDatabase() throws IOException {
        // Create a post and some associated comments
        postService.createPost("Post To Remove", "user1", "Content to be removed");
        Post post = postDao.getAll().getFirst();

        // Create a moderator user in the user DAO
        User moderator = new User("moderator", "pass", User.Role.MODERATOR);
        userDao.save(moderator);

        // Remove the post using an admin account
        boolean removed = postService.removePost(post.getId(), "moderator");
        assertTrue(removed, "admin should be able to remove this post.");

        // Verify the post is removed
        assertNull(postDao.find(post.getId()), "Post should be removed from the database.");
    }

    @Test
    void testGetPostById() throws IOException {
        postService.createPost("Test Post", "user1", "Test post content");
        Post createdPost = postDao.getAll().getFirst();

        Post fetchedPost = postService.getPostById(createdPost.getId());
        assertNotNull(fetchedPost, "Fetched post should not be null.");
        assertEquals(createdPost.getId(), fetchedPost.getId(), "IDs should match.");
        assertEquals(createdPost.getContent(), fetchedPost.getContent(), "Contents should match.");
    }

    @Test
    void testGetAllPosts() throws IOException {
        // Create multiple posts
        postService.createPost("Post One", "user1", "Content One");
        postService.createPost("Post Two", "user2", "Content Two");
        postService.createPost("Post Three", "user3", "Content Three");

        List<Post> allPosts = postService.getAllPosts();
        assertNotNull(allPosts, "The returned list should not be null.");
        assertEquals(3, allPosts.size(), "There should be three posts in total.");
    }

    @Test
    void testGetPostComments() throws IOException {
        // Create a post
        postService.createPost("Post with Comments", "user1", "Post content");
        Post post = postDao.getAll().getFirst();

        // Create comments for this post
        commentDao.save(new Comment(1L, post.getId(), "user1", "First comment", Instant.now().toEpochMilli()));
        commentDao.save(new Comment(2L, post.getId(), "user2", "Second comment", Instant.now().toEpochMilli()));
        // Create a comment for another post (should not be returned)
        commentDao.save(new Comment(3L, post.getId() + 1, "user3", "Comment for another post", Instant.now().toEpochMilli()));

        List<Comment> postComments = postService.getPostComments(post.getId());
        assertNotNull(postComments, "The returned list should not be null.");
        assertEquals(2, postComments.size(), "There should be exactly two comments for this post.");
    }

    @Test
    void testStringMatchingSearchTitles() throws IOException {
        // Create posts with and without the search pattern in their titles
        postService.createPost("A unique title", "user1", "Content A");
        postService.createPost("Searching for patterns", "user2", "Content B");
        postService.createPost("Another unique title for unique purposes", "user3", "Content C");

        String searchPattern = "unique";
        List<Post> posts = postService.getAllPosts();

        Map<Long, int[]> expectedMatches = new HashMap<>();

        for (Post post : posts) {
            if (post.getUserName().equals("user1")) {
                expectedMatches.put(post.getId(), new int[]{2});
            } else if (post.getUserName().equals("user3")) {
                expectedMatches.put(post.getId(), new int[]{8, 25});
            }
        }

        // Execute the search
        SearchResult<Post> titleSearchResult = postService.stringMatchingSearchTitles(searchPattern);

        // Expect matches in the posts whose titles contain "unique"
        assertNotNull(titleSearchResult, "Search result should not be null.");
        assertTrue(titleSearchResult.hasMatches(), "There should be at least one match found.");
        assertEquals(3, titleSearchResult.countMatches(), "There should be 3 matches found.");

        // Extract actual matches using post IDs
        Map<Long, int[]> actualMatches = new HashMap<>();
        for (MatchResult<Post> matchResult : titleSearchResult.getMatches()) {
            actualMatches.put(matchResult.getItem().getId(), matchResult.getIndexes());
        }

        // Compare posts found in both results
        assertEquals(expectedMatches.keySet(), actualMatches.keySet(),
                "Matched post IDs should be the same.");

        // Checking if actualMatches match positions is contained in expectedMatches match positions.
        for (Long postId : expectedMatches.keySet()) {
            assertArrayEquals(expectedMatches.get(postId), actualMatches.get(postId),
                    "Match positions should be identical for post ID: " + postId);
        }

        // Checking if expectedMatches match positions is contained in actualMatches match positions.
        for (Long postId : actualMatches.keySet()) {
            assertArrayEquals(expectedMatches.get(postId), actualMatches.get(postId),
                    "Match positions should be identical for post ID: " + postId);
        }
    }

    @Test
    void testStringMatchingSearchContents() throws IOException {
        // Create posts with and without the search pattern in their content
        postService.createPost("Title A", "user1", "Content with special pattern here");
        postService.createPost("Title B", "user2", "Content without the word");
        postService.createPost("Title C", "user3", "Another content with special pattern, can you see the pattern?");

        String searchPattern = "pattern";
        List<Post> posts = postService.getAllPosts();

        Map<Long, int[]> expectedMatches = new HashMap<>();

        for (Post post : posts) {
            if (post.getUserName().equals("user1")) {
                expectedMatches.put(post.getId(), new int[]{21});
            } else if (post.getUserName().equals("user3")) {
                expectedMatches.put(post.getId(), new int[]{29, 54});
            }
        }

        // Execute the search
        SearchResult<Post> contentSearchResult = postService.stringMatchingSearchContents(searchPattern);

        // Expect matches in the posts whose titles contain "pattern"
        assertNotNull(contentSearchResult, "Search result should not be null.");
        assertTrue(contentSearchResult.hasMatches(), "There should be at least one match found.");
        assertEquals(3, contentSearchResult.countMatches(), "There should be 3 matches found.");

        // Extract actual matches using post IDs
        Map<Long, int[]> actualMatches = new HashMap<>();
        for (MatchResult<Post> matchResult : contentSearchResult.getMatches()) {
            actualMatches.put(matchResult.getItem().getId(), matchResult.getIndexes());
        }

        // Compare posts found in both results
        assertEquals(expectedMatches.keySet(), actualMatches.keySet(),
                "Matched post IDs should be the same.");

        // Checking if actualMatches match positions is contained in expectedMatches match positions.
        for (Long postId : expectedMatches.keySet()) {
            assertArrayEquals(expectedMatches.get(postId), actualMatches.get(postId),
                    "Match positions should be identical for post ID: " + postId);
        }

        // Checking if expectedMatches match positions is contained in actualMatches match positions.
        for (Long postId : actualMatches.keySet()) {
            assertArrayEquals(expectedMatches.get(postId), actualMatches.get(postId),
                    "Match positions should be identical for post ID: " + postId);
        }
    }
}
