package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.models.User;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // checking if the user is already logged in
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // finding the XML elements and instantiating them
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignupLink = findViewById(R.id.tvSignupLink);

        // setting a listener for the login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        // setting a listener for the signup button
        tvSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup link");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signupUser(username, password);
            }
        });

    }

    private void signupUser(String username, String password) {
        Log.i(TAG, "Attempting to sign up user " + username);

        // creating a new user to add to the database
        // ParseUser user = new ParseUser();
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // if the signup is not successful, we'll get an exception
                if (e != null) {
                    Log.e(TAG, "Issue with login: ", e);
                    return;
                }
                // if the action succeeds, then the exception e will be null and we can start the main activity
                goMainActivity();
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to log in user " + username);

        User.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // if the login is not successful, we'll get an exception
                if (e != null) {
                    Log.e(TAG, "Issue with login: ", e);
                    return;
                }
                // if the action succeeds, then the exception e will be null and we can start the main activity
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}