package com.techfix.app.models;

public class SparePart {

    private int partId;
    private String partName;
    private String description;
    private double price;
    private int stockQuantity;
    private String compatibleModels;

    public SparePart() {
    }

    public SparePart(
            int partId,
            String partName,
            String description,
            double price,
            int stockQuantity,
            String compatibleModels
    ) {
        this.partId = partId;
        this.partName = partName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.compatibleModels = compatibleModels;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCompatibleModels() {
        return compatibleModels;
    }

    public void setCompatibleModels(String compatibleModels) {
        this.compatibleModels = compatibleModels;
    }
}
