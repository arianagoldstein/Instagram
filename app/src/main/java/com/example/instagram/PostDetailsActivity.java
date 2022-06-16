package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailsActivity";

    private ImageView ivProfileImage;
    private TextView tvUsernameDetailsTop;
    private ImageView ivPostImageDetails;
    private TextView tvUsernameDetailsBottom;
    private TextView tvDescriptionDetails;
    private TextView tvCreatedAtDetails;
    private TextView tvLikesDetails;

    private ImageButton ibLikeDetails;
    private ImageButton ibCommentDetails;
    private RecyclerView rvComments;
    private CommentsAdapter adapter;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // locating the XML elements in the layout
        tvUsernameDetailsTop = findViewById(R.id.tvUsernameDetailsTop);
        ivPostImageDetails = findViewById(R.id.ivPostImageDetails);
        tvUsernameDetailsBottom = findViewById(R.id.tvUsernameDetailsBottom);
        tvDescriptionDetails = findViewById(R.id.tvDescriptionDetails);
        tvCreatedAtDetails = findViewById(R.id.tvCreatedAtDetails);
        ibLikeDetails = findViewById(R.id.ibLikeDetails);
        ibCommentDetails = findViewById(R.id.ibCommentDetails);
        rvComments = findViewById(R.id.rvComments);
        tvLikesDetails = findViewById(R.id.tvLikesDetails);

        // unwrapping the Parcel so we can populate the details page with this post
        post = getIntent().getParcelableExtra("post");

        // populating the details page with this post's information
        tvUsernameDetailsTop.setText(post.getUser().getUsername());
        tvUsernameDetailsBottom.setText(post.getUser().getUsername());
        tvDescriptionDetails.setText(post.getDescription());
        tvLikesDetails.setText(post.getLikesCount());

        if (post.isLikedByCurrentUser()) {
            ibLikeDetails.setBackgroundResource(R.drawable.fullheart);
        } else {
            ibLikeDetails.setBackgroundResource(R.drawable.emptyheart);
        }


        // calculating how long ago this post was created and updating the textview accordingly
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvCreatedAtDetails.setText(timeAgo);

        // loading the image into the ImageView only if the image isn't null
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivPostImageDetails);
        }

        // setting an onclick listener for the comment button
        ibCommentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PostDetailsActivity.this, CommentActivity.class);
                i.putExtra("post", post);
                startActivity(i);
            }
        });

        // creating a comments adapter to show comments for this post
        adapter = new CommentsAdapter();
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        refreshComments();

        // triggers when the user likes this post
        ibLikeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ParseUser> likedBy = post.getLikedBy();
                ParseUser user = ParseUser.getCurrentUser();

                // check if we've already liked this image
                if (post.isLikedByCurrentUser()) {
                    // need to unlike
                    post.unlike();
                    ibLikeDetails.setBackgroundResource(R.drawable.emptyheart);
                } else {
                    // need to like
                    post.like();
                    ibLikeDetails.setBackgroundResource(R.drawable.fullheart);
                }

                post.setLikedBy(likedBy);

                // uploads new values back to database
                post.saveInBackground();

                // updating number of likes
                tvLikesDetails.setText(post.getLikesCount());

            }
        });
    }

    // runs when we come back to the details activity after posting a new comment
    // when we say "finish" on a future activity and come back to this one
    @Override
    protected void onRestart() {
        super.onRestart();
        refreshComments();
    }

    // this function queries the database for the comments for this post
    void refreshComments() {
        // we want to load all of the comments for this post
        ParseQuery<Comment> query = new ParseQuery<Comment>("Comment");

        // getting comments specifically for this post
        query.whereEqualTo(Comment.KEY_POST, post);

        // ordering by how recently the comment was made
        query.orderByDescending("createdAt");
        query.include(Comment.KEY_AUTHOR);

        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                // if the query for the comments is unsuccessful, we will get an exception
                if (e != null) {
                    Log.e(TAG, "Failed to get comments", e);
                    return;
                }
                // we clear before adding these comments so we don't see duplicate comments
                adapter.mComments.clear();
                adapter.mComments.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }


}