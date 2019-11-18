package ru.labs.anonymizer.messages;

public class RemoveAddressMessage {
    private String hostName;

    public RemoveAddressMessage(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }
}
