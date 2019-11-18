package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.labs.anonymizer.actors.AddressStoreActor;
import ru.labs.anonymizer.messages.SetAddressesMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms
    private static final String REGISTRY_ROOT = "/servers";
    private static final String REGISTRY_NODE_PATH = REGISTRY_ROOT + "/s";
    private static final Logger logger = LoggerFactory.getLogger(AddressStoreActor.class);

    private String zkAddr;
    private ZooKeeper zoo;
    private ActorRef addressStorageActor;

    public ServiceDiscovery(String zkAddr, ActorRef addressStorageActor) throws IOException {
        this.zkAddr = zkAddr;
        this.addressStorageActor = addressStorageActor;
        this.zoo = connect();
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

    private ZooKeeper connect() throws IOException {
        return new ZooKeeper(zkAddr, SESSION_TIMEOUT, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired
                || watchedEvent.getState() == Watcher.Event.KeeperState.Disconnected) {
                try {
                    logger.info("starting reconnect");
                    reconnect();
                } catch (IOException e) {
                    logger.error("got state watch error:", e);
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

            logger.info("servers list updated");
            List<String> addresses = new ArrayList<>();
            for (String nodeName : serverNodeNames) {
                byte[] addr = zoo.getData(REGISTRY_ROOT + "/" + nodeName, false, null);
                addresses.add(new String(addr));
            }

            logger.debug("got new nodes: {}", addresses);
            addressStorageActor.tell(new SetAddressesMessage((String[]) addresses.toArray()), ActorRef.noSender());
        } catch (KeeperException | InterruptedException e) {
            logger.error("got getChildren error:", e);
        }
    }
}
