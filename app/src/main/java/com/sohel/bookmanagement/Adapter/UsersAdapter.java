package com.sohel.bookmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookmanagement.ChatingActivity;
import com.sohel.bookmanagement.Model.Agent;
import com.sohel.bookmanagement.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {


    Context context;
    List<Agent> userList;

    public UsersAdapter(Context context, List<Agent> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Agent user=userList.get(position);

        String senderId= FirebaseAuth.getInstance().getUid();
        String senderRoom=senderId+user.getuID();



        FirebaseDatabase.getInstance().getReference().child("chats")
                    .child(senderRoom)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String lastMessage=snapshot.child("lastMessage").getValue().toString();
                                String time=snapshot.child("time").getValue().toString();
                                holder.lastMsg.setText("Last Msg:"+lastMessage);
                            }else{
                                holder.lastMsg.setText("Tap To Chat");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        holder.userName.setText(user.getName());
        Picasso.get().load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.profile);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatingActivity.class);
               intent.putExtra("name",user.getName());
               intent.putExtra("image",user.getProfileImage());
               intent.putExtra("uid",user.getuID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        CircleImageView profile;
        TextView userName,lastMsg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profile=itemView.findViewById(R.id.profile);
            userName=itemView.findViewById(R.id.username);
            lastMsg=itemView.findViewById(R.id.lastMsg);

        }
    }
}
