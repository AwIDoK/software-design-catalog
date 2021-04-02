package com.awidok.catalog.reactive_mongo_driver;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;


public class User {
    public final ObjectId id;
    public final String name;
    public final String currency;


    public User(Document doc) {
        this(doc.getObjectId("_id"), doc.getString("name"), doc.getString("currency"));
    }

    public User(ObjectId id, String name, String currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public ObjectId getId() {
        return id;
    }

    public User(String name, String currency) {
        this(new BsonObjectId().getValue(), name, currency);
    }

    public Document asDocument() {
        return new Document()
                .append("_id", id)
                .append("name", name)
                .append("currency", currency);
    }
}
