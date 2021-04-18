package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout reg_name_inputText,reg_email_inputText,reg_password_inputText;
    Button signUpButton;
    TextView reg_TextView_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();


        reg_name_inputText=findViewById(R.id.reg_name_id);
        reg_email_inputText=findViewById(R.id.reg_email_id);
        reg_password_inputText=findViewById(R.id.reg_password_id);
        reg_TextView_btn=findViewById(R.id.register_textview_id);

        signUpButton=findViewById(R.id.signbup_button_id);




        signUpButton.setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.signbup_button_id:
                userRegistration();
                break;

        }

    }

    private void userRegistration() {

        String name=reg_name_inputText.getEditText().toString().trim();
        String email=reg_email_inputText.getEditText().toString().trim();
        String password=reg_password_inputText.getEditText().toString().trim();


        if (name.isEmpty())
        {
            reg_name_inputText.setError("Please Enter your name");
            reg_name_inputText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            reg_email_inputText.setError("Enter Valid Email address");
            reg_email_inputText.requestFocus();
            return;
        }

        if (password.isEmpty())
        {
            reg_name_inputText.setError("Enter a password");
            reg_name_inputText.requestFocus();
            return;
        }


        if (password.length()<6)
        {
            reg_password_inputText.setError("Minimum length of a password should be 6");
            reg_password_inputText.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(RegistrationActivity.this, "Alhamdulillah Registration is Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration is not Successful", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }
}