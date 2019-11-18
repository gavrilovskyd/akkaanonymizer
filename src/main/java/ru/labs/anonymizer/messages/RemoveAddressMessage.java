package ru.labs.anonymizer.messages;

public class RemoveAddressMessage {
    private String addrName;

    public RemoveAddressMessage(String hostName) {
        this.addrName = hostName;
    }

    public String getAddrName() {
        return addrName;
    }
}
