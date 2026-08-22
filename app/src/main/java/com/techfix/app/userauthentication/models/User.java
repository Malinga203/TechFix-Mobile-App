package com.techfix.app.userauthentication.models;

public class User {

    public static final String ROLE_CUSTOMER =
            "CUSTOMER";

    public static final String ROLE_ADMIN =
            "ADMIN";

    public static final String ROLE_TECHNICIAN =
            "TECHNICIAN";

    private int id;

    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;

    private Integer technicianId;

    public User() {
    }

    public User(
            int id,
            String name,
            String email,
            String phone,
            String password,
            String role,
            Integer technicianId
    ) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.technicianId = technicianId;
    }

    public User(
            String name,
            String email,
            String phone,
            String password
    ) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;

        this.role =
                ROLE_CUSTOMER;

        this.technicianId =
                null;
    }

    public int getId() {
        return id;
    }

    public void setId(
            int id
    ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(
            String role
    ) {
        this.role = role;
    }

    public Integer getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(
            Integer technicianId
    ) {
        this.technicianId = technicianId;
    }
}