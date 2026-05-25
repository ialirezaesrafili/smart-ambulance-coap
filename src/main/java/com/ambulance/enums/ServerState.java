package com.ambulance.enums;

public enum ServerState {

    // Server State will be given two code and message for each therefore
    STARTING(100, "Server is Starting"),
    RUNNING(200, "Server is Running"),
    STOPPING(300, "Server is Stopping"),
    ERROR(400, "Server Error");


    // private construction
    private final int code;
    private final String message;

    ServerState(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
