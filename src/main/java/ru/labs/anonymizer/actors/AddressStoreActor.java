package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ru.labs.anonymizer.messages.AddAddressMessage;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;
import ru.labs.anonymizer.messages.RemoveAddressMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AddressStoreActor extends AbstractActor {
    private HashMap<String, String> hostsStorage = new HashMap<>();
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(List<String>.class, m -> {
                hostsStorage.put(m.getAddrName(), m.getAddr());
                logger.info("added {} with name {}", m.getAddr(), m.getAddrName());
            })
            .match(RemoveAddressMessage.class, m -> {
                hostsStorage.remove(m.getAddrName());
                logger.info("remove server with name {} ", m.getAddrName());
            })
            .match(GetRandomAddressMessage.class, m -> {
                Object[] servers = hostsStorage.values().toArray();
                getSender().tell(servers[new Random().nextInt(servers.length)], getSelf());
            })
            .matchAny(o -> { logger.warning("got unknown message: {}", o.getClass().toString()); })
            .build();
    }
}
