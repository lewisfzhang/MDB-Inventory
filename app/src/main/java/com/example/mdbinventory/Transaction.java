package com.example.mdbinventory;

import java.util.HashMap;
import java.util.Map;

public class Transaction implements Comparable<Transaction> {

    String cost;
    String description;
    String suppliers;
    String date;
    String url; // a link to the image
    String key;

    public static final String KEY = "key", COST = "cost", DESCRIPTION = "description", SUPPLIERS = "suppliers", DATE = "date", URL = "url";

    public Transaction() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Transaction(String key, String cost, String description, String suppliers, String date, String url) {
        this.key = key;
        this.cost = cost;
        this.description = description;
        this.suppliers = suppliers;
        this.date = date;
        this.url = url;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(KEY, key);
        result.put(COST, cost + "");
        result.put(DESCRIPTION, description);
        result.put(SUPPLIERS, suppliers);
        result.put(DATE, date);
        result.put(URL, url);
        return result;
    }

    public static Transaction toTransaction(Map<String, Object> map) {
        return new Transaction(
                (String) map.get(KEY),
                (String) map.get(COST),
                (String) map.get(DESCRIPTION),
                (String) map.get(SUPPLIERS),
                (String) map.get(DATE),
                (String) map.get(URL)
        );
    }

    @Override
    public int compareTo(Transaction t) {
        return dateToInt(this.date) - dateToInt(t.date);
    }

    public int dateToInt(String date) {
        // assuming date format is MM-DD-YYYY
        int month = Integer.parseInt(date.substring(0, 2));
        int day = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(date.length() - 4));
        return (int) (Math.pow(10, 4) * year + Math.pow(10, 2) * month + day);
    }
}