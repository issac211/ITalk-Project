package com.hit.dm;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;

public class Comment implements Serializable {
    private long id;
    private long postId;
    private String userName;
    private String content;
    private long timestamp;
    private Boolean isEdited;

    public Comment() {
    }

    public Comment(long id, long postId, String userName, String content, long timestamp) {
        setId(id);
        setPostId(postId);
        setUserName(userName);
        setContent(content);
        setTimestamp(timestamp);
        setEdited(false);
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", isEdited='" + isEdited + '\'' +
                ", timestamp=" + timestamp +
                ", localDateTime=" + Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault()).toLocalDateTime() +
                '}';
    }
}
