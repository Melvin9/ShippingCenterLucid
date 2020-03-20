package com.example.shippingcenterlucid.Login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.example.shippingcenterlucid.MainActivity.MainActivity;
import com.example.shippingcenterlucid.R;

import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {
EditText username,password;
Button login;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        username=findViewById(R.id.username);
        password=findViewById(R.id.pass);
        login=findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("f1")&&password.getText().toString().equals("f1")){ //sample data
                    startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                }
            }
        });

    }
}
