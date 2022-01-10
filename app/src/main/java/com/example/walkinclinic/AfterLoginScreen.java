package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AfterLoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login_screen);

        ((TextView)findViewById(R.id.welcomeText)).setText("Welcome "+MainActivity.currentUser.username+"!");
        ((TextView)findViewById(R.id.loggedInAsText)).setText("You are logged in as: "+MainActivity.typeString(MainActivity.currentUser.type).toLowerCase()+".");
    }

    public void onClickContinue(View view){
        if (MainActivity.currentUser.type == MainActivity.ADMIN){
            Intent intent = new Intent(getApplicationContext(), ProfileAdmin.class);
            startActivityForResult(intent,0);
        } else if (MainActivity.currentUser.type == MainActivity.EMPLOYEE){
            Intent intent = new Intent(getApplicationContext(), ProfileEmployee.class);
            startActivityForResult(intent,0);
        }
    }
}
