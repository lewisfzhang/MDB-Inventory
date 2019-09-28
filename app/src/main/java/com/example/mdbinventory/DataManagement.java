package com.example.mdbinventory;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class DataManagement {
    public static void writeNewTransaction(Transaction transaction, DatabaseReference database) {
        String key = database.child("transactions").push().getKey();
        Map<String, Object> transactionValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transactions/" + key, transactionValues);
        database.updateChildren(childUpdates);
    }
}
