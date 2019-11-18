package ru.labs.anonymizer.core;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

public class AnonymizerApp {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("anonymizer-system");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
    }
}
