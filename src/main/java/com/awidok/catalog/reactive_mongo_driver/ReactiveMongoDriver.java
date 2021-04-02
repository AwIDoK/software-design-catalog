package com.awidok.catalog.reactive_mongo_driver;

import com.mongodb.rx.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import rx.Observable;

public class ReactiveMongoDriver {

    private static final MongoClient client = createMongoClient();
    private static final MongoDatabase database = client.getDatabase("catalog");
    private static final MongoCollection<Document> userCollection = database.getCollection("users");
    private static final MongoCollection<Document> itemCollection = database.getCollection("items");

    public static Observable<Item> getAllItems() {
        return itemCollection.find().toObservable().map(Item::new);
    }

    public static Observable<User> getUserById(ObjectId id) {
        return userCollection.find(new Document("_id", id)).toObservable().map(User::new);
    }

    public static Observable<Success> saveUser(User user) {
        return userCollection.insertOne(user.asDocument());
    }

    public static Observable<Success> saveItem(Item item) {
        return itemCollection.insertOne(item.asDocument());
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}

