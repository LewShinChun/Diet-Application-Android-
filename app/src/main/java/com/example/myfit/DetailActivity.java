package com.example.myfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myfit.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = this.getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String time = intent.getStringExtra("time");
            String calories = intent.getStringExtra("calories");
            int ingredients = intent.getIntExtra("ingredients", R.string.maggiIngredients);
            int desc = intent.getIntExtra("desc", R.string.maggieDesc);
            int image = intent.getIntExtra("image", R.drawable.pea_n_spinach_carbonara);

            binding.detailName.setText(name);
            binding.detailTime.setText(time);
            binding.detailCalories.setText(calories);
            binding.detailDesc.setText(desc);
            binding.detailIngredients.setText(ingredients);
            binding.detailImage.setImageResource(image);
        }
    }
}