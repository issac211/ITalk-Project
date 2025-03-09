package com.hit.service;

import com.hit.algorithm.IAlgoStringMatching;
import com.hit.algorithm.KMPStringMatchingImpl;
import com.hit.dao.CommentDaoImpl;
import com.hit.dao.PostDaoImpl;
import com.hit.dao.UserDaoImpl;
import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;
import com.hit.dm.User;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PostService {
    private static long postNextId = 1; // Auto-increment ID simulation
    IAlgoStringMatching stringMatching;
    PostDaoImpl postDao;
    CommentDaoImpl commentDao;
    UserDaoImpl userDao;

    public PostService(PostDaoImpl postDao, CommentDaoImpl commentDao, UserDaoImpl userDao) throws IOException {
        this.stringMatching = new KMPStringMatchingImpl();
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.userDao = userDao;

        List<Post> posts = postDao.getAll();
        for (Post post : posts) {
            if (postNextId <= post.getId()) {
                postNextId = post.getId() + 1;
            }
        }
    }

    public void createPost(String title, String userName, String content) throws IOException {
        Instant instant = Instant.now();
        Post post = new Post(postNextId++, title, userName, content, instant.toEpochMilli());
        postDao.save(post);
    }

    public boolean editPost(Long postId, String title, String userName, String content) throws IOException {
        Post post = postDao.find(postId);
        if (post != null) {
            if (post.getUserName().equals(userName)) {
                post.setContent(content);
                post.setTitle(title);
                post.setEdited(true);
                postDao.save(post);
                return true;
            }
        }
        return false;
    }

    public boolean removePost(Long postId, String userName) throws IOException {
        Post post = postDao.find(postId);
        User user = userDao.find(userName);
        boolean authorizedUser = false;

        if (user != null) {
            authorizedUser = user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.MODERATOR;
        }

        if (post != null) {
            if (post.getUserName().equals(userName) || authorizedUser) {
                ArrayList<Comment> toDelete = new ArrayList<>();
                for (Comment comment : commentDao.getAll()) {
                    if (comment.getPostId() == postId) {
                        toDelete.add(comment);
                    }
                }

                for (Comment comment : toDelete) {
                    commentDao.delete(comment);
                }

                postDao.delete(post);
                return true;
            }
        }
        return false;
    }

    public Post getPostById(Long postId) throws IOException {
        return postDao.find(postId);
    }

    public List<Post> getAllPosts() throws IOException {
        return postDao.getAll();
    }

    public List<Comment> getPostComments(Long postId) throws IOException {
        ArrayList<Comment> postComments = new ArrayList<>();
        for (Comment comment : commentDao.getAll()) {
            if (comment.getPostId() == postId) {
                postComments.add(comment);
            }
        }

        return postComments;
    }

    public SearchResult<Post> stringMatchingSearchTitles(String searchPattern) throws IOException {
        List<Post> posts = postDao.getAll();
        SearchResult<Post> titleSearchResult = new SearchResult<>(searchPattern);
        for (Post post : posts) {
            int[] titleIndexes = stringMatching.search(post.getTitle().toLowerCase(), searchPattern.toLowerCase());
            if (titleIndexes.length > 0) {
                titleSearchResult.addMatch(post, titleIndexes);
            }
        }
        return titleSearchResult;
    }

    public SearchResult<Post> stringMatchingSearchContents(String searchPattern) throws IOException {
        List<Post> posts = postDao.getAll();
        SearchResult<Post> contentSearchResult = new SearchResult<>(searchPattern);
        for (Post post : posts) {
            int[] contentIndexes = stringMatching.search(post.getContent().toLowerCase(), searchPattern.toLowerCase());
            if (contentIndexes.length > 0) {
                contentSearchResult.addMatch(post, contentIndexes);
            }
        }
        return contentSearchResult;
    }
}
