package com.example.walkinclinic;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class UnitTests {
    @Test
    public void serviceGetVars_test() {
        Service service = new Service("thisisatest","hello",123,70);
        assertEquals("Checking id","thisisatest", service.getId());
        assertEquals("Checking name","hello", service.getServiceName());
        assertEquals("Checking rate",123, service.getServiceCost(),0);
        assertEquals("Checking role",70,service.getServiceRole(),0);
    }
    @Test
    public void serviceUpdateAndGetVars_test() {
        Service service = new Service("NOThello","notPoop",0,70);
        service.setId("hello");
        service.setName("poop");
        service.setServiceCost(123);
        service.setServiceRole(0);
        assertEquals("Checking id","hello", service.getId());
        assertEquals("Checking name","poop", service.getServiceName());
        assertEquals("Checking rate",123, service.getServiceCost(),0);
        assertEquals("Checking role",0,service.getServiceRole(),0);
    }
    @Test
    public void userGetType_test() {
        User patient = new Patient("billybob","123","dasdfseseadwa");
        User employee = new Employee("billybob","123","dasdfseseadwa");
        User admin = new Admin("billybob","123","dasdfseseadwa");
        assertEquals("Checking patient type", MainActivity.PATIENT, patient.type);
        assertEquals("Checking employee type", MainActivity.EMPLOYEE, employee.type);
        assertEquals("Checking admin type", MainActivity.ADMIN, admin.type);
    }
    @Test
    public void userGetMap_test() {
        User patient = new Patient("billybob","123","fesjdifkadsdsx");
        Map<String,Object> patientMap = patient.toMap();
        assertEquals("Checking map username value", "billybob", patientMap.get("username"));
        assertEquals("Checking map password value", "123", patientMap.get("password"));
        assertEquals("Checking map type value", MainActivity.PATIENT, patientMap.get("type"));
    }
    @Test
    public void userTypeString_test() {
        assertEquals("Checking admin string",MainActivity.typeString(MainActivity.ADMIN),"Admin");
        assertEquals("Checking patient string",MainActivity.typeString(MainActivity.PATIENT),"Patient");
        assertEquals("Checking employee string",MainActivity.typeString(MainActivity.EMPLOYEE),"Employee");
    }
    @Test
    public void employeeGetMap_test() {
        Employee employee = new Employee("billybob","123","123street","yes","123","feisndufjiesd");
        employee.generalDesc="hi";
        employee.licensed=true;
        Map<String,Object> employeeMap = employee.toMap();
        assertEquals("Checking address value", "123street", employeeMap.get("address"));
        assertEquals("Checking companyName value", "yes", employeeMap.get("companyName"));
        assertEquals("Checking phoneNumber value", "123", employeeMap.get("phoneNumber"));
        assertEquals("Checking generalDesc value", "hi", employeeMap.get("generalDesc"));
        assertEquals("Checking licensed value", true, employeeMap.get("licensed"));
    }
    @Test
    public void availabilityString_test() {
        Availability a = new Availability(
                15,
                30,
                17,
                0,
                "Monday",
                "djjfhbud89wijfuegrj9fewusivd"
        );
        assertEquals("Checking date-time string","Monday, 15:30-17:00",a.toString());
    }
}
