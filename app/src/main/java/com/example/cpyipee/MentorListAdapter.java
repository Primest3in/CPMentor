package com.example.cpyipee;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

public class MentorListAdapter extends RecyclerView.Adapter<MentorListAdapter.MyViewHolder> {
    public Context context;
    public ArrayList<Mentors> mentorsArrayList = new ArrayList<>();

    public MentorListAdapter(Context context, ArrayList<Mentors> mentors) {
        this.context = context;
        this.mentorsArrayList = mentors;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mentor_feed, parent, false);
        return new MentorListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorListAdapter.MyViewHolder holder, int position) {
        final Mentors mentors = mentorsArrayList.get(position);

        holder.name.setText(mentors.getFullName());
        holder.email.setText(mentors.getEmail());
        holder.handle.setText("CF: "+ mentors.getUserName());
        Glide.with(context).load(mentors.getDownloadUrl()).transform(new CircleCrop()).into(holder.img);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowBlog.class);
                intent.putExtra("key", mentors.getUserID());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mentorsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, email, handle;
        public ImageView img;
        LinearLayout layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.mentor_name);
            email = itemView.findViewById(R.id.mentor_email);
            handle = itemView.findViewById(R.id.cf_handle);
            img = itemView.findViewById(R.id.mentor_img);
            layout = itemView.findViewById(R.id.Lout);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
