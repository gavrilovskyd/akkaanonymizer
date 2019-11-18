package ru.labs.anonymizer.messages;

public class RemoveHostMessage {
    private String serverName;

    public RemoveHostMessage(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }
}
