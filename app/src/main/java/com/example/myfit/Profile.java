package com.example.myfit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class Profile extends AppCompatActivity {
    public static final String TAG = "TAG";
    TextView Uname, email;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String UserID;
    Button mbackBtn, mResetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home)
            {
                // Handle bottom_home selection
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                return true;
            }
            else if (item.getItemId() == R.id.bottom_recipe)
            {
                startActivity(new Intent(getApplicationContext(), Recipe.class));
                finish();
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
                // Handle profile selection
                return true;
            }
            return false;
        });

        Uname = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        mbackBtn = findViewById(R.id.backBtn);
        mResetBtn = findViewById(R.id.resetPassword);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        UserID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = fStore.collection("users").document(UserID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists())
                {
                    Uname.setText(documentSnapshot.getString("fName"));
                    email.setText(documentSnapshot.getString("email"));
                }
            }
        });

        mbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {
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
                                    Toast.makeText(Profile.this, "Reset Link sent to your Email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(Profile.this, "Error! The reset link is not sent." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(Profile.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
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
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}