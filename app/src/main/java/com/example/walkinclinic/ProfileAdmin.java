package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;



public class ProfileAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_admin);
    }

    public void onClickManageServices(View view){
        Intent intent = new Intent(getApplicationContext(), ManageServices.class);
        startActivityForResult(intent,0);
    }
}
