package com.hit.dao;

import com.hit.dm.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentDaoImpl implements IDao<Long, Comment> {
    private final JsonFileManager<Long, Comment> jsonFileManager;

    public CommentDaoImpl(String pathFile) {
        jsonFileManager = new JsonFileManager<>(pathFile, Long.class, Comment.class);
    }

    @Override
    public void delete(Comment commentEntity) throws IOException {
        HashMap<Long, Comment> comments = jsonFileManager.getFileData();
        if (comments != null && comments.containsKey(commentEntity.getId())) {
            comments.remove(commentEntity.getId());
            jsonFileManager.setFileData(comments);
        }
    }

    @Override
    public Comment find(Long commentId) throws IOException {
        HashMap<Long, Comment> comments = jsonFileManager.getFileData();
        if (comments == null)
            comments = new HashMap<Long, Comment>();

        return comments.get(commentId);
    }

    @Override
    public void save(Comment comment) throws IOException {
        HashMap<Long, Comment> comments = jsonFileManager.getFileData();
        if (comments == null)
            comments = new HashMap<Long, Comment>();

        comments.put(comment.getId(), comment);
        jsonFileManager.setFileData(comments);
    }

    @Override
    public List<Comment> getAll() throws IOException {
        HashMap<Long, Comment> comments = jsonFileManager.getFileData();
        if (comments == null)
            return new ArrayList<Comment>();

        return new ArrayList<>(comments.values());
    }
}
