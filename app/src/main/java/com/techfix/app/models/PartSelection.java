package com.techfix.app.models;

import java.io.Serializable;

public class PartSelection implements Serializable {

    private static final long serialVersionUID = 1L;

    private int partId;
    private String partName;
    private double unitPrice;
    private int quantity;

    public PartSelection() {
    }

    public PartSelection(
            int partId,
            String partName,
            double unitPrice,
            int quantity
    ) {
        this.partId = partId;
        this.partName = partName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}
