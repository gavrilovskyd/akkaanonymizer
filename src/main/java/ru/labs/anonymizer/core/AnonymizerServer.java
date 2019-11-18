package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.server.Route;
import ru.labs.anonymizer.actors.HostStoreActor;

public class AnonymizerServer {
    private AnonymizerRoutes routes;

    public AnonymizerServer(ActorSystem system, String zkHost, String host, int port) {
        ActorRef hostStoreActor = system.actorOf(Props.create(HostStoreActor.class), "host-store");
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(zkHost, hostStoreActor);
        this.routes = new AnonymizerRoutes(system, hostStoreActor);
    }

    public Route routes() {
        return routes.routes();
    }
}
