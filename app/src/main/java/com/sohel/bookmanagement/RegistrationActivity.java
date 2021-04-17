package com.sohel.bookmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class RegistrationActivity extends AppCompatActivity {

    TextInputLayout reg_name_inputText,reg_email_inputText,reg_password_inputText;
    Button signUpButton;
    TextView signup_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        reg_name_inputText=findViewById(R.id.reg_name_id);
        reg_email_inputText=findViewById(R.id.reg_email_id);
        reg_password_inputText=findViewById(R.id.reg_password_id);
        signup_textview=findViewById(R.id.signup_textview_id);


    }


}