package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.KeeperException;
import ru.labs.anonymizer.actors.AddressStoreActor;

import java.io.IOException;

public class AnonymizerServer {
    private final static String SCHEME = "http";

    private AnonymizerRoutes routes;

    public AnonymizerServer(ActorSystem system, String zkHost, String host, int port)
        throws InterruptedException, IOException, KeeperException {

        ActorRef hostStoreActor = system.actorOf(Props.create(AddressStoreActor.class), "host-store");
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(zkHost, hostStoreActor);
        serviceDiscovery.register(SCHEME + "://" +host+":"+port);

        this.routes = new AnonymizerRoutes(system, hostStoreActor);
    }

    public Route routes() {
        return routes.routes();
    }
}
