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
                new BranchDao(
                        context
                );


        technicianDao =
                new TechnicianDao(
                        context
                );


        sparePartDAO =
                new SparePartDAO(
                        context
                );
    }


    /*
     * NEW VERSION.
     *
     * Branch must have:
     *
     * 1. Technician with matching device type
     * 2. Technician with matching repair category
     * 3. Technician must be available
     * 4. Technician must belong to the branch
     * 5. Branch must contain required spare parts
     * 6. Closest suitable branch is selected
     */
    public Branch findNearestSuitableBranch(
            double customerLatitude,
            double customerLongitude,
            String serviceType,
            String requiredCategory,
            List<PartSelection> requiredParts
    ) {

        List<Branch> branches =
                branchDao.getAllBranches();


        Branch nearestSuitableBranch =
                null;


        float shortestDistance =
                Float.MAX_VALUE;


        for (
                Branch branch
                :
                branches
        ) {

            boolean technicianAvailable =
                    technicianDao
                            .isTechnicianAvailable(
                                    branch.getBranchId(),
                                    serviceType,
                                    requiredCategory
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
     * Compatibility method for any older code.
     */
    public Branch findNearestSuitableBranch(
            double customerLatitude,
            double customerLongitude,
            String requiredCategory,
            List<PartSelection> requiredParts
    ) {

        List<Branch> branches =
                branchDao.getAllBranches();


        Branch nearestSuitableBranch =
                null;


        float shortestDistance =
                Float.MAX_VALUE;


        for (
                Branch branch
                :
                branches
        ) {

            boolean technicianAvailable =
                    technicianDao
                            .isTechnicianAvailable(
                                    branch.getBranchId(),
                                    requiredCategory
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
     * Existing single part compatibility.
     */
    public Branch findNearestSuitableBranch(
            double customerLatitude,
            double customerLongitude,
            String requiredCategory,
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
                requiredCategory,
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
                            selection.getPartId()
                                    <=
                                    0
                            ||
                            selection.getQuantity()
                                    <=
                                    0
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


        return results[0]
                /
                1000f;
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