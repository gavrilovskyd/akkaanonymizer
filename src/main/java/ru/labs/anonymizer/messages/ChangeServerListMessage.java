package ru.labs.anonymizer.messages;

public class ChangeServerListMessage {
    public enum EventType {
        ADD,
        REMOVE
    };

    private String serverAddr;
    private EventType type;
}
