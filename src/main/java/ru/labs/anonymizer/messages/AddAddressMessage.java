package ru.labs.anonymizer.messages;

public class AddHostMessage {
    private String hostName;
    private String host;

    public AddHostMessage(String hostName, String host) {
        this.hostName = hostName;
        this.host = host;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHost() {
        return host;
    }
}
