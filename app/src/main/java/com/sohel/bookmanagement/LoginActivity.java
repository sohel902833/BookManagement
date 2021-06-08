package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {


    TextInputLayout login_email_Input,login_password_Input;
    Button loginButton,registrationButton;
    private  ProgressBar progress;
    private FirebaseAuth mAuth;
    private ProgressDialog progressBar;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        login_email_Input=findViewById(R.id.login_email_id);
        login_password_Input=findViewById(R.id.login_password_id);
        loginButton=findViewById(R.id.login_button_id);
        registrationButton=findViewById(R.id.registration_button_id);
        progress=findViewById(R.id.lprogressBar);

        userRef= FirebaseDatabase.getInstance().getReference().child("users");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });


    }
    private void userLogin() {

        String email=login_email_Input.getEditText().getText().toString().trim();
        String password=login_password_Input.getEditText().getText().toString().trim();
        if(email.isEmpty()){
            login_email_Input.setError("Enter Email");
            login_email_Input.requestFocus();
            return;
        }

       else  if (password.isEmpty())
        {
            login_password_Input.setError("Enter a password");
            login_password_Input.requestFocus();
            return;
        }


        else if (password.length()<6)
        {
            login_password_Input.setError("Minimum length of a password should be 6");
            login_password_Input.requestFocus();
            return;
        }
        else {
            progress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progress.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        /*Intent intent = new Intent(LoginActivity.this, RequestTokenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();*/
                        sendUserToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Is Successful", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser=mAuth.getCurrentUser();
        if(mUser!=null){
           // onCheck();
            sendUserToMainActivity();
        }
    }

    public void senduserToTokenRequestActivity(){
        Intent intent=new Intent(LoginActivity.this,RequestTokenActivity.class);
        startActivity(intent);
        finish();
    }

    private void onCheck() {


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
                                }else{
                                    Toast.makeText(LoginActivity.this, "Token Timeout", Toast.LENGTH_SHORT).show();
                                    senduserToTokenRequestActivity();
                                }
                            }else{
                               senduserToTokenRequestActivity();
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


    private boolean isValid(long tokenTime){//true
        long currentTime=System.currentTimeMillis();
        boolean value=tokenTime>currentTime?true:false;
        return value;
    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}