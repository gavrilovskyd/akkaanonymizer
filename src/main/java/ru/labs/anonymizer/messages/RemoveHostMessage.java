package ru.labs.anonymizer.messages;

public class RemoveHostMessage {
    private String hostName;

    public RemoveHostMessage(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }
}
