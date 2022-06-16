package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";

    private RecyclerView rvPosts;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    protected SwipeRefreshLayout swipeContainer;

    private EndlessRecyclerViewScrollListener scrollListener;

    public FeedFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    // triggered soon after onCreateView
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // looking up views in the layout
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        rvPosts = view.findViewById(R.id.rvPosts);

        // initializing empty list for posts
        allPosts = new ArrayList<>();

        // creating the adapter and setting it for the recyclerview
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);

        // set the layout manager on the recyclerview
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(llm);

        // query posts from the database
        queryPosts(0);

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

        // setting up endless scrolling
//        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                queryPosts(allPosts.size());
//            }
//        };
//
//        rvPosts.addOnScrollListener(scrollListener);
    }

    // method to query our Parse server to return the most recent 20 posts
    protected void queryPosts(int i) {
        // specifying the type of data we want to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // include data referred by user key
        query.include(Post.KEY_USER);

        // limit query to latest 20 items
        query.setLimit(5);
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

                // save these posts to the list and notify the adapter to update
                allPosts.clear();
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();

            }
        });
    }
}