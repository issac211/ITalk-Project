package com.hit.server;

import java.util.Map;

/**
 * The Response class wraps the server’s response.
 * It typically contains an action and a body with data.
 */
public class Request {
    private String action; // e.g., "user/create" or "post/edit"
    private Map<String, Object> body;

    public Request(String action, Map<String, Object> body) {
        this.action = action;
        this.body = body;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }
}
