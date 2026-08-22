package com.techfix.app.models;

public class RepairService {

    private int serviceId;
    private String serviceName;
    private String description;
    private double price;
    private int durationMinutes;
    private String category;

    public RepairService() {
    }

    public RepairService(
            int serviceId,
            String serviceName,
            String description,
            double price,
            int durationMinutes,
            String category
    ) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.category = category;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
