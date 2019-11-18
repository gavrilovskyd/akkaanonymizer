package ru.labs.anonymizer.core;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;

public class AnonymizerApp {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: AnonymizerApp <host> <port>");
            System.exit(-1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        ActorSystem system = ActorSystem.create("anonymizer-system");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AnonymizerServer server = new AnonymizerServer(system);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.routes().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
            routeFlow, ConnectHttp.toHost("localhost", 8080), materializer);

        System.out.println("Server started at http://localhost:8080/");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind)
            .thenAccept(unbound -> system.terminate());
    }
}
