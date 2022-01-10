package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProfileEmployee extends AppCompatActivity {

    Button manageInfo;
    Button manageServices;
    Button manageAvailabilities;

    static TextView name;
    static TextView clinicName;
    static TextView clinicAddress;
    static TextView license;
    static TextView description;

    ListView adminServiceList;
    ListView employeeServiceList;

    private List<Service> adminServices;
    private List<Service> employeeServices;
    private DatabaseReference databaseServices = FirebaseDatabase.getInstance().getReference("services");
    private DatabaseReference employeeDatabaseServices = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.getId()).child("services");

    private ListView aviListView;
    private List<Availability> aviList;
    private DatabaseReference aviDatabase = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.getId()).child("availabilities");

    private Employee employee = (Employee) MainActivity.currentUser;

    protected void onStart() {

        super.onStart();
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adminServices.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    adminServices.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(ProfileEmployee.this,adminServices);
                adminServiceList.setAdapter(servicesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        employeeDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employeeServices.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    employeeServices.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(ProfileEmployee.this,employeeServices);
                employeeServiceList.setAdapter(servicesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        aviList = new ArrayList<>();
        aviListView = (ListView) findViewById(R.id.listOfAvailabilities);
        aviDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                aviList.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Availability availability = postSnapshot.getValue(Availability.class);
                    aviList.add(availability);
                }

                AvailabilityList availabilitiesAdapter = new AvailabilityList(ProfileEmployee.this,aviList);
                aviListView.setAdapter(availabilitiesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_employee);

        adminServices = new ArrayList<>();
        employeeServices = new ArrayList<>();

        adminServiceList = (ListView) findViewById(R.id.listServicesAdmin);
        employeeServiceList = (ListView) findViewById(R.id.listServicesByYou);

        manageInfo = (Button) findViewById(R.id.manageButtonPersonal);
        manageServices = (Button) findViewById(R.id.manageServicesEmployee);
        manageAvailabilities = (Button) findViewById(R.id.ManageButtonAvailability);

        name = (TextView) findViewById(R.id.employeeName);
        clinicName = (TextView) findViewById(R.id.clinicName);
        clinicAddress = (TextView) findViewById(R.id.clinicAddress);
        license = (TextView) findViewById(R.id.licensingValue);
        description = (TextView) findViewById(R.id.generalDescriptionValue);

        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.getId());

        name.setText(MainActivity.currentUser.username);
        clinicName.setText(employee.companyName);
        clinicAddress.setText(employee.address);
        if (employee.licensed) {
            license.setText("Licensed");
        }
        else {
            license.setText("Unlicensed");
        }
        description.setText(employee.generalDesc);

        manageInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManagePersonalInfoEmployee.class);
                startActivityForResult(intent, 0);
            }

        });

        manageServices.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageServicesEmployee.class);
                startActivityForResult(intent, 0);
            }

        });

        manageAvailabilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageAvailabilities.class);
                startActivityForResult(intent,0);
            }
        });

    }

    public void onClickManageAvailability(View view){
        Intent intent = new Intent(getApplicationContext(), ManageAvailabilities.class);
        startActivityForResult(intent,0);
    }

    public static void setClinicName(Employee employ) {
        clinicName.setText(employ.companyName);
    }

    public static void setClinicAddress(Employee employ) {
        clinicAddress.setText(employ.address);
    }

    public static void setLicense(Employee employ) {
        if (employ.licensed) {
            license.setText("Licensed");
        }
        else {
            license.setText("Unlicensed");
        }
    }

    public static void setDescription(Employee employ) {
        description.setText(employ.generalDesc);
    }
}
