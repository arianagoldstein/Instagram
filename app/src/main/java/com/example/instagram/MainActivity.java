package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.instagram.fragments.ComposeFragment;
import com.example.instagram.fragments.FeedFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.Parse;
import com.parse.ParseUser;

// main activity where each of the 3 fragments will be accessible via the bottom navigation bar
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    FeedFragment feedFragment = new FeedFragment();
    ComposeFragment composeFragment = new ComposeFragment(MainActivity.this);
    ProfileFragment profileFragment = new ProfileFragment();

    // declaring items in layout
    public BottomNavigationView bottomNavigationView;
    private Button btnLogout;

    // setting up fragments
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // logout button
        btnLogout = findViewById(R.id.btnLogout);

        // setting a listener for the logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick logout button");
                ParseUser.logOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        // top toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.smaller_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // bottom navbar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // determining which fragment to display based on which item is selected
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_feed:
                        fragment = feedFragment;
                        break;
                    case R.id.action_compose:
                        fragment = composeFragment;
                        break;
                    case R.id.action_profile:
                        profileFragment.user = (User) ParseUser.getCurrentUser();
                        fragment = profileFragment;
                        break;
                    default:
                        fragment = new FeedFragment();
                        break;
                }
                // replacing the frame layout with the selected fragment
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // set default selection to feed
        bottomNavigationView.setSelectedItemId(R.id.action_feed);
    }

    // brings user to Feed tab
    public void goToFeedFragment() {
        bottomNavigationView.setSelectedItemId(R.id.action_feed);
    }

    // brings user to Profile tab
    public void goToProfileFragment(User user) {
        // indicating which user's profile we want to view on the profile tab
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        profileFragment.user = user;
    }

}