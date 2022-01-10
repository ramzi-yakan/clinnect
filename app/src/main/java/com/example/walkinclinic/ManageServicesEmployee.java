package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageServicesEmployee extends AppCompatActivity {

    private Button refreshButton;
    private Button cancelButton;

    private ListView employeeServices;
    private String userID;

    private RadioGroup sort;
    private int sortNumber;

    private List<Service> services;
    private List<Service> employeeList;
    private DatabaseReference databaseServices = FirebaseDatabase.getInstance().getReference("services");
    private DatabaseReference employeeDatabaseServices = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.getId()).child("services");
    private ListView listViewServices;


    protected void onStart() {

        super.onStart();

        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                services.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    services.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(ManageServicesEmployee.this,services);
                listViewServices.setAdapter(servicesAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }







        });

        employeeDatabaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                employeeList.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    employeeList.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(ManageServicesEmployee.this,employeeList);
                employeeServices.setAdapter(servicesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services_employee);

        refreshButton = (Button) findViewById(R.id.cancelPersonalInfoButton);
        cancelButton = (Button) findViewById(R.id.cancelEmployeeServicesButton);

        employeeServices = (ListView) findViewById(R.id.listViewEmployee);
        listViewServices = (ListView) findViewById(R.id.listOfAvailabilities);

        sort = (RadioGroup) findViewById(R.id.serviceRole);

        services = new ArrayList<>();
        employeeList = new ArrayList<>();

        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                findRadioSelectButton(v);
                refreshServices();
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileEmployee.class);
                startActivityForResult(intent, 0);
            }

        });

        employeeServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Service service = employeeList.get(position);
                System.out.println(service);
                openRemoveDialog(service.getId(),service.getServiceName());
                return true;

            }

        });

        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Service service = services.get(position);
                openAddDialog(service.getId(),service.getServiceName());
                return true;
            }
        });

    }

    private void openRemoveDialog(final String serviceID, String serviceName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog_employee_services, null);
        dialogBuilder.setView(dialogView);

        final Button buttonDeleteService = (Button) dialogView.findViewById(R.id.deleteButton);
        final Button buttonCancelService = (Button) dialogView.findViewById(R.id.cancelButton);

        dialogBuilder.setTitle(serviceName);
        final AlertDialog c = dialogBuilder.create();
        c.show();

        buttonDeleteService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteService(serviceID);
                c.dismiss();
            }

        });

        buttonCancelService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                c.dismiss();
            }

        });


    }

    private void openAddDialog(final String serviceID, final String serviceName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog_employee_services_admin, null);
        dialogBuilder.setView(dialogView);

        final Button buttonAddService = (Button) dialogView.findViewById(R.id.addServiceButton);
        final Button buttonCancelAdd = (Button) dialogView.findViewById(R.id.cancelAddButton);

        dialogBuilder.setTitle(serviceName);
        final AlertDialog d = dialogBuilder.create();
        d.show();

        buttonAddService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                databaseServices.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot data: snapshot.getChildren()) {

                            if(data.getKey().equals(serviceID)){
                                Service service= data.getValue(Service.class);

                                double cost = service.getServiceCost();
                                int role = service.getServiceRole();

                                Service insertService = new Service(serviceID,serviceName,cost,role);

                                employeeDatabaseServices.child(serviceID).setValue(insertService);

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError);
                    }


                });

                refreshEmployeeServices();

                d.dismiss();
            }

        });

        buttonCancelAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                d.dismiss();
            }

        });

    }

    private boolean deleteService(String id) {

        employeeDatabaseServices.child(id).removeValue();
        Toast.makeText(getApplicationContext(), "Service Deleted", Toast.LENGTH_LONG).show();
        return true;
    }

    private void refreshEmployeeServices(){
        ServiceList serviceList = new ServiceList(ManageServicesEmployee.this,employeeList);
        employeeServices.setAdapter(serviceList);
        return;
    }

    private void refreshServices() {
        if (sortNumber == -1) {
            Toast.makeText(this,"Please select a role to refresh list.",Toast.LENGTH_LONG).show();
            return;
        }


        List<Service> services2 = new ArrayList<>();

        if (sortNumber==4){
            ServiceList serviceList = new ServiceList(ManageServicesEmployee.this,services);
            listViewServices.setAdapter(serviceList);
            return;
        }



        for (int i=0; i<services.size(); i++){
            if (services.get(i).getServiceRole()==sortNumber){
                System.out.println(services.get(i).getServiceRole());
                services2.add(services.get(i));
            }
        }

        if (services2.size()==0){
            ServiceList serviceAdapter = new ServiceList(ManageServicesEmployee.this,services2);
            listViewServices.setAdapter(serviceAdapter);
            Toast.makeText(ManageServicesEmployee.this, "There was no services with the given role.", Toast.LENGTH_SHORT).show();

            return;
        }
        else{
            ServiceList serviceAdapter = new ServiceList(ManageServicesEmployee.this,services2);
            listViewServices.setAdapter(serviceAdapter);
            return;
        }
    }

    private void findRadioSelectButton(View view) {
        RadioGroup addButtons = (RadioGroup) findViewById(R.id.serviceRole);
        int id = addButtons.getCheckedRadioButtonId();
        RadioButton chosen = (RadioButton) findViewById(id);

        if (chosen == null) {
            sortNumber=-1;
            return;
        }
        if ("Doctor".equals(chosen.getText().toString())) {
            sortNumber = 0;
        }
        else if ("Nurse".equals(chosen.getText().toString())) {
            sortNumber = 1;
        }
        else if ("Staff".equals(chosen.getText().toString())){
            sortNumber = 2;
        }
        else {
            sortNumber = 4;
        }
    }

}
