package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import ru.labs.anonymizer.messages.GetRandomHostMessage;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AnonymizerRoutes extends AllDirectives {
    private static final Duration TIMEOUT = Duration.ofMillis(5000);

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
                                    return completeWithFuture(fetch(urlQuery));
                                }

                                return completeWithFuture(redirect(urlQuery, count));
                            })
                        ))
                ))
        );
    }

    private CompletionStage<HttpResponse> fetch(String url) {
        return Http.get(system).singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> redirect(String url, int count) {
        Patterns.ask(hostStoreActor, new GetRandomHostMessage(), )
    }
}
