package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import ru.labs.anonymizer.messages.SetAddressesMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms
    private static final String REGISTRY_ROOT = "/servers";
    private static final String REGISTRY_NODE_PATH = REGISTRY_ROOT + "/s";

    private String zkAddr;
    private ZooKeeper zoo;
    private ActorRef addressStorageActor;

    public ServiceDiscovery(String zkAddr, ActorRef addressStorageActor)
        throws IOException, KeeperException, InterruptedException {
        this.zkAddr = zkAddr;
        this.addressStorageActor = addressStorageActor;
        this.zoo = connect();
        watchNodes();
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
        watchNodes();
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
        try {
            List<String> servers = zoo.getChildren(REGISTRY_ROOT, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNodes();
                }
            });

            List<String> addresses = new ArrayList<>();
            servers.forEach((server) -> {
                byte[] addr = zoo.getData(REGISTRY_ROOT + "/" + server, false, null);
                }
            );
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
