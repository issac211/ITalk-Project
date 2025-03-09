package com.hit.dm;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;

public class Post implements Serializable {
    private long id;
    private String userName;
    private String title;
    private String content;
    private long timestamp;
    private Boolean isEdited;

    public Post() {
    }

    public Post(long id, String title, String userName, String content, long timestamp) {
        setId(id);
        setUserName(userName);
        setTitle(title);
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", isEdited='" + isEdited + '\'' +
                ", timestamp=" + timestamp +
                ", localDateTime=" + Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault()).toLocalDateTime() +
                '}';
    }
}
