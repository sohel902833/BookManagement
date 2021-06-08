package com.sohel.bookmanagement.Services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.renderscript.Sampler;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sohel.bookmanagement.Model.AccessToken;
import com.sohel.bookmanagement.Model.TimeDateModel;

import java.sql.Time;
import java.util.HashMap;

public class TokenService {
    Activity activity;
    private DatabaseReference tokenRef,agentRef,userRef,buyRef,coinRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    AlertDialog dialog;
    public TokenService(Activity activity) {
        this.activity = activity;
        buyRef= FirebaseDatabase.getInstance().getReference().child("Buy");
        coinRef= FirebaseDatabase.getInstance().getReference().child("AgentCoins");
        tokenRef= FirebaseDatabase.getInstance().getReference().child("Tokens");
        userRef= FirebaseDatabase.getInstance().getReference().child("users");
        agentRef= FirebaseDatabase.getInstance().getReference().child("Admin").child("AgentList");
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage("Checking Token");
        progressDialog.setTitle("Please Wait");
    }
    public void checkToken(String token, TimeDateModel time, AlertDialog dialog,int year){
        this.dialog=dialog;
        progressDialog.show();
        tokenRef.child(token)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            AccessToken token=snapshot.getValue(AccessToken.class);
                            if(token.getUserId().equals(mAuth.getCurrentUser().getUid())){
                                if(!token.isSealed()){
                                    checkAgent2(token,time,year);
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(activity, "You Already Used This Token", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(activity, "This Token is not for you", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Token Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }


    private void checkAgent(AccessToken token) {
        agentRef.child(token.getAgentId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                           int prevCoin= snapshot.child("coin").getValue(Integer.class);
                           removeCoin(token.getAgentId(),prevCoin,token.getToken());
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Errro: ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void checkAgent2(AccessToken token,TimeDateModel time,int year) {
        coinRef.child(token.getAgentId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                           int prevCoin= snapshot.child("coins").getValue(Integer.class);
                            setTokenToProfile(token,time,prevCoin,year);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Sorry Your Agent Don't Have Enough Coin.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void setTokenToProfile(AccessToken token, TimeDateModel time,int prevCoin,int year){

        TimeDateModel time2=new TimeDateModel(year,time.getMonth(),time.getDate());

        buyRef.child(mAuth.getCurrentUser().getUid())
                .child(String.valueOf(System.currentTimeMillis()))
                .setValue(time2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            removeCoin(token.getAgentId(),prevCoin,token.getToken());
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void removeCoin(String agentId,int prevCoin, String token) {

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("coins",prevCoin-10);

        coinRef.child(agentId)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            updateToken(token);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateToken(String token) {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sealed",true);
        tokenRef.child(token)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(activity, "Magagine Buy Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "Magagine Buy Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
