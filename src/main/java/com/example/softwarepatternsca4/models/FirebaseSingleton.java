package com.example.softwarepatternsca4.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSingleton {

    private static final String DATABASE_URL = "https://your-custom-database-url.firebaseio.com/";
    private static FirebaseDatabase customFirebaseDatabaseInstance;

    private FirebaseSingleton() {
        // Private constructor to prevent instantiation
    }

    public static DatabaseReference getCustomDatabaseReference() {
        return getInstance().getReference();
    }

    public static FirebaseDatabase getInstance() {
        if (customFirebaseDatabaseInstance == null) {
            synchronized (FirebaseSingleton.class) {
                if (customFirebaseDatabaseInstance == null) {
                    customFirebaseDatabaseInstance = FirebaseDatabase.getInstance(DATABASE_URL);
                }
            }
        }
        return customFirebaseDatabaseInstance;
    }

    // This method should return the default DatabaseReference instance
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
