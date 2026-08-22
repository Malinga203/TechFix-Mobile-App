package com.techfix.app.models;

public class BranchSparePart {

    private int branchId;
    private int partId;
    private int stockQuantity;

    public BranchSparePart() {
    }

    public BranchSparePart(
            int branchId,
            int partId,
            int stockQuantity
    ) {
        this.branchId = branchId;
        this.partId = partId;
        this.stockQuantity = stockQuantity;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}