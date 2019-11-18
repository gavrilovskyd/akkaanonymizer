package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;
import ru.labs.anonymizer.messages.SetAddressListMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AddressStoreActor extends AbstractActor {
    private String[] addressStorage;
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(SetAddressListMessage.class, m -> {
                hostsStorage = m.getAddrList();
                logger.info("received new hosts list");
            })
            .match(GetRandomAddressMessage.class, m -> {
                getSender().tell(addressStorage[new Random().nextInt(addressStorage.length)], getSelf());
            })
            .matchAny(o -> { logger.warning("got unknown message: {}", o.getClass().toString()); })
            .build();
    }
}
