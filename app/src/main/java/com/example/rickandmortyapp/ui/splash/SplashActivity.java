package com.example.rickandmortyapp.ui.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.ui.home.HomeActivity;

public class
SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View backgroundImage = findViewById(R.id.splashBackgroundImageView);
        View splashContent = findViewById(R.id.splashContent);
        View splashLogo = findViewById(R.id.splashLogoContainer);
        TextView title = findViewById(R.id.splashTitleTextView);
        TextView subtitle = findViewById(R.id.splashSubtitleTextView);
        View overlay = findViewById(R.id.splashOverlayView);

        splashContent.setAlpha(0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(backgroundImage, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(splashContent, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(splashContent, "scaleX", 0.96f, 1f),
                ObjectAnimator.ofFloat(splashContent, "scaleY", 0.96f, 1f),
                ObjectAnimator.ofFloat(overlay, "alpha", 0.95f, 0.82f),
                ObjectAnimator.ofFloat(splashLogo, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(splashLogo, "scaleX", 0.82f, 1f),
                ObjectAnimator.ofFloat(splashLogo, "scaleY", 0.82f, 1f),
                ObjectAnimator.ofFloat(title, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(title, "translationY", 34f, 0f),
                ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(subtitle, "translationY", 18f, 0f)
        );
        animatorSet.setDuration(1050L);
        animatorSet.start();

        title.postDelayed(() -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }, 1500L);
    }
}
