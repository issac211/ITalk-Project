package com.hit.service;

import com.hit.algorithm.IAlgoStringMatching;
import com.hit.algorithm.KMPStringMatchingImpl;
import com.hit.dao.CommentDaoImpl;
import com.hit.dao.UserDaoImpl;
import com.hit.dm.Comment;
import com.hit.dm.SearchResult;
import com.hit.dm.User;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class CommentService {
    private static long commentNextId = 1; // Auto-increment ID simulation
    IAlgoStringMatching stringMatching;
    UserDaoImpl userDao;
    CommentDaoImpl commentDao;

    public CommentService(CommentDaoImpl commentDao, UserDaoImpl userDao) throws IOException {
        this.stringMatching = new KMPStringMatchingImpl();
        this.userDao = userDao;
        this.commentDao = commentDao;

        List<Comment> comments = commentDao.getAll();
        for (Comment comment : comments) {
            if (commentNextId < comment.getId()) {
                commentNextId = comment.getId() + 1;
            }
        }
    }

    public void createComment(Long postId, String userName, String content) throws IOException {
        Instant instant = Instant.now();
        Comment comment = new Comment(commentNextId++, postId, userName, content, instant.toEpochMilli());
        commentDao.save(comment);
    }

    public boolean editComment(Long commentId, String userName, String content) throws IOException {
        Comment comment = commentDao.find(commentId);
        if (comment != null && comment.getUserName().equals(userName)) {
            comment.setContent(content);
            comment.setEdited(true);
            commentDao.save(comment);
            return true;
        }
        return false;
    }

    public boolean removeComment(Long commentId, String userName) throws IOException {
        Comment comment = commentDao.find(commentId);
        User user = userDao.find(userName);
        boolean authorizedUser = false;

        if (user != null) {
            authorizedUser = user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.MODERATOR;
        }

        if (comment != null) {
            if (comment.getUserName().equals(userName) || authorizedUser) {
                commentDao.delete(comment);
                return true;
            }
        }
        return false;
    }

    public Comment getCommentById(Long commentId) throws IOException {
        return commentDao.find(commentId);
    }

    public List<Comment> getAllComments() throws IOException {
        return commentDao.getAll();
    }

    public SearchResult<Comment> stringMatchingSearchContents(String searchPattern) throws IOException {
        List<Comment> comments = commentDao.getAll();
        SearchResult<Comment> contentSearchResult = new SearchResult<>(searchPattern);
        for (Comment comment : comments) {
            int[] contentIndexes = stringMatching.search(comment.getContent().toLowerCase(), searchPattern.toLowerCase());
            if (contentIndexes.length > 0) {
                contentSearchResult.addMatch(comment, contentIndexes);
            }
        }
        return contentSearchResult;
    }
}
