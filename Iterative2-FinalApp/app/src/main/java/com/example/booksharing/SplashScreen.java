package com.example.booksharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashScreen extends AppCompatActivity {


    String isLoggedin = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        isLoggedin = preferences.getString("isLoggedIn", "0");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedin.equals("0")) {
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                    finish();
                }
                else if (isLoggedin.equals("1")){
                    Intent intent = new Intent(SplashScreen.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }
}