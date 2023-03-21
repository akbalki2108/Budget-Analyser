package com.example.myapplicationfirebase;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    ProgressBar splashProgress;
    Animation animation;
    private ImageView imageView;
    private TextView appName;

    int SPLASH_TIME = 3000; //This is 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);

//        splashProgress = findViewById(R.id.splashProgress);
//        playProgress();
//
//        //Code to start timer and take action after the timer ends
//        new Handler(getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do any action here. Now we are moving to next page
//                Intent intent = new Intent(SplashScreen.this,Register.class);
//                startActivity(intent);
//
//                //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
//                finish();
//
//            }
//        }, SPLASH_TIME);

        animation = AnimationUtils.loadAnimation(this,R.anim.animation);

        imageView = findViewById(R.id.imageView);
        appName = findViewById(R.id.appName);

        imageView.setAnimation(animation);
        appName.setAnimation(animation);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Register.class));
                finish();
            }
        }, SPLASH_TIME);
    }

    //Method to run progress bar for 5 seconds
//    private void playProgress() {
//        ObjectAnimator.ofInt(splashProgress, "progress", 100)
//                .setDuration(5000)
//                .start();
//    }


}