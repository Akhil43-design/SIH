package com.routeoptimizer;

import java.time.Instant;

public class ApiErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error != null ? error : "INTERNAL_SERVER_ERROR";
        this.message = message;
        this.path = path;
    }

    public ApiErrorResponse() {
        this(500, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", "");
    }

    public String getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}
