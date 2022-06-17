package com.example.instagram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.PostDetailsActivity;
import com.example.instagram.R;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

// this class will allow us to populate the recyclerview with posts
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    // for every visible item on the screen, we want to inflate (create) a view
    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    // whenever RecyclerView has an item to show, it calls this function
    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        // getting the post at this position
        Post post = posts.get(position);

        // binding that post to the viewholder
        holder.bind(post);
    }

    // returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return posts.size();
    }


    // clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // add a list of items
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // declaring XML elements
        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvLikesFeed;
        private ImageButton ibLikeFeed;
        private TextView tvCreatedAtFeed;
        private TextView tvUsernameFeedBottom;
        private ImageView ivProfileImageFeed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivPostImageFeed);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikesFeed = itemView.findViewById(R.id.tvLikesFeed);
            ibLikeFeed = itemView.findViewById(R.id.ibLikeFeed);
            tvCreatedAtFeed = itemView.findViewById(R.id.tvCreatedAtFeed);
            ivProfileImageFeed = itemView.findViewById(R.id.ivProfileImageFeed);
            tvUsernameFeedBottom = itemView.findViewById(R.id.tvUsernameFeedBottom);

            itemView.setOnClickListener(this);
        }

        // binds the post data to the elements in the layout
        public void bind(Post post) {
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvLikesFeed.setText(post.getLikesCount());
            tvUsernameFeedBottom.setText(post.getUser().getUsername());

            Date createdAt = post.getCreatedAt();
            String timeAgo = Post.calculateTimeAgo(createdAt);
            tvCreatedAtFeed.setText(timeAgo);

            if (post.isLikedByCurrentUser()) {
                ibLikeFeed.setBackgroundResource(R.drawable.fullheart);
            } else {
                ibLikeFeed.setBackgroundResource(R.drawable.emptyheart);
            }

            ParseFile image = post.getImage();

            // loading the image into the ImageView only if the image isn't null
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            ParseFile profilePic = post.getUser().getParseFile("profilePic");
            Glide.with(context).load(profilePic.getUrl())
                    .circleCrop()
                    .into(ivProfileImageFeed);
        }


        @Override
        public void onClick(View view) {
            // get item position
            int position = getAdapterPosition();

            // check if the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // accessing post at this position
                Post post = posts.get(position);

                // create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);

                // serialize the movie using Parceler, use its short name as a key
                intent.putExtra("post", post);

                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
