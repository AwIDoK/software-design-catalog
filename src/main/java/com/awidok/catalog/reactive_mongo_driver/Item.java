package com.awidok.catalog.reactive_mongo_driver;

import com.awidok.catalog.Currencies;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Item {
    public final ObjectId id;
    public final String name;
    public long price;


    public Item(Document doc) {
        this(doc.getObjectId("_id"), doc.getString("name"), doc.getLong("price"));
    }

    public Item(ObjectId id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Item(String name, long price) {
        this(new BsonObjectId().getValue(), name, price);
    }

    public String toStringWithConvertedPrice(Currencies.Names currency) {
        return id + " " + name + " " + Currencies.convert(price, currency) + "\n";
    }

    public Document asDocument() {
        return new Document()
                .append("_id", id)
                .append("name", name)
                .append("price", price);
    }
}
