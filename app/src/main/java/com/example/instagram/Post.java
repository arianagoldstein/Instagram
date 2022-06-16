package com.example.instagram;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKED_BY = "likedBy";

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

    // getting the list of users who have liked this post
    public List<ParseUser> getLikedBy() {
        List<ParseUser> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null) {
            return new ArrayList<>();
        }
        return likedBy;
    }

    // setting the list of users who have liked this post
    public void setLikedBy(List<ParseUser> users) {
        put(KEY_LIKED_BY, users);
    }

    // returns the number of likes as a string
    public String getLikesCount() {
        int likeCount = getLikedBy().size();
        return String.valueOf(likeCount) + ((likeCount == 1) ? " like" : " likes");
    }

    // calculating how long ago this post was created
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
