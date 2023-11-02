package com.example.cpyipee;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    public Context context;
    public ArrayList<Comments> commentsArrayList =  new ArrayList<>();
    private FirebaseUser firebaseUser;
    private String userId;
    private String nameee;
    private int a, b;

    public CommentAdapter(Context context, ArrayList<Comments> commentsArrayList) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;
    }
    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_feed, parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        final Comments comments = commentsArrayList.get(position);
        holder.commentedText.setText(comments.getAnswer());
        holder.commentDate.setText(comments.getDate());
        Glide.with(context).load(comments.getImage()).transform(new CircleCrop()).into(holder.commenterDp);
        holder.Name.setText(comments.getName());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        isLiked(comments.getCommentId(), holder.upvote);
        isdisLiked(comments.getCommentId(), holder.downvote);

        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.upvote.getTag().equals("upvote") && holder.downvote.getTag().equals("downvote")){
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).setValue(true);
                }
                else if (holder.upvote.getTag().equals("upvote") && holder.downvote.getTag().equals("downvoted")){
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).setValue(true);

                }else {
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.downvote.getTag().equals("downvote") && holder.upvote.getTag().equals("upvote")){
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).setValue(true);
                }else if (holder.downvote.getTag().equals("downvote") && holder.upvote.getTag().equals("upvoted")){
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(comments.getCommentId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        getLikes(comments.getCommentId(), holder.votes);
        getdisLikes(comments.getCommentId(), holder.votes);




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

    private void isdisLiked(String commentId, ImageView downvote) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("downvotes").child(commentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    downvote.setImageResource(R.drawable.arrow_down_red);
                    downvote.setTag("downvoted");

                }
                else {
                    downvote.setImageResource(R.drawable.arrow_downward);
                    downvote.setTag("downvote");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLiked(String commentId, ImageView upvote) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("upvotes").child(commentId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    upvote.setImageResource(R.drawable.arrow_green_up);
                    upvote.setTag("upvoted");

                }
                else {

                    upvote.setImageResource(R.drawable.arrow_upward);
                    upvote.setTag("upvote");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView commenterDp;
        public TextView commentedText;
        public TextView commentDate;
        public TextView Name;
        public TextView votes;
        public ImageView upvote, downvote;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            commentDate = itemView.findViewById(R.id.time_id);
            commentedText = itemView.findViewById(R.id.commenter_name);
            commenterDp = itemView.findViewById(R.id.commenter_dp);
            Name = itemView.findViewById(R.id.name);
            votes = itemView.findViewById(R.id.comment_upvote_cnt_text);
            upvote = itemView.findViewById(R.id.comment_upvote_img);
            downvote = itemView.findViewById(R.id.comment_downvote_img);

        }
    }
}
