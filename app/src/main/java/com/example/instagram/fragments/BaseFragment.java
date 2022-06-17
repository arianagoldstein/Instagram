package com.example.instagram.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;

// has everything we need to go to the camera app and take a picture
public class BaseFragment extends Fragment {

    public static final String TAG = "BaseFragment";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int RESULT_OK = -1;
    public File photoFile;
    public String photoFileName;

    // helper function to get a URI for the image file
    private File getPhotoFileUri(String fileName) {

        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    // launches implicit intent to open the phone's camera and take a photo for the post
    protected void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // simply specifying a file to write to can lead to a lot of concern if the app that will be using the file
        // can't access the file

        // wrap File object into a content provider, make our application a file provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.ParseApplication", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // checking if there is an app on this phone that can handle this intent
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            Log.i(TAG, "Going to phone camera app to take a picture");
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Log.e(TAG, "Could not go to camera app.");
        }
    }
}
