package com.example.walkinclinic;

public class Service {
    private String id;
    private String serviceName;

    private int serviceRole;
    //role of 0 = doctor
    //role of 1 = nurse
    //role of 2 = staff

    private double serviceCost;

    public Service() {}
    public Service(String id, String name, double hourlyRate, int role) {
        this.id = id;
        this.serviceName = name;
        this.serviceCost = hourlyRate;
        this.serviceRole = role;
}
    public Service(String name, double hourlyRate, int role) {
        this.serviceName = name;
        this.serviceCost = hourlyRate;
        this.serviceRole = role;
    }

    //Getters
    public String getId() { return id; }
    public String getServiceName() { return serviceName; }
    public double getServiceCost() { return serviceCost; }
    public int getServiceRole() { return serviceRole; }

    //Setters
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) { this.serviceName = name; }
    public void setServiceCost(double hourlyRate) { this.serviceCost = hourlyRate; }
    public void setServiceRole(int role) { this.serviceRole = role; }
}
