package com.example.instagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("sSwooDvTPqkGc1wnTNRVf0gWMOq8RjbN7jER0wA0")
                .clientKey("RZwCiSoTNELSbWQN4kDnc6F3aJctv8p7qMbEXmsi")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
