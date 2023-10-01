package com.lazypanda.activities;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistent extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
