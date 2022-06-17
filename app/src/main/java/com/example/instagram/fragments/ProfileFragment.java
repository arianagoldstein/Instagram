package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.instagram.models.User;
import com.example.instagram.utility.BitmapScaler;
import com.example.instagram.utility.EndlessRecyclerViewScrollListener;
import com.example.instagram.models.Post;
import com.example.instagram.adapters.ProfileAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseFragment {
    public static final String TAG = "ProfileFragment";

    // declaring elements in layout
    ParseUser userToFilterBy;
    RecyclerView rvProfile;
    ProfileAdapter adapter;
    List<Post> allPosts;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    TextView tvNumPostsNum;
    TextView tvUsernameProfile;
    int numPostsByThisUser;
    ImageView ivProfileImageProfile;
    TextView tvBio;

    // storing the current User
    User user = (User) ParseUser.getCurrentUser();

    // constructor
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
        ivProfileImageProfile = view.findViewById(R.id.ivProfileImageProfile);
        tvUsernameProfile = view.findViewById(R.id.tvUsernameProfile);
        tvBio = view.findViewById(R.id.tvBio);

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

        // getting all the user info from parse
//        user.fetchInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                user = (User) object;
//
//                // calling function to display user information
//                displayUserInfo();
//            }
//        });

        // querying the posts created by the logged-in user
        queryPosts(0);

        // creating an onclick listener for the profile image
        // when we click on it, we should be able to update it
        ivProfileImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        // calling function to display user information
        displayUserInfo();

    }

    // function that displays the user's username, bio, and profile picture
    private void displayUserInfo() {
        tvUsernameProfile.setText(ParseUser.getCurrentUser().getUsername());

        // populating the bio field
        tvBio.setText(user.getBio());

        //ParseFile profilePic = ParseUser.getCurrentUser().getParseFile("profilePic");
        ParseFile profilePic = user.getProfilePic();
        Glide.with(this).load(profilePic.getUrl())
                .circleCrop()
                .into(ivProfileImageProfile);
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


    // getting the image back from the user
    // invoked when the camera app returns to the Instagram application
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if the photo capture was successful, we can load the image onto the page as a preview for the user
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // load the taken image into a preview
                Glide.with(this).load(takenImage).circleCrop().into(ivProfileImageProfile);

                // constructing a new file and saving it to the database
                ParseFile newPic = new ParseFile(photoFile);
                user.setProfilePic(newPic);
                user.saveInBackground();

            } else { // result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
