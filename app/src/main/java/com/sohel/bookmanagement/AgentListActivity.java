package com.sohel.bookmanagement;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookmanagement.Adapter.BookCategoryAdapter;
import com.sohel.bookmanagement.Adapter.UsersAdapter;
import com.sohel.bookmanagement.Model.Agent;
import com.sohel.bookmanagement.Model.TimeDateModel;

import java.util.ArrayList;
import java.util.List;

public class AgentListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ShimmerRecyclerView recyclerView;
    private List<Agent> dataList=new ArrayList<>();

    private ProgressDialog progressDialog;
    private DatabaseReference agentRef;
    private FirebaseAuth mAuth;

    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_list);


        mAuth=FirebaseAuth.getInstance();
        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");

        toolbar=findViewById(R.id.agentList_AppBarId);
        setSupportActionBar(toolbar);
        this.setTitle("Chat List");

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.showShimmerAdapter();
        usersAdapter=new UsersAdapter(this,dataList);
        recyclerView.setAdapter(usersAdapter);


    }


    @Override
    protected void onStart() {
        super.onStart();
        agentRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            dataList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                Agent agent=snapshot1.getValue(Agent.class);
                                dataList.add(agent);
                                usersAdapter.notifyDataSetChanged();
                            }
                           recyclerView.hideShimmerAdapter();
                        }else{
                            recyclerView.hideShimmerAdapter();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

}