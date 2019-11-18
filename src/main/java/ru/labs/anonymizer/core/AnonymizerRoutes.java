package ru.labs.anonymizer.core;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;

public class AnonymizerRoutes extends AllDirectives {
    public AnonymizerRoutes() {}

    public Route route() {
        return route(
            path("go", () ->
                route(
                    get(() ->
                        parameter("url", (urlQuery) ->
                            parameter("count", (countQuery)-> {
                                int count = Integer.parseInt(countQuery);
                                if (count == 0) {
                                    
                                }
                            })
                        ))
                ))
        );
    }
}
