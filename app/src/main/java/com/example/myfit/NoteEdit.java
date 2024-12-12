package com.example.myfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.util.Objects;

public class NoteEdit extends AppCompatActivity {

    Intent data;
    EditText mEditTitle, mEditNoteContent;
    FloatingActionButton mSaveEditNote;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        mEditTitle=findViewById(R.id.editTitle);
        mEditNoteContent=findViewById(R.id.editNoteContent);
        mSaveEditNote=findViewById(R.id.saveEditNote);

        data=getIntent();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar=findViewById(R.id.editNote);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mSaveEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "savebutton clicked", Toast.LENGTH_SHORT).show();

                String newTitle=mEditTitle.getText().toString();
                String newContent=mEditNoteContent.getText().toString();

                if(newTitle.isEmpty()||newContent.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Something is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    DocumentReference documentReference=firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("myNotes").document(Objects.requireNonNull(data.getStringExtra("noteId")));
                    Map <String,Object> note=new HashMap<>();
                    note.put("title", newTitle);
                    note.put("content",newContent);
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(getApplicationContext(), "Note is updated", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(NoteEdit.this, NoteRecorder.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "Note is fail to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        String noteTitle=data.getStringExtra("title");
        String noteContent=data.getStringExtra("content");
        mEditTitle.setText(noteTitle);
        mEditNoteContent.setText(noteContent);
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