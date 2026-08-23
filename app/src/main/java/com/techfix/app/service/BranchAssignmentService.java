package com.techfix.app.services;

import android.content.Context;
import android.location.Location;

import com.techfix.app.database.BranchDao;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.models.Branch;
import com.techfix.app.models.PartSelection;

import java.util.ArrayList;
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

    /*
     * NEW MULTI-PART VERSION.
     *
     * A branch is suitable only if:
     * 1. it has an available technician for the service category
     * 2. it has enough stock for EVERY selected spare part
     * 3. among suitable branches, it is the closest one
     */
    public Branch findNearestSuitableBranch(
            double customerLatitude,
            double customerLongitude,
            String requiredSpecialization,
            List<PartSelection> requiredParts
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

            if (
                    !hasEnoughInventory(
                            branch.getBranchId(),
                            requiredParts
                    )
            ) {
                continue;
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

    /*
     * Kept only for compatibility with older code.
     * A legacy single part is treated as quantity 1.
     */
    public Branch findNearestSuitableBranch(
            double customerLatitude,
            double customerLongitude,
            String requiredSpecialization,
            Integer requiredPartId
    ) {

        List<PartSelection> selections =
                new ArrayList<>();

        if (
                requiredPartId != null
                        &&
                        requiredPartId > 0
        ) {

            selections.add(
                    new PartSelection(
                            requiredPartId,
                            "",
                            0.0,
                            1
                    )
            );
        }

        return findNearestSuitableBranch(
                customerLatitude,
                customerLongitude,
                requiredSpecialization,
                selections
        );
    }

    private boolean hasEnoughInventory(
            int branchId,
            List<PartSelection> requiredParts
    ) {

        if (
                requiredParts == null
                        ||
                        requiredParts.isEmpty()
        ) {

            return true;
        }

        for (
                PartSelection selection
                :
                requiredParts
        ) {

            if (
                    selection == null
                            ||
                            selection.getPartId() <= 0
                            ||
                            selection.getQuantity() <= 0
            ) {

                return false;
            }

            int availableStock =
                    sparePartDAO
                            .getPartStockAtBranch(
                                    selection.getPartId(),
                                    branchId
                            );

            if (
                    availableStock
                            <
                            selection.getQuantity()
            ) {

                return false;
            }
        }

        return true;
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
