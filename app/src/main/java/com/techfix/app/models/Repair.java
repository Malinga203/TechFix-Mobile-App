package com.techfix.app.models;

import java.util.Locale;

public class Repair {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_DIAGNOSING = "DIAGNOSING";
    public static final String STATUS_REPAIRING = "REPAIRING";
    public static final String STATUS_READY_FOR_COLLECTION = "READY_FOR_COLLECTION";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private long repairId;
    private long appointmentId;
    private long customerId;
    private long branchId;
    private long technicianId;

    private String deviceName;
    private String serviceName;
    private String problemDescription;
    private String status;
    private String imageUri;

    private double estimatedCost;
    private double finalCost;

    private String createdAt;
    private String updatedAt;
    private String completedAt;

    public Repair() {
        status = STATUS_PENDING;
    }

    public long getRepairId() {
        return repairId;
    }

    public void setRepairId(long repairId) {
        this.repairId = repairId;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getBranchId() {
        return branchId;
    }

    public void setBranchId(long branchId) {
        this.branchId = branchId;
    }

    public long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(long technicianId) {
        this.technicianId = technicianId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    public static boolean isValidStatus(String status) {
        return STATUS_PENDING.equals(status)
                || STATUS_DIAGNOSING.equals(status)
                || STATUS_REPAIRING.equals(status)
                || STATUS_READY_FOR_COLLECTION.equals(status)
                || STATUS_COMPLETED.equals(status);
    }

    public static boolean canTransition(String currentStatus, String nextStatus) {

        if (!isValidStatus(currentStatus) || !isValidStatus(nextStatus)) {
            return false;
        }

        return getStatusIndex(nextStatus) >= getStatusIndex(currentStatus);
    }

    private static int getStatusIndex(String status) {

        switch (status) {

            case STATUS_PENDING:
                return 0;

            case STATUS_DIAGNOSING:
                return 1;

            case STATUS_REPAIRING:
                return 2;

            case STATUS_READY_FOR_COLLECTION:
                return 3;

            case STATUS_COMPLETED:
                return 4;

            default:
                return -1;
        }
    }

    public int getStatusProgress() {

        switch (status) {

            case STATUS_PENDING:
                return 10;

            case STATUS_DIAGNOSING:
                return 30;

            case STATUS_REPAIRING:
                return 65;

            case STATUS_READY_FOR_COLLECTION:
                return 90;

            case STATUS_COMPLETED:
                return 100;

            default:
                return 0;
        }
    }

    public String getReadableStatus() {

        if (status == null || status.trim().isEmpty()) {
            return "Unknown";
        }

        String value = status
                .replace("_", " ")
                .toLowerCase(Locale.getDefault());

        String[] words = value.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {

            if (word.isEmpty()) {
                continue;
            }

            result.append(Character.toUpperCase(word.charAt(0)));

            if (word.length() > 1) {
                result.append(word.substring(1));
            }

            result.append(" ");
        }

        return result.toString().trim();
    }
}