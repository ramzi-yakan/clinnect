package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAccountScreen extends AppCompatActivity {

    private static String tempPassword, tempUsername;
    private static Integer tempType;
    private static Map tempMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_screen);
    }

    //used for registering a user: if a user with specified username already exists, don't do anything
    public void checkAndRegisterUser(String username, String password, int type, Map metadata) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Query databaseQuery = database.child("users").orderByChild("username").equalTo(username);

        tempUsername=username;
        tempPassword=password;
        tempType=type;
        tempMetadata=metadata;

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot data: snapshot.getChildren()) {
                    user = data.getValue(User.class);
                }

                if (user == null) {
                    registerUser(tempUsername,tempPassword,tempType,tempMetadata);
                } else {
                    System.out.println("user with that username already exists");
                }

                tempUsername=null;
                tempPassword=null;
                tempType=null;
                tempMetadata=null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        };

        databaseQuery.addListenerForSingleValueEvent(postListener);
    }

    //dont use this!!!! this should only be called by checkAndRegisterUser
    private void registerUser(String username, String password, int type, Map metadata) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        //somewhere around here is where the password should be converted to SHA256

        User newUser = null;
        try {
            MessageDigest hashCreate = MessageDigest.getInstance("SHA-256");
            //Creates byte[] representation of hash.
            byte[] passHash = hashCreate.digest(password.getBytes());
            //Converts passHash into a String representation for password.
            String hashStringRep = new String(passHash);

            switch (type) {

                case MainActivity.PATIENT:
                    String id = database.child("users").push().getKey();
                    newUser = new Patient(username, hashStringRep,id);

                    break;

                case MainActivity.EMPLOYEE:
                    String id2 = database.child("users").push().getKey();
                    newUser = new Employee(username,
                            hashStringRep,
                            (String)metadata.get("address"),
                            (String)metadata.get("companyName"),
                            (String)metadata.get("phoneNumber"),id2);
                    ((Employee)newUser).licensed=(Boolean)metadata.get("licensed");
                    ((Employee)newUser).generalDesc=(String)metadata.get("generalDesc");
                    metadata.put("id",id2);
                    break;
            }

            database.child("users").child(newUser.id).setValue(newUser.toMap());


            login(newUser);
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm used for encryption.");
        }
    }

    private void login(User user) {
        System.out.println("successful login for "+user.username+" as "+user.type);

        MainActivity.currentUser = user;

        //change view to welcome
        Intent intent = new Intent(getApplicationContext(), AfterLoginScreen.class);
        startActivityForResult(intent,0);
    }


    public void onClickRegister(View view) {
        Integer type = null;
        HashMap<String, Object> metadata = new HashMap<>();

        if (((RadioButton)findViewById(R.id.radioPatient)).isChecked()) {
            type=MainActivity.PATIENT;
        } else if (((RadioButton)findViewById(R.id.radioEmployee)).isChecked()) {
            type = MainActivity.EMPLOYEE;
            boolean error=false;

            Geocoder geocoder = new Geocoder(this);
            try {
                if (geocoder.getFromLocationName(((EditText)findViewById(R.id.address)).getText().toString(),5).size()==0) {
                    ((EditText)findViewById(R.id.address)).setError("Could not find this location");
                    error=true;
                };
            } catch (Exception e) {
                ((EditText)findViewById(R.id.address)).setError("Error while trying to find this location");
                error=true;
            }

            if (((EditText)findViewById(R.id.companyName)).getText().length()==0) {
                ((EditText)findViewById(R.id.companyName)).setError("Cannot by empty");
                error=true;
            }
            if (((EditText)findViewById(R.id.phoneNumber)).getText().length()<10) {
                ((EditText)findViewById(R.id.phoneNumber)).setError("Invalid phone number");
                error=true;
            }

            if (error) {
                Toast.makeText(NewAccountScreen.this, "Please fill out all the necessary fields.", Toast.LENGTH_LONG).show();
                return;
            } else {
                metadata.put("address",((EditText)findViewById(R.id.address)).getText().toString());
                metadata.put("companyName",((EditText)findViewById(R.id.companyName)).getText().toString());
                metadata.put("phoneNumber",((EditText)findViewById(R.id.phoneNumber)).getText().toString());
                metadata.put("generalDesc",((EditText)findViewById(R.id.generalDesc)).getText().toString());
                metadata.put("licensed",((Switch)findViewById(R.id.licensed)).isChecked());
            }
        } else {
            Toast.makeText(NewAccountScreen.this, "Please specify whether you are a patient or an employee.", Toast.LENGTH_LONG).show();
            return;
        }

        checkAndRegisterUser(
                ((EditText) findViewById(R.id.usernameUser)).getText().toString(),
                ((EditText) findViewById(R.id.passwordUser)).getText().toString(),
                type,
                metadata
        );
    }
}
