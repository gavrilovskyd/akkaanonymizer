package ru.labs.anonymizer.messages;

public class AddAddressMessage {
    private String addrName;
    private String addr;

    public AddAddressMessage(String addrName, String addr) {
        this.addrName = addrName;
        this.addr = addr;
    }

    public String getAddrName() {
        return addrName;
    }

    public String getAddr() {
        return addr;
    }
}
