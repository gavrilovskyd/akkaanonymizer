package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import ru.labs.anonymizer.messages.AddAddressMessage;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;
import ru.labs.anonymizer.messages.RemoveAddressMessage;

import java.util.HashMap;
import java.util.Random;

public class AddressStoreActor extends AbstractActor {
    private HashMap<String, String> hostsStorage = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(AddAddressMessage.class, m ->
                hostsStorage.put(m.getHostName(), m.getHost())
            )
            .match(RemoveAddressMessage.class, m ->
                hostsStorage.remove(m.getHostName())
            )
            .match(GetRandomAddressMessage.class, m -> {
                Object[] servers = hostsStorage.values().toArray();
                getSender().tell(servers[new Random().nextInt(servers.length)], getSelf());
            })
            .build();
    }
}
