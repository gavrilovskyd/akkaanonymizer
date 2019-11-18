package ru.labs.anonymizer.messages;

public class AddServerMessage {
    private String serverName;
    private String serverAddr;

    public AddServerMessage(String serverName, String serverAddr) {
        this.serverName = serverName;
        this.serverAddr = serverAddr;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerAddr() {
        return serverAddr;
    }
}
