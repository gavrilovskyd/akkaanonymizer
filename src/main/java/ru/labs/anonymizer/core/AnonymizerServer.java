package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import ru.labs.anonymizer.actors.HostStoreActor;

public class AnonymizerServer {
    private AnonymizerRoutes routes;

    public AnonymizerServer(ActorSystem system) {
        ActorRef hostStoreActor = system.actorOf(Props.create(HostStoreActor.class), "host-store");
    }
}
