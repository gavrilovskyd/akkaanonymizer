package ru.labs.anonymizer.messages;

public class AddHostMessage {
    private String serverName;
    private String serverAddr;

    public AddHostMessage(String serverName, String serverAddr) {
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
