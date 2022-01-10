package com.example.walkinclinic;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AvailabilityList extends ArrayAdapter<Availability> {

    private Activity context;
    List<Availability> availabilities;

    public AvailabilityList(Activity context, List<Availability> availabilities) {
        super(context, R.layout.activity_availability_list, availabilities);
        this.context = context;
        this.availabilities = availabilities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.activity_service_list, null, true);

        TextView textView = (TextView) listViewItem.findViewById(R.id.serviceNameList);
        Availability availability=availabilities.get(position);
        textView.setText(availability.toString());
        return listViewItem;
    }

}
