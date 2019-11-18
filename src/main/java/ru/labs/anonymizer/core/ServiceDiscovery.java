package ru.labs.anonymizer.core;

import org.apache.zookeeper.ZooKeeper;

public class ServiceDiscovery {
    private ZooKeeper zoo;

    public ServiceDiscovery(String zkHost) {
        this.zoo = new ZooKeeper(zkHost, )
    }
}
