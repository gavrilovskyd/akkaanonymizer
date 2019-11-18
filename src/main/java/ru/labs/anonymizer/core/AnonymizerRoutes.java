package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AnonymizerRoutes extends AllDirectives {
    private static final Duration TIMEOUT = Duration.ofMillis(5000); // ms
    private static final String HTTP_METHOD_NAME = "go";
    private static final String URL_PARAM_NAME = "url";
    private static final String COUNT_NAME = "count";

    private ActorRef addrStoreActor;
    private ActorSystem system;

    public AnonymizerRoutes(ActorSystem system, ActorRef addrStoreActor) {
        this.system = system;
        this.addrStoreActor = addrStoreActor;
    }

    public Route routes() {
        return route(
            path(HTTP_METHOD_NAME, () ->
                route(
                    get(() ->
                        parameter("url", urlQuery ->
                            parameter("count", countQuery -> {
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
        return Patterns.ask(addrStoreActor, new GetRandomAddressMessage(), TIMEOUT)
            .thenCompose(addrParam -> {
                String addr = ((String) addrParam);
                Uri redirectUri = Uri.create(addr)
                    .addPathSegment(HTTP_METHOD_NAME)
                    .query(Query.create(Pair.create("url", url)))
                    .query(Query.create(Pair.create("count", Integer.toString(count - 1))));
                return fetch(redirectUri.toString());
            });
    }
}
