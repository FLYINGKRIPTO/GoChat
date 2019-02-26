package com.example.gochat.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gochat.Model.User;
import com.example.gochat.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,viewGroup,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            viewHolder.profile_image.setImageResource(R.drawable.boy);

        }
        else{
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);


        }
    }


}
