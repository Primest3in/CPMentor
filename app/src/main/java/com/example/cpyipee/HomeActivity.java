package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ProgressBar progressCircular;
    private String userId;

    private AdapterFeed postAdapter;
    private ArrayList<Post> postList;
    ImageView navHeaderImg;
    TextView navHeaderE, navHeaderU;
    private Context context;
    private TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        recyclerView = findViewById(R.id.recycler_view);
        progressCircular = findViewById(R.id.progress_circular);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.home_toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navHeaderImg = navigationView.getHeaderView(0).findViewById(R.id.nav_header_profile_img);
        navHeaderE = navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);
        navHeaderU = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userImg = snapshot.child("DownloadUrl").getValue(String.class);
                String userEm = snapshot.child("Email").getValue(String.class);
                String userNm = snapshot.child("UserName").getValue(String.class);
                Glide.with(context).load(userImg).transform(new CircleCrop()).into(navHeaderImg);
                navHeaderE.setText(userEm);
                navHeaderU.setText(userNm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MakePostActivity.class);
                startActivity(intent);
            }
        });

        postList = new ArrayList<>();
        postAdapter = new AdapterFeed(HomeActivity.this, postList);
        recyclerView.setAdapter(postAdapter);

        loadFeed();

    }

    private void loadFeed() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot i: snapshot.getChildren()) {
                    Post post = i.getValue(Post.class);
                    String a = i.child("isPost").getValue(String.class);
                    if(a.equals("t"))
                        postList.add(post);
                }
                Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
                progressCircular.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        super.onBackPressed();
    }


}