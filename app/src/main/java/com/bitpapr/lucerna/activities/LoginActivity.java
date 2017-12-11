package com.bitpapr.lucerna.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bitpapr.lucerna.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * The activity responsible for login in the user
 */
public class LoginActivity extends AppCompatActivity
    implements OnCompleteListener<AuthResult>, OnFailureListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mLoginFailureTextView;
    private TextView mNetworkFailureTextView;

    private ProgressDialog mLoginProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.edit_email);
        mPasswordEditText = findViewById(R.id.edit_password);
        mLoginFailureTextView = findViewById(R.id.text_login_failure);
        mNetworkFailureTextView = findViewById(R.id.text_network_failure);
        Button loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener((View v) -> loginUser());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mLoginProgressDialog = new ProgressDialog(this);
    }

    /**
     * Called when the authentication process is completed (success or failure)
     * @param task a Task object containing the result of the authentication process
     */
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Log.d(TAG, "Sign in with email was successful");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        mLoginProgressDialog.dismiss();
    }

    /**
     * Called when the authentication process fails
     * @param e the exception whose concrete type represents the kind of failure
     */
    @Override
    public void onFailure(@NonNull Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException ||
                e instanceof FirebaseAuthInvalidCredentialsException) {
            Log.e(TAG, e.getMessage());
            showLoginFailedMessage();
        } else {
            Log.e(TAG, e.getMessage());
            showLoginFailedMessage();
        }

        mLoginProgressDialog.dismiss();
    }

    /**
     * Tries to log in the user
     */
    private void loginUser() {

        if (!validLoginFields())
            return;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this)
                .addOnFailureListener(this);

        mLoginProgressDialog.setIndeterminate(true);
        mLoginProgressDialog.setCancelable(false);
        mLoginProgressDialog.setMessage(getString(R.string.login_wait_text));
        mLoginProgressDialog.show();
    }

    /**
     * Check if email and password fields contain valid data
     * @return a boolean indicating if login and password fields are
     * in valid state
     */
    private boolean validLoginFields() {

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        boolean validFields = true;

        if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validFields = false;
            mEmailEditText.setError(getString(R.string.invalid_email_error));
        }

        if (TextUtils.isEmpty(password)) {
            validFields = false;
            mPasswordEditText.setError(getString((R.string.empty_password_error)));
        }

        return validFields;
    }

    /**
     * Shows a text indicating that the login was unsuccessful
     */
    private void showLoginFailedMessage() {
        mNetworkFailureTextView.setVisibility(View.INVISIBLE);
        mLoginFailureTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows a text indicating that a failure related to network has occurred
     */
    private void showNetworkFailureMessage() {
        mLoginFailureTextView.setVisibility(View.INVISIBLE);
        mNetworkFailureTextView.setVisibility(View.VISIBLE);
    }
}
