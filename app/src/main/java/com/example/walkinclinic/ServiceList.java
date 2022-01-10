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

public class ServiceList extends ArrayAdapter<Service> {

        private Activity context;
        List<Service> services;

        public ServiceList(Activity context, List<Service> services) {
            super(context, R.layout.activity_service_list, services);
            this.context = context;
            this.services = services;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.activity_service_list, null, true);

            TextView textViewName = (TextView) listViewItem.findViewById(R.id.serviceNameList);
            TextView textViewRole = (TextView) listViewItem.findViewById(R.id.serviceRoleList);
            TextView textViewCost = (TextView) listViewItem.findViewById(R.id.serviceCostList);

            Service service = services.get(position);
            textViewName.setText(service.getServiceName());

            if (service.getServiceRole() == 0) {
                textViewRole.setText("DOCTOR");
            }
            else if (service.getServiceRole() == 1) {
                textViewRole.setText("NURSE");
            }
            else {
                textViewRole.setText("STAFF");
            }


            textViewCost.setText(String.valueOf(service.getServiceCost()));
            return listViewItem;
        }

}
