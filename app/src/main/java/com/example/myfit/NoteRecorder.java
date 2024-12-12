package com.example.myfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteRecorder extends AppCompatActivity {

    FloatingActionButton mcreateNote;
    private FirebaseAuth firebaseAuth;
    RecyclerView mRecycleView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder> noteAdapter;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_recorder);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigateView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_note);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            } else if (item.getItemId() == R.id.bottom_recipe) {
                startActivity(new Intent(getApplicationContext(), Recipe.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.bottom_note) {
                // Handle bottom_note selection
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

        mcreateNote=findViewById(R.id.createNote);
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("All Notes");
            }

        mcreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(NoteRecorder.this,NoteCreate.class));

            }
        });

            Query query=firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<firebaseModel> allUserNote= new FirestoreRecyclerOptions.Builder<firebaseModel>().setQuery(query, firebaseModel.class).build();

            noteAdapter= new FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(allUserNote) {
                @Override
                protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull firebaseModel firebaseModel) {

                    ImageView popUpBtn=noteViewHolder.itemView.findViewById(R.id.menuPopup);

                    int colorCode=getRandomColor();
                    noteViewHolder.mNote.setBackgroundColor(noteViewHolder.itemView.getResources().getColor(colorCode, null));

                    noteViewHolder.noteTitle.setText(firebaseModel.getTitle());
                    noteViewHolder.noteContent.setText(firebaseModel.getContent());

                    String docId=noteAdapter.getSnapshots().getSnapshot(i).getId();

                    noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // we have to open note detail activity

                            Intent intent=new Intent(v.getContext(), NoteDetails.class);
                            intent.putExtra("title",firebaseModel.getTitle());
                            intent.putExtra("content",firebaseModel.getContent());
                            intent.putExtra("noteId",docId);
                            v.getContext().startActivity(intent);
                            //Toast.makeText(getApplicationContext(),"This is Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

                    popUpBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                            popupMenu.setGravity(Gravity.END);
                            popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem item) {

                                    Intent intent=new Intent(v.getContext(), NoteEdit.class);
                                    intent.putExtra("title",firebaseModel.getTitle());
                                    intent.putExtra("content",firebaseModel.getContent());
                                    intent.putExtra("noteId",docId);
                                    v.getContext().startActivity(intent);
                                    return false;
                                }
                            });

                            popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(@NonNull MenuItem item) {
                                    //Toast.makeText(v.getContext(), "The note is deleted.", Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference=firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            Toast.makeText(v.getContext(), "The note is deleted.", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(v.getContext(), "Fail to delete.", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    return false;
                                }
                            });

                            popupMenu.show();
                        }
                    });
                }

                @NonNull
                @Override
                public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout,parent,false);
                    return new NoteViewHolder(view);
                }
            };

            mRecycleView=findViewById(R.id.recycleView);
            mRecycleView.setHasFixedSize(true);
            staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            mRecycleView.setLayoutManager(staggeredGridLayoutManager);
            mRecycleView.setAdapter(noteAdapter);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView noteTitle;
        private TextView noteContent;
        LinearLayout mNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle=itemView.findViewById(R.id.noteTitle);
            noteContent=itemView.findViewById(R.id.noteContent);
            mNote=itemView.findViewById(R.id.note);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter != null)
        {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor()
    {
        List<Integer> colorCode=new ArrayList<>();
        colorCode.add(R.color.green);
        colorCode.add(R.color.skyBlue);
        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);
        colorCode.add(R.color.color6);
        colorCode.add(R.color.color7);

        Random random = new Random();
        int number=random.nextInt(colorCode.size());
        return colorCode.get(number);
    }
}