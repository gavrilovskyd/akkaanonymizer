package ru.labs.anonymizer.core;

import org.apache.zookeeper.ZooKeeper;

public class ServiceDiscovery {
    private static final int SESSION_TIMEOUT = 3000; // ms

    private ZooKeeper zoo;

    public ServiceDiscovery(String zkHost) {
        this.zoo = new ZooKeeper(zkHost, )
    }
}
