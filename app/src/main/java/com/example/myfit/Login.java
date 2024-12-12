package com.example.myfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn; //resendEmail;
    TextView mCreateBtn, forgotPBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id. Email);
        mPassword = findViewById(R.id.Password);
        mLoginBtn = findViewById(R.id.loginbtn);
        mCreateBtn = findViewById(R.id.toRegister);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        forgotPBtn = findViewById(R.id.forgotPassword);

        //resendEmail = findViewById(R.id.resendEmail);

        //final FirebaseUser fUser = fAuth.getCurrentUser();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String Email = mEmail.getText().toString().trim();
                String Password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(Email))
                {
                    mEmail.setError("Email is required.");
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    mEmail.setError("Please input a valid email address.");
                    return;
                }

                if (TextUtils.isEmpty(Password))
                {
                    mPassword.setError("Password is required.");
                    return;
                }

                if(Password.length() < 6)
                {
                    mPassword.setError("Password Must be at least 6 characters.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user

                fAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (fAuth.getCurrentUser() != null && fAuth.getCurrentUser().isEmailVerified())
                            {
                                Toast.makeText(Login.this, "You have logged in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Profile.class));
                            }
                            else
                            {
                                Toast.makeText(Login.this, "Please verify your email before logging in. Please check your email to verify.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            // Check if the exception is not null before accessing its message
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(Login.this, "Error! " + errorMessage, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        /*resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert fUser != null; // Check that fUser is not null
                fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getContext(),"Verification email has been sent.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", "onFailure: Email has not sent" + e.getMessage());
                    }
                });
            }
        });*/

        mCreateBtn.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        forgotPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset your password?");
                passwordResetDialog.setMessage("Enter your email to get your password reset link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // extract the email and send the reset link

                        String mail = resetMail.getText().toString();

                        if (!TextUtils.isEmpty(mail)) {
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    Toast.makeText(Login.this, "Reset password Link sent to your Email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(Login.this, "Error! The reset link is not sent." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(Login.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }
}