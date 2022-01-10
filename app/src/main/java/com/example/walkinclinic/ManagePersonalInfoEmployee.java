package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManagePersonalInfoEmployee extends AppCompatActivity {

    private EditText address;
    private EditText companyName;
    private EditText generalDescription;

    private Switch licensed;

    private Button updateInfoButton;
    private Button cancelInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_personal_info_employee);

        address = (EditText) findViewById(R.id.editTextAddress);
        companyName = (EditText) findViewById(R.id.editTextCompanyName);
        generalDescription = (EditText) findViewById(R.id.generalDesc);

        licensed = (Switch) findViewById(R.id.licensedSwitch);

        updateInfoButton = (Button) findViewById(R.id.updatePersonalInfoButton);
        cancelInfoButton = (Button) findViewById(R.id.cancelPersonalInfoButton);

        final Employee employee = (Employee) MainActivity.currentUser;

        updateInfoButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                DatabaseReference dR = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.id);

                String addressUpload = address.getText().toString().trim();
                String companyUpload = companyName.getText().toString().trim();
                String generalDescUpload = generalDescription.getText().toString().trim();
                boolean license = licensed.isChecked();

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    if (geocoder.getFromLocationName(addressUpload,5).size()==0) {
                        //((EditText)findViewById(R.id.address)).setError("Could not find this location");
                        makeGeocoderToast();
                        return;
                    };
                } catch (Exception e) {
                    makeGeocoderToast();
                    return;
                }

                if (!TextUtils.isEmpty(addressUpload)) {
                    dR.child("address").setValue(addressUpload);
                    employee.address = addressUpload;
                }
                if (!TextUtils.isEmpty(companyUpload)) {
                    dR.child("companyName").setValue(companyUpload);
                    employee.companyName = companyUpload;
                }
                if (!TextUtils.isEmpty(generalDescUpload)) {
                    dR.child("generalDesc").setValue(generalDescUpload);
                    employee.generalDesc = generalDescUpload;
                }
                dR.child("licensed").setValue(license);
                employee.licensed = license;

                ProfileEmployee.setClinicAddress(employee);
                ProfileEmployee.setClinicName(employee);
                ProfileEmployee.setLicense(employee);
                ProfileEmployee.setDescription(employee);

                MainActivity.currentUser = employee;

                Intent intent = new Intent(getApplicationContext(), ProfileEmployee.class);
                startActivityForResult(intent, 0);
            }

        });

        cancelInfoButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileEmployee.class);
                startActivityForResult(intent, 0);
            }

        });
    }

    public void makeGeocoderToast() {
        Toast.makeText(this,"Could not find this location.",Toast.LENGTH_LONG).show();
    }
}
