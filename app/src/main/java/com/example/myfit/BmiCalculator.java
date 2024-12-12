package com.example.myfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DecimalFormat;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class BmiCalculator extends AppCompatActivity {
    double height_of_user;
    double weight_of_user;
    double bmi;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_calculator);

        EditText height = (EditText) findViewById(R.id.txtHeight);
        EditText weight = (EditText) findViewById(R.id.txtWeight);
        Button calculate = (Button) findViewById(R.id.btnCalc);
        TextView result = ((TextView)findViewById(R.id.txtResult));

        fAuth = FirebaseAuth.getInstance();

        calculate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                height_of_user=Double.parseDouble(height.getText().toString());
                weight_of_user=Double.parseDouble(weight.getText().toString());
                bmi = weight_of_user/(height_of_user*height_of_user);
                DecimalFormat deci = new DecimalFormat("###.##");

                if(bmi < 18.50)
                {
                    result.setText("Your BMI is "+ deci.format(bmi) +" and you are underweight.");
                }
                else if(bmi >= 18.50 && bmi <= 24.90)
                {
                    result.setText("Your BMI is "+ deci.format(bmi) +" and you are normal");
                }
                else if(bmi >= 25.00 && bmi <= 29.90)
                {
                    result.setText("Your BMI is "+ deci.format(bmi) +" and you are overweight");
                }
                else if(bmi >= 30.00 && bmi <= 34.90)
                {
                    result.setText("Your BMI is "+ deci.format(bmi) +" and you are obese");
                }
                else
                {
                    result.setText("Your BMI is "+ deci.format(bmi) +" and you are extremely obese");
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_bmi);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                // Handle bottom_bmi selection
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