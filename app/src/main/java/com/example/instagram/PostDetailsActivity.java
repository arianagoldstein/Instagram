package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private TextView tvUsernameDetailsTop;
    private ImageView ivPostImageDetails;
    private TextView tvUsernameDetailsBottom;
    private TextView tvDescriptionDetails;
    private TextView tvCreatedAtDetails;

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

        // unwrapping the Parcel so we can populate the details page with this post
        // post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        post = getIntent().getParcelableExtra("post");

        // populating the details page with this post's information
        tvUsernameDetailsTop.setText(post.getUser().getUsername());
        tvUsernameDetailsBottom.setText(post.getUser().getUsername());
        tvDescriptionDetails.setText(post.getDescription());

        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvCreatedAtDetails.setText(timeAgo);

        ParseFile image = post.getImage();

        // loading the image into the ImageView only if the image isn't null
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivPostImageDetails);
        }
    }
}