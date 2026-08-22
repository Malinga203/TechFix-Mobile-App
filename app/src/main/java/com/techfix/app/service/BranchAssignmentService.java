package com.techfix.app.services;

import android.content.Context;
import android.location.Location;

import com.techfix.app.database.BranchDao;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.models.Branch;

import java.util.List;

public class BranchAssignmentService {

    private final BranchDao branchDao;
    private final TechnicianDao technicianDao;
    private final SparePartDAO sparePartDAO;

    public BranchAssignmentService(
            Context context
    ) {

        branchDao =
                new BranchDao(context);

        technicianDao =
                new TechnicianDao(context);

        sparePartDAO =
                new SparePartDAO(context);
    }

    public Branch findNearestSuitableBranch(
            double customerLatitude,
            double customerLongitude,
            String requiredSpecialization,
            Integer requiredPartId
    ) {

        List<Branch> branches =
                branchDao.getAllBranches();

        Branch nearestSuitableBranch =
                null;

        float shortestDistance =
                Float.MAX_VALUE;

        for (Branch branch : branches) {

            boolean technicianAvailable =
                    technicianDao
                            .isTechnicianAvailable(
                                    branch.getBranchId(),
                                    requiredSpecialization
                            );

            if (!technicianAvailable) {
                continue;
            }

            if (requiredPartId != null) {

                boolean partAvailable =
                        sparePartDAO
                                .isPartAvailableAtBranch(
                                        requiredPartId,
                                        branch.getBranchId()
                                );

                if (!partAvailable) {
                    continue;
                }
            }

            float distance =
                    calculateDistance(
                            customerLatitude,
                            customerLongitude,
                            branch.getLatitude(),
                            branch.getLongitude()
                    );

            if (distance < shortestDistance) {

                shortestDistance =
                        distance;

                nearestSuitableBranch =
                        branch;
            }
        }

        return nearestSuitableBranch;
    }

    public float calculateDistance(
            double customerLatitude,
            double customerLongitude,
            double branchLatitude,
            double branchLongitude
    ) {

        float[] results =
                new float[1];

        Location.distanceBetween(
                customerLatitude,
                customerLongitude,
                branchLatitude,
                branchLongitude,
                results
        );

        return results[0] / 1000f;
    }

    public float getDistanceToBranch(
            double customerLatitude,
            double customerLongitude,
            Branch branch
    ) {

        return calculateDistance(
                customerLatitude,
                customerLongitude,
                branch.getLatitude(),
                branch.getLongitude()
        );
    }
}