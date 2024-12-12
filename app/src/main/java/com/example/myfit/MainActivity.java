package com.example.myfit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                // Handle bottom_home selection
                return true;
            } else if (item.getItemId() == R.id.bottom_recipe) {
                startActivity(new Intent(getApplicationContext(), Recipe.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_note) {

                if(fAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(getApplicationContext(),NoteRecorder.class));
                }
                else {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_bmi) {
                startActivity(new Intent(getApplicationContext(), BmiCalculator.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
                return true;
            }

            return false;
        });
    }
}