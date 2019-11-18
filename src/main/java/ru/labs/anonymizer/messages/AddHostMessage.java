package ru.labs.anonymizer.messages;

public class AddHostMessage {
    private String hostName;
    private String host;

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
