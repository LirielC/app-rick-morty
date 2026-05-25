package com.example.rickandmortyapp.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.ui.characters.CharacterListActivity;
import com.example.rickandmortyapp.ui.employees.EmployeeListActivity;
import com.example.rickandmortyapp.ui.home.HomeActivity;
import com.example.rickandmortyapp.util.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main_menu);

        MaterialCardView charactersCard = findViewById(R.id.charactersCard);
        MaterialCardView employeesCard = findViewById(R.id.employeesCard);
        Button charactersButton = findViewById(R.id.charactersButton);
        Button employeesButton = findViewById(R.id.employeesButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.mainBottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.navigation_overview);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_overview) {
                return true;
            }
            if (item.getItemId() == R.id.navigation_characters) {
                startActivity(new Intent(this, CharacterListActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.navigation_employees) {
                startActivity(new Intent(this, EmployeeListActivity.class));
                return true;
            }
            return false;
        });

        charactersCard.setOnClickListener(v -> startActivity(new Intent(this, CharacterListActivity.class)));
        employeesCard.setOnClickListener(v -> startActivity(new Intent(this, EmployeeListActivity.class)));
        charactersButton.setOnClickListener(v -> startActivity(new Intent(this, CharacterListActivity.class)));
        employeesButton.setOnClickListener(v -> startActivity(new Intent(this, EmployeeListActivity.class)));
        logoutButton.setOnClickListener(v -> {
            sessionManager.clear();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
