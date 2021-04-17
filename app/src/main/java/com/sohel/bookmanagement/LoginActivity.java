package com.sohel.bookmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    TextInputLayout login_email_Input,login_password_Input;

    Button loginButton,registrationButton;


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
        if (v.getId()==R.id.login_button_id)
        {

        }

        if (v.getId()==R.id.registration_button_id)
        {
            Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
            startActivity(intent);
        }
    }
}