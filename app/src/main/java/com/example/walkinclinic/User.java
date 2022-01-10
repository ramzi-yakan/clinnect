package com.example.walkinclinic;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String username;
    public String password;
    public int type;
    public String id;

    public User() {}

    public User(String username, String password,String id) {
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("username",this.username);
        map.put("password",this.password);
        map.put("type",this.type);
        map.put("id",this.id);

        return map;
    }
}
