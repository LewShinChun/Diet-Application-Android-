package com.example.myfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NoteCreate extends AppCompatActivity {
    EditText mcreateNoteTitle, mcreateNoteContent;
    FloatingActionButton msaveNote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_create);

        msaveNote=findViewById(R.id.saveNote);
        mcreateNoteContent=findViewById(R.id.createNoteContent);
        mcreateNoteTitle=findViewById(R.id.createNoteTitle);

        Toolbar toolbar = findViewById(R.id.addNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        msaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=mcreateNoteTitle.getText().toString();
                String content=mcreateNoteContent.getText().toString();
                if(title.isEmpty()||content.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Both field are required to have content",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    DocumentReference documentReference=firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("myNotes").document();
                    Map<String, Object> note=new HashMap<>();
                    note.put("title", title);
                    note.put("content",content);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Note create successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NoteCreate.this,NoteRecorder.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed to create note.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NoteCreate.this,NoteRecorder.class));
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}