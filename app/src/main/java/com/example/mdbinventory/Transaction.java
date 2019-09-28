package com.example.mdbinventory;

import java.util.HashMap;
import java.util.Map;

public class Transaction {

    float cost;
    String description;
    String suppliers;
    String date;
    String url; // a link to the image

    public static final String COST = "cost", DESCRIPTION = "description", SUPPLIERS = "suppliers", DATE = "date", URL = "url";

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Transaction(float cost, String description, String suppliers, String url, String date) {
        this.cost = cost;
        this.description = description;
        this.suppliers = suppliers;
        this.url = url;
        this.date = date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(COST, cost);
        result.put(DESCRIPTION, description);
        result.put(SUPPLIERS, suppliers);
        result.put(DATE, date);
        result.put(URL, url);
        return result;
    }
}