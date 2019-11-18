package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;
import ru.labs.anonymizer.messages.SetAddressesMessage;

import java.util.Random;

public class AddressStoreActor extends AbstractActor {
    private String[] addresses;
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(SetAddressesMessage.class, m -> {
                addressStorage = m.getAddrList();
                logger.info("received new hosts list");
            })
            .match(GetRandomAddressMessage.class, m -> {
                getSender().tell(addressStorage[new Random().nextInt(addressStorage.length)], getSelf());
            })
            .matchAny(o -> { logger.warning("got unknown message: {}", o.getClass().toString()); })
            .build();
    }
}
