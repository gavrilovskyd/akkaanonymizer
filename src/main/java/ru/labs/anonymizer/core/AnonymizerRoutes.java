package ru.labs.anonymizer.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.ExceptionHandler;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.pattern.Patterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.labs.anonymizer.messages.GetRandomAddressMessage;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class AnonymizerRoutes extends AllDirectives {
    private static final Duration TIMEOUT = Duration.ofMillis(5000); // ms
    private static final String HTTP_METHOD_NAME = "go";
    private static final String URL_PARAM_NAME = "url";
    private static final String COUNT_NAME = "count";
    private static final Logger logger = LoggerFactory.getLogger(AnonymizerRoutes.class);

    private ActorRef addrStoreActor;
    private ActorSystem system;

    public AnonymizerRoutes(ActorSystem system, ActorRef addrStoreActor) {
        this.system = system;
        this.addrStoreActor = addrStoreActor;
    }

    public Route routes() {
        final ExceptionHandler wrongParameterHandler = ExceptionHandler.newBuilder()
            .match(NumberFormatException.class, e ->
                complete(StatusCodes.BAD_REQUEST, "count must be integer"))
            .match(UnknownHostException.class, e ->
                complete(StatusCodes.BAD_REQUEST, "can not connect to provided url"))
            .build();

        return route(
            path(HTTP_METHOD_NAME, () ->
                route(
                    get(() ->
                        parameter(URL_PARAM_NAME, urlQuery ->
                            parameter(COUNT_NAME, countQuery ->
                                handleExceptions(wrongParameterHandler, () -> {
                                        logger.info("got request to {} with count {}", urlQuery, countQuery);

                                        int count = Integer.parseInt(countQuery);
                                        if (count == 0) {
                                            return completeWithFuture(fetch(urlQuery));
                                        }

                                        return completeWithFuture(redirect(urlQuery, count));
                                    }
                                )
                            )
                        )
                    )
                )
            )
        );
    }

    private CompletionStage<HttpResponse> fetch(String url) {
        logger.info("fetching {}", url);
        return Http.get(system).singleRequest(HttpRequest.create(url));
    }

    private CompletionStage<HttpResponse> redirect(String url, int count) {
        return Patterns.ask(addrStoreActor, new GetRandomAddressMessage(), TIMEOUT)
            .thenCompose(addrParam -> {
                String addr = ((String) addrParam);
                Uri redirectUri = Uri.create(addr)
                    .addPathSegment(HTTP_METHOD_NAME)
                    .query(Query.create(
                        Pair.create(URL_PARAM_NAME, url),
                        Pair.create(COUNT_NAME, Integer.toString(count - 1))
                    ));
                return fetch(redirectUri.toString())
                    .whenCompleteAsync();
            });
    }
}
