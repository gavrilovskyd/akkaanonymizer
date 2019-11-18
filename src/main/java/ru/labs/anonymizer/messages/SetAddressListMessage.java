package ru.labs.anonymizer.messages;

import java.util.List;

public class SetAddressListMessage {
    private String[] addrList;

    public SetAddressListMessage(String[] addrList) {
        this.addrList = addrList;
    }

    public List<String> getAddrList() {
        return addrList;
    }
}
