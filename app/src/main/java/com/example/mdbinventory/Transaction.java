package com.example.mdbinventory;

import java.util.Date;

public class Transaction {

    float cost;
    String description;
    String suppliers;
    Date date;
    private String image; // a link to the image

    public Transaction(float cost, String description, String suppliers, String image) {
        this.cost = cost;
        this.description = description;
        this.suppliers = suppliers;
        this.image = image;
        date = new Date(System.currentTimeMillis());
    }
}