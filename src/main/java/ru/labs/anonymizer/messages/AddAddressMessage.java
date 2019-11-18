package ru.labs.anonymizer.messages;

public class AddAddressMessage {
    private String addrName;
    private String addr;

    public AddAddressMessage(String hostName, String host) {
        this.addrName = hostName;
        this.addr = host;
    }

    public String getAddrName() {
        return addrName;
    }

    public String getAddr() {
        return addr;
    }
}
