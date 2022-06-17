package com.example.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.utility.EndlessRecyclerViewScrollListener;
import com.example.instagram.models.Post;
import com.example.instagram.adapters.ProfileAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    ParseUser userToFilterBy;
    RecyclerView rvProfile;
    ProfileAdapter adapter;
    List<Post> allPosts;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    TextView tvNumPostsNum;
    int numPostsByThisUser;

    public ProfileFragment(ParseUser userToFilterBy) {
        this.userToFilterBy = userToFilterBy;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // called when fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // triggered soon after onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvProfile = view.findViewById(R.id.rvProfile);

        // initializing empty list for posts
        allPosts = new ArrayList<>();

        // creating the adapter and setting it for the recyclerview
        adapter = new ProfileAdapter(getContext(), allPosts);
        rvProfile.setAdapter(adapter);

        int numberOfColumns = 3;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);

        // looking up views in the layout
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        tvNumPostsNum = view.findViewById(R.id.tvNumPostsNum);

        // set the layout manager on the recyclerview
        rvProfile.setLayoutManager(gridLayoutManager);

        // setting up refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts(0);
            }
        });

        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        queryPosts(0);

    }

    // method to query our Parse server to return the most recent 20 posts
    protected void queryPosts(int i) {
        // specifying the type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // include data referred by these keys
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, userToFilterBy);

        // limit query to latest 20 items
        query.setLimit(20);
        query.setSkip(i);

        // order posts by the order they were created with the newest first
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                swipeContainer.setRefreshing(false);

                // check for error/success
                if (e != null) {
                    Log.e(TAG, "Issue getting posts.", e);
                    return;
                }

                // at this point, we have gotten the posts successfully
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                numPostsByThisUser = posts.size();
                tvNumPostsNum.setText(String.valueOf(numPostsByThisUser));

                // save these posts to the list and notify the adapter to update
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();

            }
        });
    }
}
