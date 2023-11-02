package com.example.cpyipee;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter1 extends RecyclerView.Adapter<PostAdapter1.PostViewHolder> {

    List<Post1> postList;
    Context context;

    public PostAdapter1(Context context , List<Post1> posts){
        this.context = context;
        postList = posts;
    }
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item , parent , false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post1 post1 = postList.get(position);
        String link=post1.getUrl();
        String lnk =String.format("<a href=\"%s\">Register</a> ", link);
        holder.ulink.setText(Html.fromHtml(lnk));
        holder.ulink.setMovementMethod(LinkMovementMethod.getInstance());
        holder.title.setText(post1.getName());

        int hour,min,dur;
        dur=Integer.parseInt(post1.getDuration());
        hour=dur/3600;
        dur=dur%3600;
        min=dur/60;

        holder.duration.setText(hour+" hours "+min+" mins");

        String cf_time=post1.getStart_time().substring(11,13);
        Integer bd_time=Integer.parseInt(cf_time);
        bd_time=(bd_time+6)%24;
        String st = post1.getStart_time().substring(0,10)+" ";
        if(bd_time<10)
            st=st+"0";

        st=st+bd_time+post1.getStart_time().substring(13,19);

        holder.start.setText(st);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        TextView title , duration , start , ulink;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            duration = itemView.findViewById(R.id.title_tv);
            start = itemView.findViewById(R.id.id_tv);
            title = itemView.findViewById(R.id.user_id_tv);
            ulink = itemView.findViewById(R.id.body_tv);

        }
    }
}
