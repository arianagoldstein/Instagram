package com.example.instagram;

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
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

// this class will allow us to populate the recyclerview with posts
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public static final String TAG = "ProfileAdapter";
    private Context context;
    private List<Post> posts;

    public ProfileAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    // for every visible item on the screen, we want to inflate (create) a view
    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_profile, parent, false);
        return new ViewHolder(view);
    }

    // whenever RecyclerView has an item to show, it calls this function
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
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

        // declaring XML element (just the imageview for the picture)
        private final ImageView ivUserPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPost = itemView.findViewById(R.id.ivUserPost);

            ivUserPost.setOnClickListener(this);
        }

        // binds the post data to the elements in the layout
        public void bind(Post post) {

            ParseFile image = post.getImage();

            // loading the image into the ImageView only if the image isn't null
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivUserPost);
            }
        }

        // onClick method so that when you click on the image, it goes to the detail view
        @Override
        public void onClick(View v) {
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
