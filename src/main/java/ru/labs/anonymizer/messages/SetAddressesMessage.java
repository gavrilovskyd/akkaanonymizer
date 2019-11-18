package ru.labs.anonymizer.messages;

import java.util.List;

public class SetAddressesMessage {
    private String[] addresses;

    public SetAddressesMessage(String[] addresses) {
        this.addresses = addresses;
    }

    public String[] getAddresses() {
        return addresses;
    }
}
