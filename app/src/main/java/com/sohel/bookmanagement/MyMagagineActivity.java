package com.sohel.bookmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookmanagement.Adapter.BookCategoryAdapter;
import com.sohel.bookmanagement.Model.TimeDateModel;

import java.util.ArrayList;
import java.util.List;

public class MyMagagineActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<TimeDateModel> dataList=new ArrayList<>();
    private BookCategoryAdapter adapter;
    private ProgressDialog progressDialog;

    private DatabaseReference buyRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_magagine);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        mAuth=FirebaseAuth.getInstance();
        buyRef= FirebaseDatabase.getInstance().getReference().child("Buy");

        toolbar=findViewById(R.id.myMagazineAppBarId);
        setSupportActionBar(toolbar);
        this.setTitle("My Magazine");

        recyclerView=findViewById(R.id.myMagazineRecyclerViewid);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);




        adapter=new BookCategoryAdapter(this,dataList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListner(new BookCategoryAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position,int year) {
                TimeDateModel time=dataList.get(position);
                Intent intent=new Intent(MyMagagineActivity.this, BookListActivity.class);
                intent.putExtra("day",time.getDate());
                intent.putExtra("month",time.getMonth());
                intent.putExtra("year",time.getYear());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();
        buyRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            dataList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                TimeDateModel time=snapshot1.getValue(TimeDateModel.class);
                                dataList.add(time);
                                adapter.notifyDataSetChanged();
                            }
                            progressDialog.dismiss();
                        }else{
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}