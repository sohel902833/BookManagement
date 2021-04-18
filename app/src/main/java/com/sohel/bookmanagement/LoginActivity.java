package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputLayout login_email_Input,login_password_Input;
    Button loginButton,registrationButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_email_Input=findViewById(R.id.login_email_id);
        login_password_Input=findViewById(R.id.login_password_id);

        loginButton=findViewById(R.id.login_button_id);
        registrationButton=findViewById(R.id.registration_button_id);

        registrationButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);




    }





    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.login_button_id:
                        userLogin();
                break;

            case R.id.registration_button_id:
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
                break;
        }

    }

    private void userLogin() {

        String email=login_email_Input.getEditText().toString().trim();
        String password=login_password_Input.getEditText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            login_email_Input.setError("Enter Valid Email address");
            login_email_Input.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            login_password_Input.setError("Enter a password");
            login_password_Input.requestFocus();
            return;
        }


        if (password.length()<6)
        {
            login_password_Input.setError("Minimum length of a password should be 6");
            login_password_Input.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    finish();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Login Is Successful", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}