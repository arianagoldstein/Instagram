package com.example.instagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";

    // returns the description of the post
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    // sets the description of the post
    public void setDescription(String description) {
        // associates this key-value pair
        put(KEY_DESCRIPTION, description);
    }

    // get the image from this post
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    // set the image for this post
    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    // get the user that created this post
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // set the user for this post
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
