package com.qrid.model;

public class User {
    private long id;
    private String name, email, role, department, status;
    public long getId(){return id;} public void setId(long v){id=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
