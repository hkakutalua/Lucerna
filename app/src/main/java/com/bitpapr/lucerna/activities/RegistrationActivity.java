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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Activity responsible for users registration
 */
public class RegistrationActivity extends AppCompatActivity
    implements OnCompleteListener<AuthResult>, OnFailureListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmationEditText;
    private TextView mRegistrationErrorTextView;

    private ProgressDialog mRegistrationProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmailEditText = findViewById(R.id.edit_email);
        mPasswordEditText = findViewById(R.id.edit_password);
        mPasswordConfirmationEditText = findViewById(R.id.edit_password_confirmation);
        mRegistrationErrorTextView = findViewById(R.id.text_registration_error);

        TextView loginLinkTextView = findViewById(R.id.text_login_link);
        Button registerButton = findViewById(R.id.button_register);

        loginLinkTextView.setOnClickListener((View v) -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        registerButton.setOnClickListener((View v) -> registerUser());

        mRegistrationProgressDialog = new ProgressDialog(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Called when the registration process is completed (success or failure)
     * @param task a Task object containing the result of the registration process
     */
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Log.i(TAG, "The registration was successful");

            startActivity(new Intent(this, ProfileConfigurationActivity.class));
            finish();
        }

        mRegistrationProgressDialog.dismiss();
    }

    /**
     * Called when the registration process has failed for some reason
     * @param e the exception whose concrete type represents the kind of failure
     */
    @Override
    public void onFailure(@NonNull Exception e) {

        Log.e(TAG, e.getClass().getSimpleName());
        Log.e(TAG, e.getMessage());

        if (e instanceof FirebaseNetworkException) {
            showNetworkFailureMessage();
        } else if (e instanceof FirebaseAuthUserCollisionException) {
            final String errorCode = ((FirebaseAuthUserCollisionException) e).getErrorCode();

            if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                showEmailAlreadyInUseMessage();
            }
        }
    }

    /**
     * Tries to register the user
     */
    private void registerUser() {

        if (!validRegistrationFields())
            return;

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this)
                .addOnFailureListener(this);

        mRegistrationProgressDialog.setMessage(getString(R.string.registration_wait_text));
        mRegistrationProgressDialog.setIndeterminate(true);
        mRegistrationProgressDialog.setCancelable(false);
        mRegistrationProgressDialog.show();
    }

    /**
     * Check if name, email, and password fields data are in valid state
     * @return a boolean indicating if the registration fields data are ok
     */
    private boolean validRegistrationFields() {

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordConfirmation = mPasswordConfirmationEditText.getText().toString();

        boolean validFields = true;

        if (TextUtils.isEmpty(email) ||
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validFields = false;
            mEmailEditText.setError(getString(R.string.invalid_email_error));
        }

        if (TextUtils.isEmpty(password)) {
            validFields = false;
            mPasswordEditText.setError(getString(R.string.empty_password_error));
        } else if (!TextUtils.equals(password, passwordConfirmation)) {
            validFields = false;
            mPasswordConfirmationEditText.setError(getString(R.string.passwords_not_matched_error));
        }

        return validFields;
    }

    /**
     * Shows a message indicating that the email specified is already in use
     */
    private void showEmailAlreadyInUseMessage() {
        mRegistrationErrorTextView.setText(
                getString(R.string.email_already_in_use_error));
        mRegistrationErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows a message indicating that a network failure has occurred
     */
    private void showNetworkFailureMessage() {
        mRegistrationErrorTextView.setText(
                getString(R.string.network_failure_message));
        mRegistrationErrorTextView.setVisibility(View.VISIBLE);
    }
}
