package com.techfix.app.models;

import java.util.Locale;

public class RepairMedia {

    /*
     * Legacy values are kept because old database rows
     * may still contain these values.
     *
     * New technician photos are always TYPE_PROGRESS.
     * Public sample visibility is controlled by isSample.
     */
    public static final String TYPE_PROGRESS =
            "PROGRESS";

    public static final String TYPE_SAMPLE =
            "SAMPLE";

    public static final String APPROVAL_PENDING =
            "PENDING";

    public static final String APPROVAL_APPROVED =
            "APPROVED";

    public static final String APPROVAL_REJECTED =
            "REJECTED";


    private long mediaId;

    private long repairId;

    private long technicianId;

    private String imageUri;

    private String caption;

    private String mediaType;

    private String repairStage;

    private String approvalStatus;

    private String createdAt;

    private String approvedAt;


    /*
     * NEW
     *
     * false = normal repair photo
     * true  = admin-selected public sample
     */
    private boolean sample;


    // Display values obtained from repair table.
    private String deviceName;

    private String serviceName;


    public long getMediaId() {

        return mediaId;
    }


    public void setMediaId(
            long mediaId
    ) {

        this.mediaId =
                mediaId;
    }


    public long getRepairId() {

        return repairId;
    }


    public void setRepairId(
            long repairId
    ) {

        this.repairId =
                repairId;
    }


    public long getTechnicianId() {

        return technicianId;
    }


    public void setTechnicianId(
            long technicianId
    ) {

        this.technicianId =
                technicianId;
    }


    public String getImageUri() {

        return imageUri;
    }


    public void setImageUri(
            String imageUri
    ) {

        this.imageUri =
                imageUri;
    }


    public String getCaption() {

        return caption;
    }


    public void setCaption(
            String caption
    ) {

        this.caption =
                caption;
    }


    public String getMediaType() {

        return mediaType;
    }


    public void setMediaType(
            String mediaType
    ) {

        this.mediaType =
                mediaType;
    }


    public String getRepairStage() {

        return repairStage;
    }


    public void setRepairStage(
            String repairStage
    ) {

        this.repairStage =
                repairStage;
    }


    public String getApprovalStatus() {

        return approvalStatus;
    }


    public void setApprovalStatus(
            String approvalStatus
    ) {

        this.approvalStatus =
                approvalStatus;
    }


    public String getCreatedAt() {

        return createdAt;
    }


    public void setCreatedAt(
            String createdAt
    ) {

        this.createdAt =
                createdAt;
    }


    public String getApprovedAt() {

        return approvedAt;
    }


    public void setApprovedAt(
            String approvedAt
    ) {

        this.approvedAt =
                approvedAt;
    }


    public boolean isSample() {

        return sample;
    }


    public void setSample(
            boolean sample
    ) {

        this.sample =
                sample;
    }


    public String getDeviceName() {

        return deviceName;
    }


    public void setDeviceName(
            String deviceName
    ) {

        this.deviceName =
                deviceName;
    }


    public String getServiceName() {

        return serviceName;
    }


    public void setServiceName(
            String serviceName
    ) {

        this.serviceName =
                serviceName;
    }


    public boolean isProgress() {

        return TYPE_PROGRESS.equals(
                mediaType
        );
    }


    public String getReadableStage() {

        if (
                repairStage == null ||
                        repairStage.trim().isEmpty()
        ) {

            return "Repair Update";
        }


        String value =
                repairStage
                        .replace(
                                "_",
                                " "
                        )
                        .toLowerCase(
                                Locale.getDefault()
                        );


        String[] words =
                value.split(
                        " "
                );


        StringBuilder result =
                new StringBuilder();


        for (
                String word
                :
                words
        ) {

            if (word.isEmpty()) {

                continue;
            }


            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );


            if (word.length() > 1) {

                result.append(
                        word.substring(1)
                );
            }


            result.append(
                    " "
            );
        }


        return result
                .toString()
                .trim();
    }
}