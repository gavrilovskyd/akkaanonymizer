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

    public ServiceDiscovery(String zkAddr, ActorRef addressStorageActor) throws IOException {
        this.zkAddr = zkAddr;
        this.addressStorageActor = addressStorageActor;

        reconnect();
    }

    public void register(String addr) throws KeeperException, InterruptedException {
        zoo.create(
            REGISTRY_NODE_PATH,
            addr.getBytes(),
            ZooDefs.Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL_SEQUENTIAL
        );
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

    private void watchNodes() {
        try {
            List<String> serverNodeNames = zoo.getChildren(REGISTRY_ROOT, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNodes();
                }
            });

            List<String> addresses = new ArrayList<>();
            for (String nodeName : serverNodeNames) {
                byte[] addr = zoo.getData(REGISTRY_ROOT + "/" + nodeName, false, null);
                addresses.add(new String(addr));
            }

            addressStorageActor.tell(new SetAddressesMessage((String[]) addresses.toArray()), ActorRef.noSender());
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
