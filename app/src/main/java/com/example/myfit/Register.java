package com.example.myfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mUsername, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            //"(?=.*[a-z])" +         //at least 1 lower case letter
            //"(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +    //any letter
            //"(?=.*[@#$%&*+=^])" +   //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{6,}" + "$");         //at least 6 characters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.UserName);
        mEmail = findViewById(R.id. Email);
        mPassword = findViewById(R.id.Password);
        mRegisterBtn = findViewById(R.id.registerbtn);
        mLoginBtn = findViewById(R.id.toLogin);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(),Profile.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String Email = mEmail.getText().toString().trim();
                String Password = mPassword.getText().toString().trim();
                String UserName = mUsername.getText().toString();

                if (TextUtils.isEmpty(UserName)) {
                    mUsername.setError("Username is required.");
                    return;
                }

                if (TextUtils.isEmpty(Email)) {
                    mEmail.setError("Please enter your email address");
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    mEmail.setError("Please input a valid email address.");
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    mPassword.setError("Password is required.");
                    return;
                }
                else if (!PASSWORD_PATTERN.matcher(Password).matches())
                {
                    mPassword.setError("Password too weak");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // send verification link

                            /*FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    Toast.makeText(Register.this, "Verification Email has been sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Log.d(TAG,"onFailure: Email not send." + e.getMessage());
                                }
                            });*/
                            // old code above ^ ^ ^

                            fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(Register.this, "User Created and PLEASE verify your email", Toast.LENGTH_SHORT).show();

                                        userID = fAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userID);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("fName", UserName);
                                        user.put("email", Email);
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG,"onFailure: " + e.toString());
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(Register.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                        else
                        {
                            Toast.makeText(Register.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}