package com.ambulance.core;

public final class ServerConfig {

    public static final String HOST =
            "0.0.0.0";

    public static final int PORT =
            5683;

    public static final String PROTOCOL =
            "coap";

    public static final String BASE_URI =
            PROTOCOL +
                    "://" +
                    HOST +
                    ":" +
                    PORT;

    private ServerConfig() {
    // next features for error handling here v2
    }
}