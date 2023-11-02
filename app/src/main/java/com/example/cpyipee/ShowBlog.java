package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ShowBlog extends AppCompatActivity {
    private String mentorId;
    private RecyclerView mentorBlogRecycler;
    private ArrayList<Post> postList;
    private AdapterFeed postAdapter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_blog);
        mentorId = getIntent().getStringExtra("key");
        mentorBlogRecycler = findViewById(R.id.mentor_blog_list);
        progressBar = findViewById(R.id.progressBarrr);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mentorBlogRecycler.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
        postAdapter = new AdapterFeed(ShowBlog.this, postList);
        mentorBlogRecycler.setAdapter(postAdapter);


        load();


    }

    private void load() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Blogs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot i: snapshot.getChildren()) {
                    Post post = i.getValue(Post.class);

                    DataSnapshot isThat = i.child("Publisher");
                    String Ok = isThat.getValue(String.class);
                    if(Ok.equals(mentorId)) {
                        postList.add(post);
                    }


                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowBlog.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}