package com.techfix.app.models;

public class RepairSparePart {

    private long repairId;
    private int partId;
    private int quantity;

    private String partName;
    private double unitPrice;

    public RepairSparePart() {
    }

    public RepairSparePart(
            long repairId,
            int partId,
            int quantity
    ) {
        this.repairId = repairId;
        this.partId = partId;
        this.quantity = quantity;
    }

    public long getRepairId() {
        return repairId;
    }

    public void setRepairId(long repairId) {
        this.repairId = repairId;
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
