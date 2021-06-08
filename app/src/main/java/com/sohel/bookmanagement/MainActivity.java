
package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookmanagement.Adapter.BookCategoryAdapter;
import com.sohel.bookmanagement.Model.BookCategory;
import com.sohel.bookmanagement.Model.BookModel;
import com.sohel.bookmanagement.Model.TimeDateModel;
import com.sohel.bookmanagement.Model.Users;
import com.sohel.bookmanagement.Services.TokenService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private ProgressDialog progressBar;

    private  BookCategoryAdapter categoryAdapter;

    private List<BookCategory> bookCategoryList=new ArrayList<>();
    private DatabaseReference bookRef,userRef,requestRef,buyRef;

    private List<TimeDateModel> dataList=new ArrayList<>();
    private Spinner yearSelectSpinner;
    ArrayAdapter spinnerAdapter;
    boolean oneTime;
    private Button searchButton;

    TokenService tokenService;
    private List<TimeDateModel> buyMagagineList=new ArrayList<>();
    private List<TimeDateModel> yearsList=new ArrayList<>();

    private CircleImageView profileImage;
    private TextView nameTextView;
    int cYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        Calendar calendar = Calendar.getInstance();
        cYear = calendar.get(Calendar.YEAR);

        bookRef=FirebaseDatabase.getInstance().getReference().child("Admin").child("Books");
        userRef=FirebaseDatabase.getInstance().getReference().child("users");
        requestRef=FirebaseDatabase.getInstance().getReference().child("KeyRequest");
        buyRef= FirebaseDatabase.getInstance().getReference().child("Buy");




        tokenService=new TokenService(this);
        progressBar=new ProgressDialog(this);
        progressBar.setMessage("Loading");
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();



        mToolbar=findViewById(R.id.main_page_Toolbar);
        recyclerView=findViewById(R.id.categoryList);
        yearSelectSpinner=findViewById(R.id.yearSelectId);
        searchButton=findViewById(R.id.searchButton);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Magazine");

        drawerLayout=findViewById(R.id.drawerlayout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);




        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView=findViewById(R.id.navigation_view);


        View navView=navigationView.inflateHeaderView(R.layout.header_layout);

        profileImage=navView.findViewById(R.id.nav_profileImage);
        nameTextView=navView.findViewById(R.id.nav_UserName);

        loadUserData();





        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        setRecyclerView(cYear);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem=yearSelectSpinner.getSelectedItem().toString();
                setRecyclerView(Integer.parseInt(selectedItem));

            }
        });


    }

    public void setRecyclerView(int year){
        categoryAdapter=new BookCategoryAdapter(this,dataList,year);
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        categoryAdapter.setOnItemClickListner(new BookCategoryAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position,int year) {

                TimeDateModel time=dataList.get(position);

                if(isBuy(time,year)){
                    Intent intent=new Intent(MainActivity.this, BookListActivity.class);
                    intent.putExtra("day",time.getDate());
                    intent.putExtra("month",time.getMonth());
                    intent.putExtra("year",year);
                    startActivity(intent);
                }else{
                    showRequestDiolouge(time,year);
                }
            }
        });

    }

    public void loadUserData(){
        userRef.child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Users user=snapshot.getValue(Users.class);
                            String img=snapshot.child("image").getValue().toString();

                            Picasso.get().load(img).placeholder(R.drawable.profile).into(profileImage);
                            nameTextView.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        prepareData();
        loadData();
        buyRef.child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            buyMagagineList.clear();
                            for(DataSnapshot snapshot1:snapshot.getChildren()){
                                TimeDateModel timeDateModel=snapshot1.getValue(TimeDateModel.class);
                                buyMagagineList.add(timeDateModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void  prepareData(){
        dataList.clear();

        for(int i=1; i<=12; i++){
            TimeDateModel time=new TimeDateModel(i,16);
            dataList.add(time);
        }
        for(int i=2; i<=12; i++){
            TimeDateModel time=new TimeDateModel(i,1);
            dataList.add(time);
        }

        TimeDateModel time2=new TimeDateModel(12,30);
        dataList.add(time2);
        categoryAdapter.notifyDataSetChanged();


    }

    private boolean isBuy(TimeDateModel currentTime,int year){
            boolean isBuy=false;
            for(int i=0; i<buyMagagineList.size(); i++){
                TimeDateModel model=buyMagagineList.get(i);
                if(model.getDate()==currentTime.getDate() && model.getMonth()==currentTime.getMonth() && model.getYear()==year){
                    isBuy=true;
                    break;
                }
            }
            return  isBuy;
    }

    private void loadData() {
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    progressBar.dismiss();
                    yearsList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()) {
                        BookModel book = snapshot1.getValue(BookModel.class);
                        TimeDateModel time = book.getTime();
                        if (!checkOvverride(time)) {
                            yearsList.add(time);
                        }
                    }
                    setSpinnerData();
                }else{
                    setSpinnerData();
                    progressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setSpinnerData(){
        ArrayList<String> uniqueYears=new ArrayList<>();
        for(int i=1950; i<=2050; i++){
            String year=String.valueOf(i);
            uniqueYears.add(year);
        }

       /* for(int i=0; i<yearsList.size(); i++){
            int currentYear=yearsList.get(i).getYear();
            uniqueYears.add(String.valueOf(currentYear));
        }
        if(uniqueYears.size()==0){
            uniqueYears.add(String.valueOf(cYear));
        }*/
        spinnerAdapter=new ArrayAdapter(MainActivity.this,R.layout.item_layout,R.id.spinnerHeaderTExt,uniqueYears);
        yearSelectSpinner.setAdapter(spinnerAdapter);
        yearSelectSpinner.setSelection(71);

    }
    public boolean checkOvverride(TimeDateModel time){
        boolean isNone=false;
        for(int i=0; i<dataList.size(); i++){
            TimeDateModel t=dataList.get(i);
            if(t.getDate()==time.getDate() && t.getYear()==time.getYear()){
                isNone=true;
                break;
            }
        }
        return isNone;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return  true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_profile: {
                sendUserToProfileActivity();
                break;
            }
            case R.id.nav_Home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_books: {
                sendUserToMYMagazineActivity();
                break;
            }
            case R.id.nav_liveSupport: {
                sendUserToAgentListActivity();
                break;
            }
            case R.id.nav_logut:{
                mAuth.signOut();
                sendUserToLoginActivity();
                break;
            }


        }

    }

    private void sendUserToProfileActivity() {
        startActivity(new Intent(MainActivity.this,ProfileActivity.class));

    }

    private void sendUserToAgentListActivity() {
        startActivity(new Intent(MainActivity.this,AgentListActivity.class));

    }

    private void sendUserToMYMagazineActivity() {
        startActivity(new Intent(MainActivity.this,MyMagagineActivity.class));
    }

    private void sendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showRequestDiolouge(TimeDateModel time,int year) {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.access_key_diolouge,null);
        builder.setView(view);

         EditText tokenEt=view.findViewById(R.id.tokenEditText);
        Button activeMagazine=view.findViewById(R.id.activeMaazineButton);
        Button requestTokenButton=view.findViewById(R.id.requestTokenId);


        final AlertDialog dialog=builder.create();
        dialog.show();


        requestTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForToken(dialog);
            }
        });

        activeMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token=tokenEt.getText().toString();

                if(token.isEmpty()){
                    tokenEt.setError("Please Write Your Token");
                    tokenEt.requestFocus();
                }else{
                   tokenService.checkToken(token,time,dialog,year);
                }
            }
        });



    }

    private void requestForToken(AlertDialog dialog) {
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());



        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",mAuth.getCurrentUser().getUid());
        hashMap.put("date",currentdate);
        hashMap.put("time",currenttime);



        requestRef.child(mAuth.getCurrentUser().getUid())
                .setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Request Sent Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}