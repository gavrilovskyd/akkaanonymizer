package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.KeeperException;
import ru.labs.anonymizer.actors.HostStoreActor;

import java.io.IOException;

public class AnonymizerServer {
    private AnonymizerRoutes routes;
    private ServiceDiscovery serviceDiscovery;

    public AnonymizerServer(ActorSystem system, String zkHost, String host, int port)
        throws InterruptedException, IOException, KeeperException {
        ActorRef hostStoreActor = system.actorOf(Props.create(HostStoreActor.class), "host-store");
        this.serviceDiscovery = new ServiceDiscovery(zkHost, hostStoreActor);
        this.routes = new AnonymizerRoutes(system, hostStoreActor);
    }

    public Route routes() {
        return routes.routes();
    }
}
