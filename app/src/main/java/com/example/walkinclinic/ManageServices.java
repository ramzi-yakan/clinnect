package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageServices extends AppCompatActivity {

    private EditText serviceName;
    private RadioGroup editServiceRole;
    private RadioGroup chooseSelectRole;
    private EditText costForService;
    private Button buttonAddService;
    private Button buttonRefreshServices;
    private ListView listViewServices;
    private ListView listEmployeeServices;
    private List<Service> services;
    private DatabaseReference databaseServices;

    private int employeeRoleAdd;
    private int employeeRoleSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        //Specified service names and costs
        serviceName = (EditText) findViewById(R.id.serviceName);
        costForService = (EditText) findViewById(R.id.costForService);

        //List of services
        listViewServices = (ListView) findViewById(R.id.listOfAvailabilities);

        //Buttons to add and refresh service list
        buttonAddService = (Button) findViewById(R.id.updateManageInfoButton);
        buttonRefreshServices = (Button) findViewById(R.id.cancelPersonalInfoButton);

        //The two RadioGroups
        editServiceRole = (RadioGroup) findViewById(R.id.serviceRole);
        chooseSelectRole = (RadioGroup) findViewById(R.id.daysOfWeek);

        //List of services and database
        services = new ArrayList<>();
        databaseServices = FirebaseDatabase.getInstance().getReference("services");

        //adding an onclicklistener to button
        buttonAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findRadioAddButton(view);
                addService();
            }
        });

        buttonRefreshServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findRadioSelectButton(view);
                refreshServices();
            }
        });



        listViewServices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = services.get(i);
                showUpdateDeleteDialog(service.getId(), service.getServiceName());
                return true;
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        databaseServices.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                services.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Service service = postSnapshot.getValue(Service.class);
                    services.add(service);
                }

                ServiceList servicesAdapter = new ServiceList(ManageServices.this,services);
                listViewServices.setAdapter(servicesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    private void addService() {
        if (employeeRoleAdd == -1) {
            Toast.makeText(this,"Please select a role for service.",Toast.LENGTH_LONG).show();
            return;
        }
        String name = serviceName.getText().toString().trim();
        Double cost;
        try{
            cost = Double.parseDouble(String.valueOf(costForService.getText().toString()));
        }

        catch(NumberFormatException e){
            Toast.makeText(this,"Please enter a service cost",Toast.LENGTH_LONG).show();
            return;
        }



        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this,"Please enter a service name",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            String id = databaseServices.push().getKey();
            databaseServices.child(id).setValue(new Service(id,name,cost,employeeRoleAdd));

            serviceName.setText("");
            costForService.setText("");
        }
    }

    private void findRadioAddButton(View view) {
        RadioGroup addButtons = (RadioGroup) findViewById(R.id.daysOfWeek);
        int id = addButtons.getCheckedRadioButtonId();
        RadioButton chosen = (RadioButton) findViewById(id);

        if (id==-1){

                Toast.makeText(this,"Please select a role for service.",Toast.LENGTH_LONG).show();
                return;

            }

        if ("Doctor".equals(chosen.getText().toString())) {

            employeeRoleAdd = 0;
        }
        else if ("Nurse".equals(chosen.getText().toString())) {

            employeeRoleAdd = 1;
        }
        else {

            employeeRoleAdd = 2;
        }
    }

    private void findRadioSelectButton(View view) {
        RadioGroup addButtons = (RadioGroup) findViewById(R.id.serviceRole);
        int id = addButtons.getCheckedRadioButtonId();
        RadioButton chosen = (RadioButton) findViewById(id);

        if (chosen == null) {
            employeeRoleSelect=-1;
            return;
        }
        if ("Doctor".equals(chosen.getText().toString())) {
            employeeRoleSelect = 0;
        }
        else if ("Nurse".equals(chosen.getText().toString())) {
            employeeRoleSelect = 1;
        }
        else if ("Staff".equals(chosen.getText().toString())){
            employeeRoleSelect = 2;
        }
        else {
            employeeRoleSelect =4;
        }
    }

    private void refreshServices() {
        if (employeeRoleSelect == -1) {
            Toast.makeText(this,"Please select a role to refresh list.",Toast.LENGTH_LONG).show();
            return;
        }


        List<Service> services2 = new ArrayList<>();

        if (employeeRoleSelect==4){
            ServiceList serviceList = new ServiceList(ManageServices.this,services);
            listViewServices.setAdapter(serviceList);
            return;
        }



        for (int i=0; i<services.size(); i++){
            if (services.get(i).getServiceRole()==employeeRoleSelect){
                System.out.println(services.get(i).getServiceRole());
                services2.add(services.get(i));
            }
        }

        if (services2.size()==0){
            ServiceList serviceAdapter = new ServiceList(ManageServices.this,services2);
            listViewServices.setAdapter(serviceAdapter);
            Toast.makeText(ManageServices.this, "There was no services with the given role.", Toast.LENGTH_SHORT).show();

            return;
        }
        else{
            ServiceList serviceAdapter = new ServiceList(ManageServices.this,services2);
            listViewServices.setAdapter(serviceAdapter);
            return;
        }
    }

    private void showUpdateDeleteDialog(final String serviceId, String servName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_diaglog, null);
        dialogBuilder.setView(dialogView);

        final EditText serviceName = (EditText) dialogView.findViewById(R.id.serviceNameUpdate);
        final EditText costForService  = (EditText) dialogView.findViewById(R.id.serviceCostUpdate);
        final Button buttonUpdateService = (Button) dialogView.findViewById(R.id.buttonCancelService);
        final Button buttonDeleteService = (Button) dialogView.findViewById(R.id.buttonCancelService);

        dialogBuilder.setTitle(servName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdateService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = serviceName.getText().toString().trim();
                double cost;

                try{
                    cost = Double.parseDouble(String.valueOf(costForService.getText().toString()));
                }
                catch (NumberFormatException e) {

                    cost=-1;

                }

                int role = -1;
                RadioGroup addButtons = (RadioGroup) dialogView.findViewById(R.id.updateRole);
                int id = addButtons.getCheckedRadioButtonId();

                RadioButton chosen = (RadioButton) dialogView.findViewById(id);

                if (chosen == null) {
                    Toast.makeText(ManageServices.this, "Make sure to update all field accordingly. Please retry to update", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("Doctor".equals(chosen.getText().toString())) {
                    role = 0;
                }
                else if ("Nurse".equals(chosen.getText().toString())) {
                    role = 1;
                }
                else if ("Staff".equals(chosen.getText().toString())) {
                    role = 2;
                }
                if (TextUtils.isEmpty(name)|| cost<0 || role == -1) {
                    Toast.makeText(ManageServices.this, "Make sure to update all field accordingly. Please retry to update", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    updateService(serviceId, name, cost, role);
                    b.dismiss();

                }


            }
        });

        buttonDeleteService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteService(serviceId);
                b.dismiss();
            }
        });
    }

    private void updateService(String id, String name, double cost, int role) {

        if (cost<0){

            Toast.makeText(this,"Enter a valid cost >=0",Toast.LENGTH_LONG).show();
            return;
        }

        if (role==-1) {
            Toast.makeText(this,"Make sure to select a role",Toast.LENGTH_LONG).show();
        }

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("services").child(id);
        Service service = new Service(id, name, cost, role);
        dR.setValue(service);
        Toast.makeText(getApplicationContext(),"Service Updated",Toast.LENGTH_LONG).show();
    }

    private boolean deleteService(String id) {

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("services").child(id);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Service Deleted", Toast.LENGTH_LONG).show();
        return true;
    }

}
