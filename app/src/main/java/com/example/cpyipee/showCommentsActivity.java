package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class showCommentsActivity extends AppCompatActivity {

    private ImageView commentDp, sendComment;
    private EditText writeComment;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userId;
    private DatabaseReference ref;
    private Toolbar toolbar;
    private String answerText;
    private String postId;
    private RecyclerView recyclerView;
    private ArrayList<Comments> commentsArrayList;
    private CommentAdapter commentAdapter;
    String url;
    String name;

    private ImageView proImage;
    private TextView nameText;
    private TextView topicText;
    private TextView dateText;
    private TextView postText;
    private TextView PostUpCnt;
    private int a, b; private String commentCount;
    private TextView ansCnt1, ansCnt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_comments);

        toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Answers");

        postId = getIntent().getStringExtra("postId");
        commentCount = getIntent().getStringExtra("comments");
        PostUpCnt = findViewById(R.id.up_cntOnPost_comment);
        ansCnt1 = findViewById(R.id.answer_cnt_00);
        ansCnt2 = findViewById(R.id.answer_cnt_11);
        proImage = findViewById(R.id.proImg_id);
        nameText = findViewById(R.id.tv_name_id);
        topicText = findViewById(R.id.posted_topic_id);
        dateText = findViewById(R.id.posted_date_id);
        postText = findViewById(R.id.tvStatus);

        commentDp = findViewById(R.id.comment_dp);
        recyclerView = findViewById(R.id.comment_list);
        sendComment = findViewById(R.id.send_image_id);
        writeComment = findViewById(R.id.comment_editText);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();
        ansCnt1.setText(commentCount);
        ansCnt2.setText("Answers");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        ref = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                url = snapshot.child("DownloadUrl").getValue(String.class);
                name = snapshot.child("FullName").getValue(String.class);

                Glide.with(showCommentsActivity.this).load(url).transform(new CircleCrop()).into(commentDp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        populatePost();

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerText = writeComment.getText().toString();
                if(answerText.length() != 0) {
                    write();
                    writeComment.setText("");
                }
                else {
                    writeComment.setError("write your answer first");
                }
            }
        });
        commentsArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(showCommentsActivity.this, commentsArrayList);
        recyclerView.setAdapter(commentAdapter);
        loadComments();
        getLikes(postId, PostUpCnt);
        getdisLikes(postId, PostUpCnt);






    }
    private void getLikes(String commentId, TextView votes) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("upvotes").child(commentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                a = (int) snapshot.getChildrenCount();
                if((a - b) >= 0) {
                    votes.setTextColor(Color.parseColor("#00FF0C"));
                    votes.setText(" +" + (a - b));
                }
                else {
                    votes.setTextColor(Color.parseColor("#FFFF0000"));
                    votes.setText(" -" + (a - b));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void getdisLikes(String commentId, TextView votes) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("downvotes").child(commentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b = (int) snapshot.getChildrenCount();
                if((a - b) >= 0) {
                    votes.setTextColor(Color.parseColor("#00FF0C"));
                    votes.setText("+" + (a - b));
                }
                else {
                    votes.setTextColor(Color.parseColor("#FFFF0000"));
                    votes.setText("-" + (a - b));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populatePost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                nameText.setText(post.getAskedBy());
                dateText.setText(post.getDate());
                topicText.setText(post.getTopic());
                postText.setText(post.getPostTexts());
                Glide.with(showCommentsActivity.this).load(post.getWhoImg()).transform(new CircleCrop()).into(proImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(showCommentsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments");
        commentRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsArrayList.clear();
                for(DataSnapshot i: snapshot.getChildren()) {
                    Comments comments = i.getValue(Comments.class);
                    commentsArrayList.add(comments);

                }

                commentAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(showCommentsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void write() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        String newId = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", userId);
        hashMap.put("Answer", answerText);
        hashMap.put("commentId", newId);
        hashMap.put("Date", getDate() );
        hashMap.put("Image", url);
        hashMap.put("Name", name);
        reference.child(newId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(showCommentsActivity.this, "your answer posted successfully", Toast.LENGTH_SHORT).show();
                    commentAdapter.notifyDataSetChanged();

                }
                else {
                    Toast.makeText(showCommentsActivity.this, "failed to post" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    String getDate() {
        return DateFormat.getDateInstance().format(new Date());
    }

}