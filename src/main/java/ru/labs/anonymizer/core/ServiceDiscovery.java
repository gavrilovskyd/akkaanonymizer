package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import ru.labs.anonymizer.messages.ChangeServerListMessage;

import java.io.IOException;
import java.util.List;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms
    private static final String REGISTRY_ROOT = "/servers";
    private static final String REGISTRY_NODE_PATH = REGISTRY_ROOT+"/s";


    private ZooKeeper zoo;

    public ServiceDiscovery(String zkHost, ActorRef serversStorageActor)
        throws IOException, KeeperException, InterruptedException {
        this.zoo = new ZooKeeper(zkHost, SESSION_TIMEOUT, watchedEvent -> {

        });

       List<String> servers = zoo.getChildren(REGISTRY_ROOT, false);
       servers.forEach((addr) ->
           serversStorageActor.tell(
               new ChangeServerListMessage(addr, ChangeServerListMessage.EventType.ADD),
               ActorRef.noSender()
           )
       );
    }

    public void register(String host) throws KeeperException, InterruptedException {
        zoo.create(
            REGISTRY_NODE_PATH,
            host.getBytes(),
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }
}
