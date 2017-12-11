package com.bitpapr.lucerna.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bitpapr.lucerna.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity related to the launch screen
 * This activity will open the main activity if the user has logged in already,
 * otherwise it will launch the login activity
 */
public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentLoggedUser = firebaseAuth.getCurrentUser();
        final Handler handler = new Handler();

        if (currentLoggedUser == null) {
            handler.postDelayed(this::showLoginActivity, 2000);
        } else {
            handler.postDelayed(this::showMainActivity, 2000);
        }
    }

    /**
     * Shows the login activity
     */
    private void showLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     * Shows the main activity
     */
    private void showMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
