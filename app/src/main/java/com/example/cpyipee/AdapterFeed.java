package com.example.cpyipee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder>{

    public Context context;
    long a = 0, b = 0;
    int cmntCount = 0;
    public ArrayList<Post> postArrayList =  new ArrayList<>();
    private  FirebaseUser firebaseUser;

    public AdapterFeed(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.postArrayList = posts;


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed, parent, false);
        return new AdapterFeed.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Post post = postArrayList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.postedByName.setText(post.getAskedBy());
        holder.postedTopic.setText(post.getTopic());
        holder.postDate.setText(post.getDate());
        holder.postedText.setText(post.getPostTexts());
        holder.postedByImage.setVisibility(View.VISIBLE);
        Glide.with(context).load(post.getWhoImg()).transform(new CircleCrop()).into(holder.postedByImage);

        if(post.getDownloadUrl() == null) {
            holder.postedImage.setVisibility(View.GONE);
        }
        else{
            holder.postedImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getDownloadUrl()).into(holder.postedImage);

        }
        isLiked(post.getPostId(), holder.upImage, holder.upTv);
        isdisLiked(post.getPostId(), holder.downImage, holder.downTv);


        holder.up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.upTv.getTag().equals("upvote") && holder.downTv.getTag().equals("downvote")){
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                }
                else if (holder.upTv.getTag().equals("upvote") && holder.downTv.getTag().equals("downvoted")){
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);

                }else {
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.downTv.getTag().equals("downvote") && holder.upTv.getTag().equals("upvote")){
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                }else if (holder.downTv.getTag().equals("downvote") && holder.upTv.getTag().equals("upvoted")){
                    FirebaseDatabase.getInstance().getReference().child("upvotes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("downvotes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        getLikes(post.getPostId(), holder.upCntTv);
        getdisLikes(post.getPostId(), holder.upCntTv);
        getcommentCount(post.getPostId(), holder.cmntCntTv);


        holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, showCommentsActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("publisher", post.getPublisher());
                intent.putExtra("comments", holder.cmntCntTv.getText().toString());

                context.startActivity(intent);

            }


        });




    }

    private void getcommentCount(String postId, TextView cmntCntTv) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cmntCount = (int) snapshot.getChildrenCount();
                cmntCntTv.setText(cmntCount + " Answers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return postArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView postedImage, upImage, downImage, commentImage;
        public TextView postedByName, postedTopic, postDate, postedText, upTv, downTv, commentTv, upCntTv, cmntCntTv;
        public ImageView postedByImage;
        public RelativeLayout up, down, answer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            postedImage = (ImageView) itemView.findViewById(R.id.posted_Img_id);
            postedByImage = (ImageView)  itemView.findViewById(R.id.proImg_id);
            upImage = (ImageView) itemView.findViewById(R.id.up_img_id);
            downImage = (ImageView) itemView.findViewById(R.id.down_img_id);
            commentImage = (ImageView) itemView.findViewById(R.id.cmnt_img_id);

            postedByName = (TextView) itemView.findViewById(R.id.tv_name_id);
            postedTopic = (TextView) itemView.findViewById(R.id.posted_topic_id);
            postDate = (TextView) itemView.findViewById(R.id.posted_date_id);
            postedText = (TextView) itemView.findViewById(R.id.tvStatus);
            upTv = (TextView) itemView.findViewById(R.id.upVoteTv_id);
            downTv = (TextView) itemView.findViewById(R.id.downVoteTv_id);
            commentTv = (TextView) itemView.findViewById(R.id.commentTv_id);
            upCntTv = (TextView) itemView.findViewById(R.id.tv_upvote_cnt_id);
            cmntCntTv = (TextView) itemView.findViewById(R.id.tv_comment_cnt_id);
            up = itemView.findViewById(R.id.upvote_layout_id);
            down = itemView.findViewById(R.id.downvote_layout_id);
            answer = itemView.findViewById(R.id.answer_layout_id);

        }
    }
    private void isLiked(String postId, ImageView imageView, TextView textView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("upvotes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.arrow_green_up);
                    imageView.setTag("upvoted");
                    textView.setText("upvoted");
                    textView.setTag("upvoted");
                }
                else {

                    imageView.setImageResource(R.drawable.arrow_upward);
                    imageView.setTag("upvote");
                    textView.setTag("upvote");
                    textView.setText("upvote");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void isdisLiked(String postId, ImageView imageView, TextView textView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("downvotes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.arrow_down_red);
                    imageView.setTag("downvoted");
                    textView.setTag("downvoted");
                    textView.setText("downvoted");
                }
                else {
                    imageView.setImageResource(R.drawable.arrow_downward);
                    imageView.setTag("downvote");
                    textView.setTag("downvote");
                    textView.setText("downvote");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void getLikes(String postId, TextView textView) {
        long[] cnt = {0};
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("upvotes").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                a = (int) snapshot.getChildrenCount();
                if((a - b) >= 0) {
                    textView.setTextColor(Color.parseColor("#00FF0C"));
                    textView.setText("+" + (a - b));
                }
                else {
                    textView.setTextColor(Color.parseColor("#FFFF0000"));
                    textView.setText("-" + (a - b));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void getdisLikes(String postId, TextView textView) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("downvotes").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b = (int) snapshot.getChildrenCount();
                if((a - b) >= 0) {
                    textView.setTextColor(Color.parseColor("#00FF0C"));
                    textView.setText("+" + (a - b));
                }
                else {
                    textView.setTextColor(Color.parseColor("#FFFF0000"));
                    textView.setText("-" + (a - b));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
