package com.hit.server;

import java.util.Map;

/**
 * The Response class wraps the server’s response.
 * It typically contains a status code and a body with data or error messages.
 */
public class Response {
    private int status; // e.g., 200 for OK, 400 for bad request, 500 for server error.
    private Map<String, Object> body;

    public Response(int status, Map<String, Object> body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

}
