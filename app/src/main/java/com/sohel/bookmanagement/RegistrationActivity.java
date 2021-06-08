package com.sohel.bookmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sohel.bookmanagement.Model.Users;

public class RegistrationActivity extends AppCompatActivity {

    TextInputLayout reg_name_inputText,reg_email_inputText,reg_password_inputText,reg_phone_inputText;
    Button signUpButton;
    TextView reg_TextView_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        userRef=FirebaseDatabase.getInstance().getReference("users");

        Toast.makeText(this, "Here", Toast.LENGTH_SHORT).show();

        reg_name_inputText=findViewById(R.id.reg_name_id);
        reg_email_inputText=findViewById(R.id.reg_email_id);
        reg_password_inputText=findViewById(R.id.reg_password_id);
        reg_TextView_btn=findViewById(R.id.login_text_viewid);
        reg_phone_inputText=findViewById(R.id.reg_phone_Edit_ID);
        progressBar=findViewById(R.id.rprogressBar);

        signUpButton=findViewById(R.id.signbup_button_id);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegistration();
            }
        });

        reg_TextView_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                finish();
            }
        });



    }
    private void userRegistration() {

        String name=reg_name_inputText.getEditText().getText().toString().trim();
        String email=reg_email_inputText.getEditText().getText().toString();
        String password=reg_password_inputText.getEditText().getText().toString().trim();
        String phone=reg_phone_inputText.getEditText().getText().toString().trim();


        if (name.isEmpty())
        {
            reg_name_inputText.setError("Please Enter your name");
            reg_name_inputText.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            reg_email_inputText.setError("Please Enter your Email");
            reg_email_inputText.requestFocus();
            return;
        } if (phone.isEmpty())
        {
            reg_phone_inputText.setError("Please Enter your Phone Number");
            reg_phone_inputText.requestFocus();
            return;
        }if(phone.length()!=11){
            reg_phone_inputText.setError("Incorrect Phone Number");
            reg_phone_inputText.requestFocus();
            return;
        }
        else if (password.isEmpty())
        {
            reg_name_inputText.setError("Enter a password");
            reg_name_inputText.requestFocus();
            return;
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Users users = new Users(mAuth.getCurrentUser().getUid(),"none", name, email,phone, password);

                        userRef.child(mAuth.getCurrentUser().getUid())
                                .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegistrationActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast.makeText(RegistrationActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }else{
                        progressBar.setVisibility(View.GONE);
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegistrationActivity.this, "User is Already Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
    }
}
