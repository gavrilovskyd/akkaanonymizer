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

    private String zkAddr;
    private ZooKeeper zoo;
    private ActorRef addressStorageActor;

    public ServiceDiscovery(String zkAddr, ActorRef addressStorageActor)
        throws IOException, KeeperException, InterruptedException {
        this.zkAddr = zkAddr;
        this.addressStorageActor = addressStorageActor;
        this.zoo = new ZooKeeper(zkAddr, SESSION_TIMEOUT, this::watchEvents);

        this.loadServersList();
    }

    private ZooKeeper connect() throws IOException {
        return new ZooKeeper(zkAddr, SESSION_TIMEOUT, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired
                || watchedEvent.getState() == Watcher.Event.KeeperState.Disconnected) {
                try {
                    reconnect();
                } catch (IOException e) {
                    // TODO: log error
                    e.printStackTrace();
                }
            }
        });
    }

    private void reconnect() throws IOException {
        zoo = connect();
    }

    public void register(String addr) throws KeeperException, InterruptedException {
        zoo.create(
            REGISTRY_NODE_PATH,
            addr.getBytes(),
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL
        );
    }

    private void watchNodes() {
        zoo.getChildren(REGISTRY_ROOT, watchedEvent -> {
            if (watchedEvent.getType() == )
        })


        String eventPath = watchedEvent.getPath();
        System.out.println(watchedEvent.toString());
        if (watchedEvent.getType() == Watcher.Event.EventType.NodeCreated) {
            try {
                byte[] addr = zoo.getData(eventPath, false, null);
                addressStorageActor.tell(
                    new AddAddressMessage(eventPath, new String(addr)),
                    ActorRef.noSender()
                );
            } catch (KeeperException | InterruptedException e) {
                // TODO: log message
                e.printStackTrace();
            }
        } else if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted) {
            addressStorageActor.tell(
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
                    addressStorageActor.tell(
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
