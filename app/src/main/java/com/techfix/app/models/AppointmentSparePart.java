package com.techfix.app.models;

public class AppointmentSparePart {

    private long appointmentId;
    private int partId;
    private int quantity;

    private String partName;
    private double unitPrice;

    public AppointmentSparePart() {
    }

    public AppointmentSparePart(
            long appointmentId,
            int partId,
            int quantity
    ) {
        this.appointmentId = appointmentId;
        this.partId = partId;
        this.quantity = quantity;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}
