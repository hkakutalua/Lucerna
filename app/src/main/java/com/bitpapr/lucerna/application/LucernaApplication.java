package com.bitpapr.lucerna.application;

import android.app.Application;
import android.net.Uri;

import com.google.firebase.FirebaseApp;
import com.squareup.picasso.Picasso;

/**
 * Created by henrick on 12/11/17.
 */

public class LucernaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        // Configure picasso to print error
        Picasso.Builder picassoBuilder = new Picasso.Builder(this);
        picassoBuilder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
