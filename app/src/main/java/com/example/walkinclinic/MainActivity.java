package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    final static int ADMIN=0, PATIENT=1, EMPLOYEE=2; //using constants instead of enums, easier to store in firebase

    private static String tempPassword;

    public static User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //used for logging in: if the username does not exist/password is incorrect, don't do anything
    public void authenticateUser(String username, String password) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();



        Query databaseQuery = database.child("users").orderByChild("username").equalTo(username);

        tempPassword=password;

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot data: snapshot.getChildren()) {
                    long type = (long)data.child("type").getValue();
                    System.out.println();
                    switch ((int)type) {
                        case ADMIN:
                            user = data.getValue(Admin.class);
                            user.id = (String)data.child("id").getValue();
                            break;
                        case PATIENT:
                            user = data.getValue(Patient.class);
                            user.id = (String)data.child("id").getValue();
                            break;
                        case EMPLOYEE:
                            user = data.getValue(Employee.class);
                            user.id = (String)data.child("id").getValue();
                            break;
                    }
                }

                if (user == null) {
                    Toast.makeText(MainActivity.this, "That user does not seem to exist. Please try again!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        //Created so we can create a hash of tempPassword.
                        MessageDigest hashCreate = MessageDigest.getInstance("SHA-256");
                        //Creates byte[] representation of hash for tempPassword.
                        byte[] passHash = hashCreate.digest(tempPassword.getBytes());
                        //Converts passHash into a String so it can be compared with the stored hash.
                        String tempPassword = new String(passHash);
                        if (user.password.equals(tempPassword)) { //also might need to change this in order to work with SHA256 passwords
                            login(user);
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect password. Please try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (NoSuchAlgorithmException e) {
                        System.out.println("Invalid algorithm used for encryption.");
                    }
                }

                tempPassword=null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        };

        databaseQuery.addListenerForSingleValueEvent(postListener);
    }

    private void login(User user) {
        System.out.println("successful login for "+user.username+" as "+user.type);

        currentUser = user;
        System.out.println(currentUser.id);

        //change view to profile/welcome
        if (user.type==ADMIN) {
            Intent intent = new Intent(getApplicationContext(), ProfileAdmin.class);
            intent.putExtra("user",currentUser.id);
            startActivityForResult(intent, 0);
        } else {
            Intent intent = new Intent(getApplicationContext(), AfterLoginScreen.class);
            intent.putExtra("user",user.id);
            startActivityForResult(intent, 0);
        }
    }

    public static String getCurrentMemberId(){
        return currentUser.id;
    }


    public void onClickLogin(View view) {
        authenticateUser(
                ((EditText) findViewById(R.id.usernameBox)).getText().toString(),
                ((EditText) findViewById(R.id.passwordBox)).getText().toString()
        );
    }

    public void onClickSignUp(View view){
        Intent intent = new Intent(getApplicationContext(), NewAccountScreen.class);
        startActivityForResult(intent,0);
    }

    public static String typeString(Integer type) {
        switch (type) {
            case ADMIN: return "Admin";
            case PATIENT: return "Patient";
            case EMPLOYEE: return "Employee";
        }
        return null;
    }
}
