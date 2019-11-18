package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class AnonymizerRoutes extends AllDirectives {
    private ActorRef hostStoreActor;
    private ActorSystem system;

    public AnonymizerRoutes(ActorSystem system, ActorRef hostStoreActor) {
        this.system = system;
        this.hostStoreActor = hostStoreActor;
    }

    public Route route() {
        return route(
            path("go", () ->
                route(
                    get(() ->
                        parameter("url", (urlQuery) ->
                            parameter("count", (countQuery)-> {
                                int count = Integer.parseInt(countQuery);
                                if (count == 0) {
                                    Http.get()
                                }
                            })
                        ))
                ))
        );
    }
}
