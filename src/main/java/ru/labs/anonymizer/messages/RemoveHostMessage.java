package ru.labs.anonymizer.messages;

public class RemoveServerMessage {
    private String serverName;

    public RemoveServerMessage(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }
}
