package com.techfix.app.models;

public class Branch {

    private int branchId;
    private String branchName;
    private String address;
    private double latitude;
    private double longitude;

    public Branch() {
    }

    public Branch(
            int branchId,
            String branchName,
            String address,
            double latitude,
            double longitude
    ) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}