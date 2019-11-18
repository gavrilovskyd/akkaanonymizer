package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import ru.labs.anonymizer.messages.AddHostMessage;

import java.io.IOException;
import java.util.List;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms
    private static final String REGISTRY_ROOT = "/servers";
    private static final String REGISTRY_NODE_PATH = REGISTRY_ROOT+"/s";

    private ZooKeeper zoo;
    private ActorRef hostStorageActor;

    public ServiceDiscovery(String zkHost, ActorRef serversStorageActor)
        throws IOException, KeeperException, InterruptedException {
        this.hostStorageActor = serversStorageActor;
        this.zoo = new ZooKeeper(zkHost, SESSION_TIMEOUT, this::watchEvents);

        this.loadServersList(serversStorageActor);
    }

    private void watchEvents(WatchedEvent watchedEvent) {
        String eventPath = watchedEvent.getPath();
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated) {
            try {
                byte[] addr = zoo.getData(eventPath, false, null);
                hostStorageActor.tell(
                    new AddHostMessage(eventPath, new String(addr),
                        AddHostMessage.EventType.ADD),
                    ActorRef.noSender()
                );
            } catch (KeeperException | InterruptedException e) {
                // TODO: log message
                e.printStackTrace();
            }
        } else if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
            hostStorageActor.tell(
                new AddHostMessage(eventPath, "",
                    AddHostMessage.EventType.REMOVE),
                ActorRef.noSender()
            );
        }
    }

    private void loadServersList(ActorRef serversStorageActor) throws KeeperException, InterruptedException {
        List<String> servers = zoo.getChildren(REGISTRY_ROOT, false);
        servers.forEach((server) -> {
                try {
                    String serverPath = REGISTRY_ROOT+"/"+server;
                    byte[] addr = zoo.getData(serverPath, false, null);
                    serversStorageActor.tell(
                        new AddHostMessage(serverPath, new String(addr),
                            AddHostMessage.EventType.ADD),
                        ActorRef.noSender()
                    );
                } catch (KeeperException | InterruptedException e) {
                    // TODO: log message
                    e.printStackTrace();
                }
            }
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
