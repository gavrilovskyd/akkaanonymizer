package ru.labs.anonymizer.actors;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;
import ru.labs.anonymizer.messages.SetAddressesMessage;

import java.util.Random;

public class AddressStoreActor extends AbstractActor {
    private String[] addresses;
    private Logger logger = LoggerFactory.getLogger(AddressStoreActor.class);

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
            .match(SetAddressesMessage.class, m -> {
                addresses = m.getAddresses();
                logger.info("received new addresses list");
            })
            .match(GetRandomAddressMessage.class, m -> {
                getSender().tell(addresses[new Random().nextInt(addresses.length)], getSelf());
            })
            .matchAny(o -> { logger.warning("got unknown message: {}", o.getClass().toString()); })
            .build();
    }
}
