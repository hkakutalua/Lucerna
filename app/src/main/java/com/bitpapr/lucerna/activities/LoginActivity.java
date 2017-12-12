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
import com.google.firebase.FirebaseNetworkException;
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
    private TextView mLoginErrorTextView;

    private ProgressDialog mLoginProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.edit_email);
        mPasswordEditText = findViewById(R.id.edit_password);
        mLoginErrorTextView = findViewById(R.id.text_login_error);

        Button loginButton = findViewById(R.id.button_login);
        TextView registrationLinkTextView = findViewById(R.id.text_registration_link);

        loginButton.setOnClickListener((View v) -> loginUser());

        registrationLinkTextView.setOnClickListener((View v) -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });

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

        Log.e(TAG, e.getMessage());

        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            showLoginFailedMessage();
        } else if (e instanceof FirebaseAuthInvalidUserException) {
            final String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

            if (TextUtils.equals(errorCode, "ERROR_USER_NOT_FOUND")) {
                showNotExistentAccountMessage();
            } else if (TextUtils.equals(errorCode, "ERROR_USER_DISABLED")) {
                showAccountDisabledMessage();
            }
        } else if (e instanceof FirebaseNetworkException) {
            showNetworkFailureMessage();
        } else {
            showUnknownFailureMessage();
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
        mLoginErrorTextView.setVisibility(View.VISIBLE);
        mLoginErrorTextView.setText(getString(R.string.login_failure_message));
    }

    /**
     * Shows a text indicating that a failure related to network has occurred
     */
    private void showNetworkFailureMessage() {
        mLoginErrorTextView.setVisibility(View.VISIBLE);
        mLoginErrorTextView.setText(getString(R.string.network_failure_message));
    }

    /**
     * Shows a text indicating that the specified email is not associated
     * with an account
     */
    private void showNotExistentAccountMessage() {
        mLoginErrorTextView.setVisibility(View.VISIBLE);
        mLoginErrorTextView.setText(getString(R.string.not_existent_account_message));
    }

    /**
     * Shows a text indicating that the specified email was disabled (though registered)
     */
    private void showAccountDisabledMessage() {
        mLoginErrorTextView.setVisibility(View.VISIBLE);
        mLoginErrorTextView.setText(getString(R.string.account_disabled_message));
    }

    /**
     * Shows a text indicating that a unknown error occurred during the login process
     */
    private void showUnknownFailureMessage() {
        mLoginErrorTextView.setVisibility(View.VISIBLE);
        mLoginErrorTextView.setText(getString(R.string.unknown_failure_message));
    }
}
