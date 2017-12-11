package com.bitpapr.lucerna.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by henrick on 12/11/17.
 */

public class LucernaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
