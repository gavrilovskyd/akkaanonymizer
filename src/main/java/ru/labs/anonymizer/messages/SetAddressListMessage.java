package ru.labs.anonymizer.messages;

import java.util.List;

public class SetAddressListMessage {
    private List<String> addrList;

    public SetAddressListMessage(String addrName, String addr) {
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
