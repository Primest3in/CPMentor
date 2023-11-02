package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MentorsActivity extends AppCompatActivity {
    RecyclerView mentorList;
    private ArrayList<Mentors> mentorArrayList;
    Toolbar toolbar;
    private MentorListAdapter mentorListAdapter;
    private ProgressBar progressB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentors);
        mentorList = findViewById(R.id.mentor_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mentorList.setLayoutManager(layoutManager);
        mentorArrayList = new ArrayList<>();
        mentorListAdapter = new MentorListAdapter(MentorsActivity.this, mentorArrayList);
        mentorList.setAdapter(mentorListAdapter);
        progressB = findViewById(R.id.progressBar1);
        toolbar = findViewById(R.id.home_toolbar_mentors);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available Mentors");

        load();

    }
    private void load() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mentorArrayList.clear();
                for(DataSnapshot i: snapshot.getChildren()) {
                    Mentors mentors = i.getValue(Mentors.class);
                    DataSnapshot isMentor = i.child("Mentor");
                    String Ok = isMentor.getValue(String.class);
                    if(Ok.equals("t")) {
                        mentorArrayList.add(mentors);
                    }


                }
                mentorListAdapter.notifyDataSetChanged();
                progressB.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MentorsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}