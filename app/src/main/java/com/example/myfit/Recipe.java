package com.example.myfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.myfit.databinding.ActivityRecipeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Recipe extends AppCompatActivity {

    ActivityRecipeBinding binding;
    ListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();
    ListData listData;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int[] imageList = {R.drawable.pea_n_spinach_carbonara,
                R.drawable.chickpea_curry,
                R.drawable.spinach_n_feta_scrambled_egg_pitas,
                R.drawable.one_skillet_bourbon_chicken,
                R.drawable.salmon_chowder,
                R.drawable.salmon_caesar_salad,
                R.drawable.gnocchi};
        int[] ingredientList = {R.string.pastaIngredients,
                R.string.maggiIngredients,
                R.string.cakeIngredients,
                R.string.pancakeIngredients,
                R.string.chowderIngredients,
                R.string.saladIngredients,
                R.string.gnocchiIngredients};
        int[] descList = {R.string.pastaDesc,
                R.string.maggieDesc,
                R.string.cakeDesc,
                R.string.pancakeDesc,
                R.string.chowderdesc,
                R.string.saladdesc,
                R.string.gnocchidesc};
        String[] nameList = {"Carbonara",
                "Chickpea Curry",
                "Pittas",
                "Bourbon Chicken",
                "Salmon Chowder",
                "Salad Caesar Salad",
                "Cauliflower Gnocchi"};
        String[] timeList = {"20 mins", "20 mins", "15 mins","20 mins", "30mins", "20mins", "15mins"};
        String[] calList = {"430 cal", "278 cal", "303 cal", "521 cal", "178 cal", "291 cal", "280 cal"};

        for (int i=0; i < imageList.length; i++){
            listData = new ListData(nameList[i], timeList[i], calList[i], ingredientList[i], descList[i], imageList[i] );
            dataArrayList.add(listData);
        }
        listAdapter = new ListAdapter(Recipe.this, dataArrayList);
        binding.listview.setAdapter(listAdapter);
        binding.listview.setClickable(true);

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Recipe.this, DetailActivity.class);
                intent.putExtra("name", nameList[i]);
                intent.putExtra("time", timeList[i]);
                intent.putExtra("calories", calList[i]);
                intent.putExtra("ingredient", ingredientList[i]);
                intent.putExtra("desc", descList[i]);
                intent.putExtra("image", imageList[i]);
                startActivity(intent);
            }
        });
        // bottom navigate

        fAuth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_recipe);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home)
            {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.bottom_recipe)
            {
                // Handle bottom_recipe selection
                return true;
            }
            else if (item.getItemId() == R.id.bottom_note)
            {
                if(fAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(getApplicationContext(),NoteRecorder.class));
                }
                else {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.bottom_bmi)
            {
                startActivity(new Intent(getApplicationContext(), BmiCalculator.class));
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.bottom_profile)
            {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
                return true;
            }
            return false;
        });
    }
}