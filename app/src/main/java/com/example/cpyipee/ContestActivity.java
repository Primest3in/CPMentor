package com.example.cpyipee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    com.codingstuff.retrofitfirst.JSONPlaceholder jsonPlaceholder;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        toolbar = findViewById(R.id.contest_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upcoming contests");

        recyclerView = findViewById(R.id.contest_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://kontests.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceholder = retrofit.create(com.codingstuff.retrofitfirst.JSONPlaceholder.class);

        getPost();

    }
    public void getPost(){
        Call<List<Post1>> call = jsonPlaceholder.getPost();
        call.enqueue(new Callback<List<Post1>>() {
            @Override
            public void onResponse(Call<List<Post1>> call, Response<List<Post1>> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(ContestActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Post1> postList = response.body();
                PostAdapter1 postAdapter1 = new PostAdapter1(ContestActivity.this , postList);
                recyclerView.setAdapter(postAdapter1);
            }

            @Override
            public void onFailure(Call<List<Post1>> call, Throwable t) {

                Toast.makeText(ContestActivity.this, t.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

}