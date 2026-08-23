package com.techfix.app.models;

import java.util.ArrayList;
import java.util.List;

public class Technician {

    private int technicianId;

    private String name;

    private String phone;

    /*
     * Kept for existing UI compatibility.
     * It will contain a display summary.
     *
     * Example:
     * Mobile: Screen, Battery | Computer: RAM, Software
     */
    private String specialization;


    private List<String> mobileSpecializations =
            new ArrayList<>();


    private List<String> computerSpecializations =
            new ArrayList<>();


    private boolean available;

    private int branchId;


    public Technician() {
    }


    public Technician(
            int technicianId,
            String name,
            String phone,
            List<String> mobileSpecializations,
            List<String> computerSpecializations,
            boolean available,
            int branchId
    ) {

        this.technicianId =
                technicianId;

        this.name =
                name;

        this.phone =
                phone;

        setMobileSpecializations(
                mobileSpecializations
        );

        setComputerSpecializations(
                computerSpecializations
        );

        this.available =
                available;

        this.branchId =
                branchId;

        refreshSpecializationSummary();
    }


    /*
     * Compatibility constructor.
     */
    public Technician(
            int technicianId,
            String name,
            String phone,
            String specialization,
            boolean available,
            int branchId
    ) {

        this.technicianId =
                technicianId;

        this.name =
                name;

        this.phone =
                phone;

        this.specialization =
                specialization;

        this.available =
                available;

        this.branchId =
                branchId;
    }


    public int getTechnicianId() {

        return technicianId;
    }


    public void setTechnicianId(
            int technicianId
    ) {

        this.technicianId =
                technicianId;
    }


    public String getName() {

        return name;
    }


    public void setName(
            String name
    ) {

        this.name =
                name;
    }


    public String getPhone() {

        return phone;
    }


    public void setPhone(
            String phone
    ) {

        this.phone =
                phone;
    }


    public String getSpecialization() {

        refreshSpecializationSummary();

        return specialization;
    }


    public void setSpecialization(
            String specialization
    ) {

        this.specialization =
                specialization;
    }


    public List<String> getMobileSpecializations() {

        return new ArrayList<>(
                mobileSpecializations
        );
    }


    public void setMobileSpecializations(
            List<String> mobileSpecializations
    ) {

        this.mobileSpecializations =
                mobileSpecializations == null
                        ?
                        new ArrayList<>()
                        :
                        new ArrayList<>(
                                mobileSpecializations
                        );


        refreshSpecializationSummary();
    }


    public List<String> getComputerSpecializations() {

        return new ArrayList<>(
                computerSpecializations
        );
    }


    public void setComputerSpecializations(
            List<String> computerSpecializations
    ) {

        this.computerSpecializations =
                computerSpecializations == null
                        ?
                        new ArrayList<>()
                        :
                        new ArrayList<>(
                                computerSpecializations
                        );


        refreshSpecializationSummary();
    }


    public boolean hasMobileSpecialization(
            String category
    ) {

        return mobileSpecializations.contains(
                category
        );
    }


    public boolean hasComputerSpecialization(
            String category
    ) {

        return computerSpecializations.contains(
                category
        );
    }


    public boolean isAvailable() {

        return available;
    }


    public void setAvailable(
            boolean available
    ) {

        this.available =
                available;
    }


    public int getBranchId() {

        return branchId;
    }


    public void setBranchId(
            int branchId
    ) {

        this.branchId =
                branchId;
    }


    private void refreshSpecializationSummary() {

        List<String> sections =
                new ArrayList<>();


        if (
                mobileSpecializations != null
                        &&
                        !mobileSpecializations.isEmpty()
        ) {

            sections.add(
                    "Mobile: "
                            +
                            joinList(
                                    mobileSpecializations
                            )
            );
        }


        if (
                computerSpecializations != null
                        &&
                        !computerSpecializations.isEmpty()
        ) {

            sections.add(
                    "Computer: "
                            +
                            joinList(
                                    computerSpecializations
                            )
            );
        }


        if (sections.isEmpty()) {

            if (
                    specialization == null
                            ||
                            specialization.trim().isEmpty()
            ) {

                specialization =
                        "No specialization";
            }

            return;
        }


        specialization =
                joinSections(
                        sections
                );
    }


    private String joinList(
            List<String> items
    ) {

        StringBuilder builder =
                new StringBuilder();


        for (
                int i = 0;
                i < items.size();
                i++
        ) {

            if (i > 0) {

                builder.append(
                        ", "
                );
            }


            builder.append(
                    items.get(i)
            );
        }


        return builder.toString();
    }


    private String joinSections(
            List<String> sections
    ) {

        StringBuilder builder =
                new StringBuilder();


        for (
                int i = 0;
                i < sections.size();
                i++
        ) {

            if (i > 0) {

                builder.append(
                        " | "
                );
            }


            builder.append(
                    sections.get(i)
            );
        }


        return builder.toString();
    }
}