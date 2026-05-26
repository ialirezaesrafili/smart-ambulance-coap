package com.ambulance.enums;

/**
 * Represents the lifecycle states of an Ambulance CoAP server.
 * Each state has a numeric code (compatible with CoAP response codes
 * or internal status) and a human‑readable message.
 * <p>
 * The enum also provides a mapping from code to state for easy lookup.
 */
public enum ServerState {

    /**
     * Server has been created but not yet started.
     */
    CREATED(0, "Server created"),

    /**
     * Server is in the process of starting (binding sockets, registering resources).
     */
    STARTING(100, "Server is starting"),

    /**
     * Server is fully operational and ready to handle requests.
     */
    RUNNING(200, "Server is running"),

    /**
     * Server is in the process of stopping (unbinding, cleaning up).
     */
    STOPPING(300, "Server is stopping"),

    /**
     * Server has stopped and is no longer accepting requests.
     */
    STOPPED(301, "Server stopped"),

    /**
     * Server has encountered a critical error and may require restart.
     */
    ERROR(400, "Server error");

    private final int code;
    private final String message;

    ServerState(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @return the numeric status code (compatible with CoAP 5.xx style errors, etc.)
     */
    public int getCode() {
        return code;
    }

    /**
     * @return a short human‑readable description
     */
    public String getMessage() {
        return message;
    }

    /**
     * Lookup a ServerState by its numeric code.
     *
     * @param code the status code
     * @return the matching ServerState, or {@link #ERROR} if not found
     */
    public static ServerState fromCode(int code) {
        for (ServerState state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        return ERROR; // safe fallback
    }

    /**
     * Check if the server is in a state where it can accept CoAP requests.
     *
     * @return true only if the server is RUNNING.
     */
    public boolean isOperational() {
        return this == RUNNING;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, message);
    }
}