package com.example.walkinclinic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Employee extends User {

    public String address, companyName, generalDesc, phoneNumber ,id;
    public boolean licensed;


    public Employee() {}

    public Employee(String username, String password, String id) {
        super(username, password,id);
        this.type = MainActivity.EMPLOYEE;
    }

    public Employee(String username, String password, String address, String companyName, String phoneNumber, String id) {
        super(username, password, id);
        this.type = MainActivity.EMPLOYEE;
        this.address = address;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("username",this.username);
        map.put("password",this.password);
        map.put("type",this.type);

        map.put("address",this.address);
        map.put("companyName",this.companyName);
        map.put("generalDesc",this.generalDesc);
        map.put("phoneNumber",this.phoneNumber);
        map.put("licensed",this.licensed);
        map.put("id",this.id);

        return map;
    }


}
