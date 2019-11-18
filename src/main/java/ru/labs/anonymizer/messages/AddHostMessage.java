package ru.labs.anonymizer.messages;

public class AddHostMessage {
    private String hostName;
    private String host;

    public AddHostMessage(String hostName, String host) {
        this.hostName = hostName;
        this.host = host;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerAddr() {
        return serverAddr;
    }
}
