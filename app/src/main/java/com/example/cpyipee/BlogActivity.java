package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class BlogActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ArrayList<Post> blogList;
    private AdapterFeed postAdapter;
    private ProgressBar progressCircular;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        fab = findViewById(R.id.floatingActionButton);
        progressCircular = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.home_toolbar_blog);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blogs");



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlogActivity.this, MakeBlogActivity.class);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.blog_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        blogList = new ArrayList<>();
        postAdapter = new AdapterFeed(BlogActivity.this, blogList);
        recyclerView.setAdapter(postAdapter);

        loadFeed();

    }
    private void loadFeed() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blogList.clear();
                for(DataSnapshot i: snapshot.getChildren()) {
                    Post post = i.getValue(Post.class);
                    String a = i.child("isPost").getValue(String.class);
                    if(a.equals("f"))
                         blogList.add(post);
                }
                Collections.reverse(blogList);
                postAdapter.notifyDataSetChanged();
                progressCircular.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BlogActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}