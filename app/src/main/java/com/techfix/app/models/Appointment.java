package com.techfix.app.models;

public class Appointment {

    private int appointmentId;
    private int userId;
    private int serviceId;
    private Integer partId;
    private int branchId;
    private String deviceModel;
    private String issueDescription;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    public Appointment() {
    }

    public Appointment(
            int userId,
            int serviceId,
            Integer partId,
            int branchId,
            String deviceModel,
            String issueDescription,
            String appointmentDate,
            String appointmentTime
    ) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.partId = partId;
        this.branchId = branchId;
        this.deviceModel = deviceModel;
        this.issueDescription = issueDescription;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = "PENDING";
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
