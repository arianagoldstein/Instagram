package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeActivity extends AppCompatActivity {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String TAG = "ComposeActivity";

    private Button btnLogout;
    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private File photoFile;
    public String photoFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // instantiating XML elements
        btnLogout = findViewById(R.id.btnLogout);
        etDescription = findViewById(R.id.etDescription);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        ivPostImage = findViewById(R.id.ivPostImageCompose);
        btnSubmit = findViewById(R.id.btnSubmit);

        // setting a listener for the logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick logout button");
                ParseUser.logOut();
            }
        });

        // queryPosts();

        // setting a listener for the submit button to create a post
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting the user's description for the post
                String description = etDescription.getText().toString();

                // can't post with an empty description
                if (description.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // can't post without a picture
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(ComposeActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // calling the savePost function to create a new post in the database
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);
            }
        });

        // setting a listener for the "take image" button
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }

    // launches implicit intent to open the phone's camera and take a photo for the post
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // simply specifying a file to write to can lead to a lot of concern if the app that will be using the file
        // can't access the file

        // wrap File object into a content provider, make our application a file provider
        Uri fileProvider = FileProvider.getUriForFile(ComposeActivity.this, "com.codepath.fileprovider.ParseApplication", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // checking if there is an app on this phone that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.i(TAG, "Going to phone camera app to take a picture");
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Log.e(TAG, "Could not go to camera app.");
        }
    }

    // getting the image back from the user
    // invoked when the camera app returns to the Instagram application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the photo capture was successful, we can load the image onto the page as a preview for the user
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // resizing bitmap
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 150);

                // load the taken image into a preview
                ivPostImage.setImageBitmap(resizedBitmap);
            } else { // result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // helper function to get a URI for the image file
    private File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        // constructing a new post with the given information from the user
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currentUser);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ComposeActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                // if we get here, the post was saved successfully
                Log.i(TAG, "Post added successfully!");

                // clearing description and image area when post is successfully saved
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });

    }
}