package io.xserverless.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    public static final String TYPE_FUNCTION_LOAD = "function.load";
    public static final String TYPE_FUNCTION_UNLOAD = "function.unload";
    public static final String TYPE_REQUEST_STARTED = "request.started";
    public static final String TYPE_REQUEST_COMPLETED = "request.completed";
    public static final String TYPE_SERVER_START = "server.start";
    public static final String TYPE_SERVER_HEARTBEAT = "server.heartbeat";

    private String type;
    private long timestamp;
    private long functionId;
    private String envDomain;
    private String server;
}
