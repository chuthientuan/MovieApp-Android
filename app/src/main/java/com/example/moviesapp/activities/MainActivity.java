package com.example.moviesapp.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.moviesapp.R;
import com.example.moviesapp.fragment.ExplorerFragment;
import com.example.moviesapp.fragment.FavoritesFragment;
import com.example.moviesapp.fragment.ProfileFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chipNavigationBar = findViewById(R.id.chipNavigationBar);
        chipNavigationBar.setOnItemSelectedListener(i -> {
            Fragment selectedFragment = null;
            if (i == R.id.explorer) {
                selectedFragment = new ExplorerFragment();
            } else if (i == R.id.favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (i == R.id.profile) {
                selectedFragment = new ProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
        });
        chipNavigationBar.setItemSelected(R.id.explorer, true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ExplorerFragment())
                .commit();
    }
}