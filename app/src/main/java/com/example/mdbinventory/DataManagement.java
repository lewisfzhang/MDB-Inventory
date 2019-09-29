package com.example.mdbinventory;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManagement {
    public static void writeNewTransaction(Transaction transaction, DatabaseReference database) {
        String key = database.child("transactions").push().getKey();
        Map<String, Object> transactionValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transactions/" + key, transactionValues);
        database.updateChildren(childUpdates);
    }

    public static List<Transaction> readSnapshot(DataSnapshot snapshot) {
        Map<String, Object> dataMap = (Map<String, Object>) snapshot.getValue();

        if (dataMap == null) {
            return new ArrayList<>();
        }

        List<Transaction> newData = new ArrayList<>();

        for (String key : dataMap.keySet()) {
            newData.add(Transaction.toTransaction((Map) dataMap.get(key)));
        }

        Collections.sort(newData);

        return newData;
    }
}
