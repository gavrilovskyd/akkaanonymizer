package ru.labs.anonymizer.messages;

public class ChangeServerListMessage {
    public enum EventType {
        ADD,
        REMOVE
    };

    private String serverAddr;
    private EventType type;

    public ChangeServerListMessage(String serverAddr, EventType type) {
        this.serverAddr = serverAddr;
        this.type = type;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public EventType getType() {
        return type;
    }
}
