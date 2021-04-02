package com.awidok.catalog.netty_http_server;

import io.reactivex.netty.protocol.http.server.HttpServer;

public class RxNettyHttpServer {

    public static void main(final String[] args) {
        HttpServer
            .newServer(8080)
            .start(Handlers::handle)
            .awaitShutdown();
    }
}
