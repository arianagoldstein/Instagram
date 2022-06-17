package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CommentActivity extends AppCompatActivity {

    public static final String TAG = "CommentActivity";

    Post post;
    Button btnComment;
    EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // finding XML elements for editText and button
        btnComment = findViewById(R.id.btnComment);
        etComment = findViewById(R.id.etComment);

        // getting the post this comment was made on
        post = getIntent().getParcelableExtra("post");

        // setting listener for comment button
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting body of the comment from the user
                String body = etComment.getText().toString();

                // constructing a Comment object
                Comment commentToAdd = new Comment();
                commentToAdd.setBody(body);
                commentToAdd.setPost(post);
                commentToAdd.setAuthor(ParseUser.getCurrentUser());

                commentToAdd.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error adding comment", e);
                            return;
                        }
                        // going back to the post details activity
                        finish();
                    }
                });
            }
        });
    }
}