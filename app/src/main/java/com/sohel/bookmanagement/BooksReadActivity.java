package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsibbold.zoomage.ZoomageView;
import com.sohel.bookmanagement.Adapter.ReadBookAdapter;
import com.sohel.bookmanagement.Model.ImageModel2;
import com.sohel.bookmanagement.Model.TimeDateModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BooksReadActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ImageModel2> imageDataList = new ArrayList<>();

    private Toolbar toolbar;
    private ProgressDialog progressBar;

    String bookId, bookName;

    private DatabaseReference bookRef;
    private ReadBookAdapter readBookAdapter;
    int day,month,year;
    private String currentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_read);

        day=getIntent().getIntExtra("day",0);
        bookId=getIntent().getStringExtra("bookId");
        currentId=getIntent().getStringExtra("currentId");
        bookName=getIntent().getStringExtra("bookName");
        month=getIntent().getIntExtra("month",0);
        year=getIntent().getIntExtra("year",0);


        toolbar = findViewById(R.id.bookRead_AppBarid);
        setSupportActionBar(toolbar);
        this.setTitle(bookName);

        bookRef = FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");

        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();


        recyclerView = findViewById(R.id.readBookRecyclerViewId);


        setRecyclerViewLayout("grid");

        readBookAdapter.setOnItemClickListner(new ReadBookAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                ImageModel2 imageModel2=imageDataList.get(position);
                showViewDialoug(imageModel2);
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        bookRef.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        imageDataList.clear();
                        if(snapshot.exists()){
                            if(snapshot.hasChild("imageList")){
                                for(DataSnapshot snapshot1:snapshot.child("imageList").getChildren()){
                                    String imageUrl=snapshot1.child("imageUrl").getValue().toString();
                                    String id=snapshot1.getKey();
                                    ImageModel2 img=new ImageModel2(imageUrl,id);
                                    imageDataList.add(img);
                                    readBookAdapter.notifyDataSetChanged();
                                }
                                progressBar.dismiss();
                            }else{
                                progressBar.dismiss();
                            }
                        }else{
                            progressBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.dismiss();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.menu_list_item){
            setRecyclerViewLayout("list");
        }
        if(item.getItemId()==R.id.menu_grid_item){
            setRecyclerViewLayout("grid");
        }

        return super.onOptionsItemSelected(item);



    }

    private void setRecyclerViewLayout(String type) {
            if(type.equals("grid")){
                recyclerView.setHasFixedSize(true);
                GridLayoutManager layoutManager=new GridLayoutManager(this,2);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                readBookAdapter = new ReadBookAdapter(this, imageDataList,type);
                recyclerView.setAdapter(readBookAdapter);

            }else if(type.equals("list")){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            readBookAdapter = new ReadBookAdapter(this, imageDataList,type);
            recyclerView.setAdapter(readBookAdapter);



            }




    }


    private void showViewDialoug(ImageModel2 imageModel) {
        AlertDialog.Builder builder=new AlertDialog.Builder(BooksReadActivity.this);
        View view=getLayoutInflater().inflate(R.layout.view_dialog_layout,null);
        builder.setView(view);

        ImageButton closeButton=view.findViewById(R.id.closeIconId);
        ZoomageView imageView=view.findViewById(R.id.viewImageViewid);

        Picasso.get().load(imageModel.getImageUrl()).placeholder(R.drawable.select_image).into(imageView);

        final AlertDialog dialog=builder.create();
        dialog.show();


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });
    }


}

