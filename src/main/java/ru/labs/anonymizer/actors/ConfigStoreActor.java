package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import ru.labs.anonymizer.messages.AddServerMessage;
import ru.labs.anonymizer.messages.RemoveServerMessage;

import java.util.HashMap;

public class ConfigStoreActor extends AbstractActor {
    private HashMap<String, String> serversStorage = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(AddServerMessage.class, m -> {
                serversStorage.put(m.getServerName(), m.getServerAddr());
            })
            .match(RemoveServerMessage.class, m -> {
                serversStorage.remove(m.getServerName());
            })
            .build();
    }
}
