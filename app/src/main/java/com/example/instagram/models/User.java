package com.example.instagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("_User")
public class User extends ParseUser {

    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_BIO = "bio";

    public ParseFile getProfilePic() {
        return getParseFile(KEY_PROFILE_PIC);
    }

    public void setProfilePic(ParseFile profilePic) {
        put(KEY_PROFILE_PIC, profilePic);
    }

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public void setUsername(String username) {
        put(KEY_USERNAME, username);
    }

    public String getPassword() {
        return getString(KEY_PASSWORD);
    }

    public void setPassword(String password) {
        put(KEY_PASSWORD, password);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

}
