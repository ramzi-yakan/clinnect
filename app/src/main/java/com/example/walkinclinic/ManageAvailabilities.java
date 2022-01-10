package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageAvailabilities extends AppCompatActivity {

    int startHour=-1, startMin=-1, endHour=-1, endMin=-1;
    int startHourTemp, startMinTemp, endHourTemp, endMinTemp;

    private ListView aviListView;
    private List<Availability> aviList;
    private DatabaseReference aviDatabase = FirebaseDatabase.getInstance().getReference("users").child(MainActivity.currentUser.getId()).child("availabilities");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availabilities);

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

                AvailabilityList availabilitiesAdapter = new AvailabilityList(ManageAvailabilities.this,aviList);
                aviListView.setAdapter(availabilitiesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        aviListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Availability availability = aviList.get(position);
                openEditDialog(availability);
                return true;
            }
        });
    }

    private void openEditDialog(Availability availability) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog_employee_availabilities, null);
        dialogBuilder.setView(dialogView);

        final Availability oldAvailability = availability;

        Button editStart = (Button) dialogView.findViewById(R.id.editStartTime);
        Button editEnd = (Button) dialogView.findViewById(R.id.editEndTime);
        Button buttonUpdateService = (Button) dialogView.findViewById(R.id.updateButton);
        Button buttonDeleteService = (Button) dialogView.findViewById(R.id.deleteButton);
        Button buttonCancelService = (Button) dialogView.findViewById(R.id.cancelButton);

        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinnerForWeekDay);
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(availability.day)){
                spinner.setSelection(i);
                break;
            }
        }
        startHourTemp=availability.startHour;
        startMinTemp=availability.startMin;
        endHourTemp=availability.endHour;
        endMinTemp=availability.endMin;
        ((TextView) dialogView.findViewById(R.id.startTimeText)).setText(availability.startHour + "h, " + availability.startMin + "m");
        ((TextView) dialogView.findViewById(R.id.endTimeText)).setText(availability.endHour + "h, " + availability.endMin + "m");

        dialogBuilder.setTitle("Availability");
        final AlertDialog c = dialogBuilder.create();
        c.show();

        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker;
                picker = new TimePickerDialog(ManageAvailabilities.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (endMinTemp!=-1 && (selectedHour>endHourTemp || selectedHour==endHourTemp && selectedMinute>endMinTemp)) {
                            showTimesError();
                        } else {
                            startHourTemp = selectedHour;
                            startMinTemp = selectedMinute;
                            ((TextView) dialogView.findViewById(R.id.startTimeText)).setText(selectedHour + "h, " + selectedMinute + "m");
                        }
                    }
                }, (startHourTemp==-1)?0:startHourTemp, (startMinTemp==-1)?0:startMinTemp, true);
                picker.show();
            }
        });

        editEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker;
                picker = new TimePickerDialog(ManageAvailabilities.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (startMinTemp!=-1 && (startHourTemp>selectedHour || startHourTemp==selectedHour && startMinTemp>selectedMinute)) {
                            showTimesError();
                        } else {
                            endHourTemp = selectedHour;
                            endMinTemp = selectedMinute;
                            ((TextView) dialogView.findViewById(R.id.endTimeText)).setText(selectedHour + "h, " + selectedMinute + "m");
                        }
                    }
                }, (endHourTemp==-1)?0:endHourTemp, (endMinTemp==-1)?0:endMinTemp, true);
                picker.show();
            }
        });

        buttonUpdateService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aviDatabase.child(oldAvailability.id).setValue(new Availability(startHourTemp, startMinTemp, endHourTemp, endMinTemp, spinner.getSelectedItem().toString(),oldAvailability.id));
                c.dismiss();
            }
        });

        buttonDeleteService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aviDatabase.child(oldAvailability.id).removeValue();
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

    public void onClickStartTime(View view){
        TimePickerDialog picker;
        picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (endMin!=-1 && (selectedHour>endHour || selectedHour==endHour && selectedMinute>endMin)) {
                    showTimesError();
                } else {
                    startHour = selectedHour;
                    startMin = selectedMinute;
                    ((TextView) findViewById(R.id.startTimeText)).setText(selectedHour + "h, " + selectedMinute + "m");
                }
            }
        }, (startHour==-1)?0:startHour, (startMin==-1)?0:startMin, true);
        picker.show();
    }

    public void onClickEndTime(View view){
        TimePickerDialog picker;
        picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (startMin!=-1 && (startHour>selectedHour || startHour==selectedHour && startMin>selectedMinute)) {
                    showTimesError();
                } else {
                    endHour = selectedHour;
                    endMin = selectedMinute;
                    ((TextView) findViewById(R.id.endTimeText)).setText(selectedHour + "h, " + selectedMinute + "m");
                }
            }
        }, (endHour==-1)?0:endHour, (endMin==-1)?0:endMin, true);
        picker.show();
    }

    public void showTimesError() {
        Toast.makeText(this,"The start time cannot be later than the end time!",Toast.LENGTH_LONG).show();
    }

    public void createAvailability(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerForWeekDay);

        String id = aviDatabase.push().getKey();
        Availability availability = new Availability(startHour, startMin, endHour, endMin, spinner.getSelectedItem().toString(), id);
        aviDatabase.child(id).setValue(availability);
        System.out.println(availability);
    }
}
