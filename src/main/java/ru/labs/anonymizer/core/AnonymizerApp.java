package ru.labs.anonymizer.core;

import akka.actor.ActorSystem;

public class AnonymizerApp {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("anonymizer-system");
    }
}
