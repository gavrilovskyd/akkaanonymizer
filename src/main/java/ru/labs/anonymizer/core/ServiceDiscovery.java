package ru.labs.anonymizer.core;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms
    private static final String REGISTRY_NODE_PATH = "/"

    private ZooKeeper zoo;

    public ServiceDiscovery(String zkHost) throws IOException {
        this.zoo = new ZooKeeper(zkHost, SESSION_TIMEOUT, watchedEvent -> {});
    }

    public void register(String host) {

    }
}
