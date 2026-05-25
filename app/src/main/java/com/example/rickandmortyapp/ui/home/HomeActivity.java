package com.example.rickandmortyapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.ui.login.LoginActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button enterButton = findViewById(R.id.enterButton);
        enterButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }
}
