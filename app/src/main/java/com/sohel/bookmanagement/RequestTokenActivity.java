package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookmanagement.Model.AccessToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RequestTokenActivity extends AppCompatActivity {

    private EditText tokenEditText;
    private  Button activeButton;
    private Button requestKeyButton;
   private ProgressDialog progressBar;


    //Database Key

    private FirebaseAuth mAuth;
    private DatabaseReference requestRef,tokenRef,userRef;

    String token="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_token);

        progressBar=new ProgressDialog(this);

        tokenEditText=findViewById(R.id.accessTokenEditText);
        activeButton=findViewById(R.id.activeAccountButtonid);
        requestKeyButton=findViewById(R.id.requestKeyButton);

        mAuth=FirebaseAuth.getInstance();
        requestRef=FirebaseDatabase.getInstance().getReference().child("KeyRequest");

        tokenRef= FirebaseDatabase.getInstance().getReference().child("Tokens");
        userRef= FirebaseDatabase.getInstance().getReference().child("users");


        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 token=tokenEditText.getText().toString().trim();

                if(token.isEmpty()){
                    tokenEditText.setError("Please Put Your Token First.");
                    tokenEditText.requestFocus();
                }else{
                   // checkToken2();
                }


            }
        });



        requestKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessages("Hello Dear,I need an Activation Key,For Upgrade My Account.");
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setMessage("Loading....");
        progressBar.show();
        userRef.child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(snapshot.hasChild("upgrade")){
                                long time=snapshot.child("upgrade").child("time").getValue(Long.class);
                                boolean d=isValid(time);
                                if(d){
                                    progressBar.show();
                                    sendUserToMainActivity();
                                }
                            }else{
                                progressBar.dismiss();
                            }
                        }else{
                            progressBar.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

 /*   private void checkToken2() {
        progressBar.setMessage("Checking Your Token");
        progressBar.setTitle("Please wait");
        progressBar.show();
            tokenRef.child(token).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        AccessToken accessToken=snapshot.getValue(AccessToken.class);
                        if(accessToken.getUserId().equals("null") && !accessToken.isSealed()){
                            upgradeAccount(accessToken.getTime());

                        }else{
                            progressBar.dismiss();
                            Toast.makeText(RequestTokenActivity.this, "This Token Already has been Used.", Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        progressBar.dismiss();
                        Toast.makeText(RequestTokenActivity.this, "Token Not Found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }
*/
    private void upgradeAccount(long time) {

        progressBar.setMessage("Upgrading Account");
        progressBar.setTitle("Please wait");
        progressBar.show();

        HashMap<String,Object> updateToken=new HashMap<>();
        updateToken.put("sealed",true);
        updateToken.put("userId",mAuth.getCurrentUser().getUid());

        tokenRef.child(token)
                .updateChildren(updateToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            HashMap<String,Object> accountUpgradeMap=new HashMap<>();
                            accountUpgradeMap.put("token",token);
                            accountUpgradeMap.put("time",time);


                            userRef.child(mAuth.getCurrentUser().getUid())
                                    .child("upgrade")
                                    .updateChildren(accountUpgradeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.dismiss();
                                        Toast.makeText(RequestTokenActivity.this, "Account Upgrade Success.", Toast.LENGTH_SHORT).show();
                                        sendUserToMainActivity();
                                    }else{
                                        progressBar.dismiss();
                                        Toast.makeText(RequestTokenActivity.this, "Upgrade Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });




    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(RequestTokenActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    private boolean isValid(long tokenTime){//true
        long currentTime=System.currentTimeMillis();
        boolean value=tokenTime>currentTime?true:false;
        return value;
    }
    private void sendMessages(String messageText) {

       Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd,yyy");
        String currentdate=simpleDateFormat.format(calendar.getTime());



        SimpleDateFormat  simpleDateFormat2=new SimpleDateFormat("hh:mm a");
        String currenttime=simpleDateFormat2.format(calendar.getTime());


        String messageKey=requestRef.push().getKey()+System.currentTimeMillis();


        HashMap<String,Object> messageTextBody=new HashMap();
        messageTextBody.put("message",messageText);
        messageTextBody.put("from",mAuth.getCurrentUser().getUid());
        messageTextBody.put("type","text");
       messageTextBody.put("messageId",messageKey);
        messageTextBody.put("time",currenttime);
        messageTextBody.put("date",currentdate);


        requestRef.child(mAuth.getCurrentUser().getUid())
                .child(messageKey)
                .updateChildren(messageTextBody)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                           Toast.makeText(RequestTokenActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RequestTokenActivity.this,ChatingActivity.class));
                        }
                    }
                });







    }
}