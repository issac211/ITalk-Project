package com.hit.dao;

import com.hit.dm.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDaoImpl implements IDao<Long, Post> {
    private final JsonFileManager<Long, Post> jsonFileManager;

    public PostDaoImpl(String pathFile) {
        jsonFileManager = new JsonFileManager<>(pathFile, Long.class, Post.class);
    }

    @Override
    public void delete(Post postEntity) throws IOException {
        HashMap<Long, Post> posts = jsonFileManager.getFileData();
        if (posts != null && posts.containsKey(postEntity.getId())) {
            posts.remove(postEntity.getId());
            jsonFileManager.setFileData(posts);
        }
    }

    @Override
    public Post find(Long postId) throws IOException {
        HashMap<Long, Post> posts = jsonFileManager.getFileData();
        if (posts == null)
            posts = new HashMap<Long, Post>();

        return posts.get(postId);
    }

    @Override
    public void save(Post post) throws IOException {
        HashMap<Long, Post> posts = jsonFileManager.getFileData();
        if (posts == null)
            posts = new HashMap<Long, Post>();

        posts.put(post.getId(), post);
        jsonFileManager.setFileData(posts);
    }

    @Override
    public List<Post> getAll() throws IOException {
        HashMap<Long, Post> posts = jsonFileManager.getFileData();
        if (posts == null)
            return new ArrayList<Post>();

        return new ArrayList<>(posts.values());
    }
}
