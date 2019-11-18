package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import ru.labs.anonymizer.messages.AddHostMessage;
import ru.labs.anonymizer.messages.GetRandomHostMessage;
import ru.labs.anonymizer.messages.RemoveHostMessage;

import java.util.HashMap;

public class HostStoreActor extends AbstractActor {
    private HashMap<String, String> hostsStorage = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(AddHostMessage.class, m ->
                hostsStorage.put(m.getServerName(), m.getServerAddr())
            )
            .match(RemoveHostMessage.class, m ->
                hostsStorage.remove(m.getServerName())
            )
            .match(GetRandomHostMessage.class, m -> {
                Object[] servers = hostsStorage.values().toArray();

            })
            .build();
    }
}
