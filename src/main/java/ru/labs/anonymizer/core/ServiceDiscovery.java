package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import ru.labs.anonymizer.messages.AddAddressMessage;
import ru.labs.anonymizer.messages.RemoveAddressMessage;

import java.io.IOException;
import java.util.List;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms
    private static final String REGISTRY_ROOT = "/servers";
    private static final String REGISTRY_NODE_PATH = REGISTRY_ROOT+"/s";

    private ZooKeeper zoo;
    private ActorRef hostStorageActor;

    public ServiceDiscovery(String zkAddr, ActorRef hostStorageActor)
        throws IOException, KeeperException, InterruptedException {
        this.hostStorageActor = hostStorageActor;
        this.zoo = new ZooKeeper(zkAddr, SESSION_TIMEOUT, this::watchEvents);

        this.loadServersList();
    }

    public void register(String addr) throws KeeperException, InterruptedException {
        zoo.create(
            REGISTRY_NODE_PATH,
            addr.getBytes(),
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    private void watchEvents(WatchedEvent watchedEvent) {
        String eventPath = watchedEvent.getPath();
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated) {
            try {
                byte[] addr = zoo.getData(eventPath, false, null);
                hostStorageActor.tell(
                    new AddAddressMessage(eventPath, new String(addr)),
                    ActorRef.noSender()
                );
            } catch (KeeperException | InterruptedException e) {
                // TODO: log message
                e.printStackTrace();
            }
        } else if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
            hostStorageActor.tell(
                new RemoveAddressMessage(eventPath),
                ActorRef.noSender()
            );
        }
    }

    private void loadServersList() throws KeeperException, InterruptedException {
        List<String> servers = zoo.getChildren(REGISTRY_ROOT, false);
        servers.forEach((server) -> {
                try {
                    String serverPath = REGISTRY_ROOT+"/"+server;
                    byte[] addr = zoo.getData(serverPath, false, null);
                    hostStorageActor.tell(
                        new AddAddressMessage(serverPath, new String(addr)),
                        ActorRef.noSender()
                    );
                } catch (KeeperException | InterruptedException e) {
                    // TODO: log message
                    e.printStackTrace();
                }
            }
        );
    }
}
