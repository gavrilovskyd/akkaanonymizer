package ru.labs.anonymizer.messages;

import java.util.List;

public class SetAddressesMessage {
    private String[] addresses;

    public SetAddressesMessage(String[] addrList) {
        this.addrList = addrList;
    }

    public String[] getAddrList() {
        return addrList;
    }
}
