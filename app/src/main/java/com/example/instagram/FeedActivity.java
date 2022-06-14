package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    public static final String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        rvPosts = findViewById(R.id.rvPosts);

        // initializing list of posts and the adapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(this, allPosts);

        // setting the adapter for the recyclerview
        rvPosts.setAdapter(adapter);

        // set the layout manager on the recyclerview
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        // query posts from the database
        queryPosts();
    }

    // method to query our Parse server to return the most recent 20 posts
    private void queryPosts() {
        // specifying the type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // include data referred by user key
        query.include(Post.KEY_USER);

        // limit query to latest 20 items
        query.setLimit(20);

        // order posts by the order they were created with the newest first
        query.addDescendingOrder("createdAt");

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for error/success
                if (e != null) {
                    Log.e(TAG, "Issue getting posts.", e);
                    return;
                }
                // at this point, we have gotten the posts successfully
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save these posts to the list and notify the adapter to update
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}