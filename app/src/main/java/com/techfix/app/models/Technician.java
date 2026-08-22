package com.techfix.app.models;

public class Technician {

    private int technicianId;

    private String name;
    private String phone;
    private String specialization;

    private boolean available;

    private int branchId;

    public Technician() {
    }

    public Technician(
            int technicianId,
            String name,
            String phone,
            String specialization,
            boolean available,
            int branchId
    ) {

        this.technicianId = technicianId;
        this.name = name;
        this.phone = phone;
        this.specialization = specialization;
        this.available = available;
        this.branchId = branchId;
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(
            int technicianId
    ) {
        this.technicianId = technicianId;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(
            String specialization
    ) {
        this.specialization =
                specialization;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(
            boolean available
    ) {
        this.available = available;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(
            int branchId
    ) {
        this.branchId = branchId;
    }
}