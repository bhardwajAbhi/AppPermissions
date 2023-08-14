package com.project.app.permissions.manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.project.app.permissions.manager.R;
import com.project.app.permissions.manager.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private List<ImageView> images = new ArrayList<>();
    private ImageView prevImage;
    private int index = 0;

    private boolean homeActivityOpened = false;
    private Handler handler = new Handler();
    private int animationDelay = 100; // .5 seconds
    private int homeActivityDelay = 1500; // 3 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Tools.setSystemBarColor(this, R.color.white);

        images.add(findViewById(R.id.image_bg_1));
        images.add(findViewById(R.id.image_bg_2));
        images.add(findViewById(R.id.image_bg_3));

        prevImage = images.get(index);
        animateLoading();
    }

/*    private void animateLoading() {
        new Handler().postDelayed(() -> {
            if(prevImage != null) prevImage.setColorFilter(ContextCompat.getColor(SplashScreenActivity.this, R.color.color_primary_light));
            ImageView img = images.get(index);
            img.setColorFilter(ContextCompat.getColor(SplashScreenActivity.this, R.color.grey));
            prevImage = img;
            index++;
            if(index > images.size()-1) index = 0;
            animateLoading();
        }, 1500);

    }*/

    private void animateLoading() {
        handler.postDelayed(this::animateNextImage, animationDelay);
    }

    private void animateNextImage() {
        if (prevImage != null) prevImage.setColorFilter(ContextCompat.getColor(SplashScreenActivity.this, R.color.color_primary_light));
        ImageView img = images.get(index);
        img.setColorFilter(ContextCompat.getColor(SplashScreenActivity.this, R.color.grey));
        prevImage = img;
        index++;
        if (index > images.size() - 1) index = 0;
        animateLoading();

        // Check if HomeActivity should be opened
        if (!homeActivityOpened) {
            handler.postDelayed(this::openHomeActivity, homeActivityDelay);
            homeActivityOpened = true;
        }
    }

    private void openHomeActivity() {
        //check if user has opened the app for the first time, if yes, show intro activity else home activity
        boolean isFirstTime = getSharedPreferences(Tools.SHARED_PREFERENCE_KEY, MODE_PRIVATE).getBoolean(Tools.KEY_FIRST_OPEN, true);
        Intent intent = null;
        if (isFirstTime){
            intent = new Intent(SplashScreenActivity.this, IntroActivity.class);
        } else {
            intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        }
        startActivity(intent);
        finish(); // Optional: If you want to finish the SplashScreenActivity after opening HomeActivity
    }




}