package com.bitpapr.lucerna.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitpapr.lucerna.R;
import com.bitpapr.lucerna.data.UserSharedPreferences;
import com.bitpapr.lucerna.utilities.BitmapUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 * Created by henrick on 12/12/17.
 */

/**
 * The activity responsible for configuring briefly user profile
 * Normally this activity is called after the login or registering process
 * to quickly set the user name and profile picture
 */
public class ProfileConfigurationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE_RETRIEVAL = 1234;
    private static final String STORAGE_REFERENCE_PROFILE_IMAGES = "profile_images";
    public static final String PROFILE_IMAGE_NAME_PREFIX = "profile";

    private ImageView mProfilePictureImageView;
    private Button mProceedButton;
    private EditText mNameEditText;
    private ProgressBar mImageUploadProgressBar;
    private TextView mPictureUploadErrorTextView;

    private FirebaseAuth mFirebaseAuth;

    private Uri mLocalImageUri;
    private Uri mLastUploadedPhotoUri = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_configuration);

        mProfilePictureImageView = findViewById(R.id.image_profile_picture);
        mNameEditText = findViewById(R.id.edit_name);
        mImageUploadProgressBar = findViewById(R.id.progress_image_upload);
        mPictureUploadErrorTextView = findViewById(R.id.text_picture_upload_error);
        mProceedButton = findViewById(R.id.button_proceed);
        LinearLayout profilePictureLayout = findViewById(R.id.layout_profile_picture);

        profilePictureLayout.setOnClickListener((View v) -> chooseProfilePicture());
        mProceedButton.setOnClickListener((View v) -> updateProfileNameAndFinish());

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        // This will download and show the user profile picture if it was previously set
        // otherwise we'll just show a default profile picture
        if (user != null) {
            mNameEditText.setText(user.getDisplayName());
            Uri photoUri = user.getPhotoUrl();

            if (photoUri == null) {
                mProfilePictureImageView.setImageResource(R.drawable.ic_default_profile_150dp);
            } else {
                downloadProfilePhoto(photoUri);
            }
        }
    }

    /**
     * Called when a picture is selected from the gallery
     * @param requestCode the intent request code
     * @param resultCode the result code
     * @param data the data returned from the selection, normally a Uri pointing
     *             to the selected picture
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE_RETRIEVAL && resultCode == RESULT_OK) {
            Uri photoLocalUri = data.getData();

            if (photoLocalUri != null) {
                mLocalImageUri = photoLocalUri;
                uploadProfilePhoto(mLocalImageUri);
            }
        }
    }

    /**
     * Requests the system to select a picture from gallery
     */
    private void chooseProfilePicture() {
        Intent imageRetrievalIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageRetrievalIntent.setType("image/*");

        if (imageRetrievalIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageRetrievalIntent, REQUEST_CODE_IMAGE_RETRIEVAL);
        } else {
            Toast.makeText(this, R.string.no_application_to_open_image, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Finishes the profile configuration by changing the name of
     * the user and then finish this activity.
     * This will also set a value in SharedPreferences that specify that user profile was
     * already configured, to skip profile configuration when the app launches in the next time
     */
    private void updateProfileNameAndFinish() {
        if (!validNameField())
            return;

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String name = mNameEditText.getText().toString();

        if (firebaseUser != null) {
            UserProfileChangeRequest.Builder profileChangeRequestBuilder =
                    new UserProfileChangeRequest.Builder()
                    .setDisplayName(name);

            firebaseUser.updateProfile(profileChangeRequestBuilder.build());
            UserSharedPreferences userSharedPreferences = new UserSharedPreferences(this);
            userSharedPreferences.setUserProfileConfigured(true);

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    /**
     * Downloads an existent profile picture from the remote uri,
     * then show it in the screen.
     * If the download fails (for reasons like network, invalid uri, etc), it
     * shows a default profile picture in the screen
     * @param photoRemoteUri A remote URL of the profile picture
     */
    private void downloadProfilePhoto(Uri photoRemoteUri) {
        mImageUploadProgressBar.setVisibility(View.VISIBLE);

        Picasso.with(this)
                .load(photoRemoteUri)
                .error(R.drawable.ic_default_profile_150dp)
                .into(mProfilePictureImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mImageUploadProgressBar.setVisibility(View.INVISIBLE);
                        mLastUploadedPhotoUri = photoRemoteUri;
                    }

                    @Override
                    public void onError() {
                        mImageUploadProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileConfigurationActivity.this,
                                R.string.picture_dowload_error_text,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Uploads a picture to Firebase Storage and then set it as profile picture
     * if the upload succeeds
     * @param photoLocalUri the local uri of the picture to upload
     */
    private void uploadProfilePhoto(Uri photoLocalUri) {
        mImageUploadProgressBar.setVisibility(View.VISIBLE);
        mPictureUploadErrorTextView.setVisibility(View.INVISIBLE);

        StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        String userUid = mFirebaseAuth.getCurrentUser().getUid();
        String pictureFileName = PROFILE_IMAGE_NAME_PREFIX + userUid;

        StorageReference profileImageRef =
                rootRef.child(STORAGE_REFERENCE_PROFILE_IMAGES + "/" + pictureFileName);

        final Bitmap photoBitmap = BitmapUtils.loadBitmapFromUri(photoLocalUri, this);
        final byte[] photoByteArray = BitmapUtils.getBitmapByteArray(photoBitmap);

        if (photoByteArray != null) {
            UploadTask photoUploadTask = profileImageRef.putBytes(photoByteArray);

            photoUploadTask.addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                mLastUploadedPhotoUri = taskSnapshot.getDownloadUrl();
                mImageUploadProgressBar.setVisibility(View.INVISIBLE);
                mProfilePictureImageView.setImageURI(photoLocalUri);
                updateUserAccountPhotoUri(taskSnapshot.getDownloadUrl());
            });

            photoUploadTask.addOnFailureListener((Exception e) -> {
                mPictureUploadErrorTextView.setText(R.string.picture_upload_error_text);
                mPictureUploadErrorTextView.setVisibility(View.VISIBLE);
                mImageUploadProgressBar.setVisibility(View.INVISIBLE);
            });
        } else {
            Toast.makeText(this, R.string.picture_not_existent_error, Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Updates the user profile uri
     * @param photoRemoteUri The remote uri
     */
    private void updateUserAccountPhotoUri(Uri photoRemoteUri) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest.Builder profileChangeRequestBuilder =
                    new UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoRemoteUri);

            user.updateProfile(profileChangeRequestBuilder.build());
        }
    }

    /**
     * Check if the name field is in a valid state
     * The name field isn't allowed to be empty or contain just
     * space characters (like space, tab, etc)
     * @return a boolean indicating if the name field is valid
     */
    private boolean validNameField() {
        boolean validField = true;

        String name = mNameEditText.getText().toString();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(name.trim())) {
            validField = false;
            Toast.makeText(this, R.string.empty_name_field_error, Toast.LENGTH_SHORT)
                    .show();
        }

        return validField;
    }
}
