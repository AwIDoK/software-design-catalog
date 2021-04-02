package com.awidok.catalog.netty_http_server;

import com.awidok.catalog.Currencies;
import com.awidok.catalog.reactive_mongo_driver.Item;
import com.awidok.catalog.reactive_mongo_driver.ReactiveMongoDriver;
import com.awidok.catalog.reactive_mongo_driver.User;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.bson.types.ObjectId;
import rx.Observable;

import java.util.Arrays;

public class Handlers {
    public static Observable<Void> handle(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp) {
        if (req.getDecodedPath().equals("/register") && req.getHttpMethod() == HttpMethod.POST) {
            return handleRegister(req, resp);
        } else if (req.getDecodedPath().equals("/add") && req.getHttpMethod() == HttpMethod.POST) {
            return handleAdd(req, resp);
        } else if (req.getDecodedPath().equals("/items") && req.getHttpMethod() == HttpMethod.GET) {
            return handleItems(req, resp);
        }
        return resp.setStatus(HttpResponseStatus.NOT_FOUND);
    }

    private static Observable<Void> handleRegister(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp) {
        if (!req.getQueryParameters().containsKey("name") || !req.getQueryParameters().containsKey("currency")) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }
        String name = req.getQueryParameters().get("name").get(0);
        String currency = req.getQueryParameters().get("currency").get(0);
        if (Arrays.stream(Currencies.Names.values()).map(Currencies.Names::name).noneMatch(currency::equals)) {
            return resp
                    .setStatus(HttpResponseStatus.BAD_REQUEST)
                    .writeString(Observable.just("Unknown currency"));
        }
        User user = new User(name, currency);
        return resp.writeString(ReactiveMongoDriver.saveUser(user).map(success -> user.getId().toString()));
    }

    private static Observable<Void> handleAdd(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp) {
        if (!req.getQueryParameters().containsKey("name") || !req.getQueryParameters().containsKey("price")) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }
        String name = req.getQueryParameters().get("name").get(0);
        long price;
        try {
            price = Long.parseLong(req.getQueryParameters().get("price").get(0));
        } catch (NumberFormatException unused) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }
        if (price < 0) {
            return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
        }
        Item item = new Item(name, price);
        return resp.writeString(ReactiveMongoDriver.saveItem(item).map(success -> ""));
    }

    private static Observable<Void> handleItems(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp) {
        ObjectId id = null;
        try {
            if (req.getQueryParameters().containsKey("id")) {
                id = new ObjectId(req.getQueryParameters().get("id").get(0));
            }
        } catch (IllegalArgumentException ignored) {
        }
        return resp.writeString(
                ReactiveMongoDriver.getUserById(id).defaultIfEmpty(null).flatMap(user ->
                        ReactiveMongoDriver.getAllItems().map(item ->
                                item.toStringWithConvertedPrice(user == null ? Currencies.DEFAULT : Currencies.Names.valueOf(user.currency))
                        )
                )
        );

    }
}
