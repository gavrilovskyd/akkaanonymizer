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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

public class AnonymizerApp {
    private static final Logger logger = LoggerFactory.getLogger(AnonymizerApp.class);

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: AnonymizerApp <zkAddr> <host> <port>");
            System.exit(-1);
        }
        String zkAddr = args[0];
        String host = args[1];
        int port = Integer.parseInt(args[2]);

        ActorSystem system = ActorSystem.create("anonymizer-system");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AnonymizerServer server = new AnonymizerServer(system, zkAddr, host, port);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.routes().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
            routeFlow, ConnectHttp.toHost(host, port), materializer);

        logger.info("Server started at http://{}:{}", host, port);
        System.in.read();
        binding.thenCompose(ServerBinding::unbind)
            .thenAccept(unbound -> system.terminate());
    }
}
