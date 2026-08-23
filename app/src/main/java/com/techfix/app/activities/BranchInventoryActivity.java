package com.techfix.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.techfix.app.R;
import com.techfix.app.adapters.BranchInventoryAdapter;
import com.techfix.app.database.BranchDao;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.models.Branch;
import com.techfix.app.models.SparePart;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchInventoryActivity
        extends AppCompatActivity
        implements BranchInventoryAdapter.OnInventoryActionListener {

    // =========================================================
    // UI
    // =========================================================

    private Spinner spinnerBranch;

    private RecyclerView recyclerInventory;

    private TextView txtNoInventory;

    private TextView txtInventoryBranch;


    // =========================================================
    // DAO
    // =========================================================

    private BranchDao branchDao;

    private SparePartDAO sparePartDAO;


    // =========================================================
    // ADAPTER
    // =========================================================

    private BranchInventoryAdapter adapter;


    // =========================================================
    // DATA
    // =========================================================

    private List<Branch> branches =
            new ArrayList<>();


    private Branch selectedBranch;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        // =====================================================
        // ADMIN SECURITY
        // =====================================================

        SessionManager sessionManager =
                new SessionManager(
                        this
                );


        if (
                !sessionManager.isLoggedIn() ||
                        !sessionManager.isAdmin()
        ) {

            Toast.makeText(
                    this,
                    "Administrator access required",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

            return;
        }


        // =====================================================
        // LAYOUT
        // =====================================================

        setContentView(
                R.layout.activity_branch_inventory
        );


        // =====================================================
        // DAO
        // =====================================================

        branchDao =
                new BranchDao(
                        this
                );


        sparePartDAO =
                new SparePartDAO(
                        this
                );


        // =====================================================
        // UI
        // =====================================================

        bindViews();


        setupToolbar();


        setupRecyclerView();


        setupBranchSpinner();


        // =====================================================
        // LOAD
        // =====================================================

        loadBranches();
    }


    // =========================================================
    // BIND
    // =========================================================

    private void bindViews() {

        spinnerBranch =
                findViewById(
                        R.id.spinnerInventoryBranch
                );


        recyclerInventory =
                findViewById(
                        R.id.recyclerBranchInventory
                );


        txtNoInventory =
                findViewById(
                        R.id.txtNoInventory
                );


        txtInventoryBranch =
                findViewById(
                        R.id.txtInventoryBranch
                );
    }


    // =========================================================
    // TOOLBAR
    // =========================================================

    private void setupToolbar() {

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarBranchInventory
                );


        toolbar.setNavigationOnClickListener(
                view ->
                        finish()
        );
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        adapter =
                new BranchInventoryAdapter(
                        this
                );


        recyclerInventory.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        recyclerInventory.setAdapter(
                adapter
        );
    }


    // =========================================================
    // BRANCH SPINNER
    // =========================================================

    private void setupBranchSpinner() {

        spinnerBranch
                .setOnItemSelectedListener(

                        new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {

                                if (
                                        position < 0 ||
                                                position >=
                                                        branches.size()
                                ) {

                                    return;
                                }


                                selectedBranch =
                                        branches.get(
                                                position
                                        );


                                txtInventoryBranch.setText(
                                        "Inventory for " +
                                                selectedBranch
                                                        .getBranchName()
                                );


                                loadInventory();
                            }


                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                                selectedBranch =
                                        null;


                                adapter.setData(
                                        new ArrayList<>(),
                                        new HashMap<>()
                                );
                            }
                        }
                );
    }


    // =========================================================
    // LOAD BRANCHES
    // =========================================================

    private void loadBranches() {

        int previouslySelectedBranchId =
                selectedBranch != null
                        ? selectedBranch.getBranchId()
                        : -1;


        branches =
                branchDao.getAllBranches();


        if (
                branches == null
        ) {

            branches =
                    new ArrayList<>();
        }


        // =====================================================
        // NO BRANCHES
        // =====================================================

        if (
                branches.isEmpty()
        ) {

            selectedBranch =
                    null;


            spinnerBranch.setEnabled(
                    false
            );


            txtInventoryBranch.setText(
                    "No branches available"
            );


            txtNoInventory.setText(
                    "Create a branch before managing inventory."
            );


            txtNoInventory.setVisibility(
                    View.VISIBLE
            );


            recyclerInventory.setVisibility(
                    View.GONE
            );


            return;
        }


        spinnerBranch.setEnabled(
                true
        );


        // =====================================================
        // SPINNER NAMES
        // =====================================================

        List<String> branchNames =
                new ArrayList<>();


        int selectedPosition =
                0;


        for (
                int i = 0;
                i < branches.size();
                i++
        ) {

            Branch branch =
                    branches.get(
                            i
                    );


            branchNames.add(
                    branch.getBranchName()
            );


            if (
                    branch.getBranchId() ==
                            previouslySelectedBranchId
            ) {

                selectedPosition =
                        i;
            }
        }


        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        branchNames
                );


        spinnerAdapter
                .setDropDownViewResource(
                        android.R.layout
                                .simple_spinner_dropdown_item
                );


        spinnerBranch.setAdapter(
                spinnerAdapter
        );


        spinnerBranch.setSelection(
                selectedPosition
        );
    }


    // =========================================================
    // LOAD INVENTORY
    // =========================================================

    private void loadInventory() {

        if (
                selectedBranch == null
        ) {

            return;
        }


        // =====================================================
        // LOAD ALL GLOBAL SPARE PARTS
        // =====================================================

        List<SparePart> spareParts =
                sparePartDAO
                        .getAllSpareParts();


        if (
                spareParts == null
        ) {

            spareParts =
                    new ArrayList<>();
        }


        // =====================================================
        // NO SPARE PARTS
        // =====================================================

        if (
                spareParts.isEmpty()
        ) {

            adapter.setData(
                    new ArrayList<>(),
                    new HashMap<>()
            );


            txtNoInventory.setText(
                    "No spare parts have been created yet."
            );


            txtNoInventory.setVisibility(
                    View.VISIBLE
            );


            recyclerInventory.setVisibility(
                    View.GONE
            );


            return;
        }


        // =====================================================
        // GET STOCK OF EVERY PART FOR SELECTED BRANCH
        // =====================================================

        Map<Integer, Integer> stockMap =
                new HashMap<>();


        for (
                SparePart sparePart
                :
                spareParts
        ) {

            int stock =
                    sparePartDAO
                            .getPartStockAtBranch(

                                    sparePart
                                            .getPartId(),

                                    selectedBranch
                                            .getBranchId()
                            );


            stockMap.put(
                    sparePart.getPartId(),
                    stock
            );
        }


        // =====================================================
        // DISPLAY
        // =====================================================

        adapter.setData(
                spareParts,
                stockMap
        );


        txtNoInventory.setVisibility(
                View.GONE
        );


        recyclerInventory.setVisibility(
                View.VISIBLE
        );
    }


    // =========================================================
    // ADAPTER UPDATE REQUEST
    // =========================================================

    @Override
    public void onUpdateStock(
            SparePart sparePart,
            int currentQuantity,
            int newQuantity
    ) {

        if (
                selectedBranch == null
        ) {

            Toast.makeText(
                    this,
                    "Select a branch first",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        showStockUpdateConfirmation(
                sparePart,
                currentQuantity,
                newQuantity
        );
    }


    // =========================================================
    // CONFIRMATION
    // =========================================================

    private void showStockUpdateConfirmation(
            SparePart sparePart,
            int currentQuantity,
            int newQuantity
    ) {

        if (
                selectedBranch == null
        ) {

            return;
        }


        // =====================================================
        // DIFFERENCE
        // =====================================================

        int difference =
                Math.abs(
                        newQuantity -
                                currentQuantity
                );


        String changeType;


        if (
                newQuantity >
                        currentQuantity
        ) {

            changeType =
                    "increase";

        } else {

            changeType =
                    "decrease";
        }


        // =====================================================
        // MESSAGE
        // =====================================================

        String message =
                "Spare Part: " +
                        sparePart.getPartName() +

                        "\n\n" +

                        "Branch: " +
                        selectedBranch.getBranchName() +

                        "\n\n" +

                        "Current Stock: " +
                        currentQuantity +

                        "\n" +

                        "New Stock: " +
                        newQuantity +

                        "\n\n" +

                        "Stock will " +
                        changeType +
                        " by " +
                        difference +
                        "." +

                        "\n\n" +

                        "Are you sure you want to update this inventory?";


        // =====================================================
        // DIALOG
        // =====================================================

        new AlertDialog.Builder(
                this
        )

                .setTitle(
                        "Confirm Inventory Update"
                )

                .setMessage(
                        message
                )

                .setPositiveButton(
                        "Confirm",

                        (
                                dialog,
                                which
                        ) -> {

                            updateStockQuantity(
                                    sparePart,
                                    newQuantity
                            );
                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    // =========================================================
    // UPDATE DATABASE
    // =========================================================

    private void updateStockQuantity(
            SparePart sparePart,
            int newQuantity
    ) {

        if (
                selectedBranch == null
        ) {

            return;
        }


        if (
                newQuantity < 0
        ) {

            Toast.makeText(
                    this,
                    "Stock quantity cannot be negative",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        /*
         * Existing SparePartDAO method.
         *
         * This inserts the branch-part relationship
         * when it does not exist, or updates its quantity
         * when it already exists.
         */
        long result =
                sparePartDAO
                        .addOrUpdateBranchStock(

                                selectedBranch
                                        .getBranchId(),

                                sparePart
                                        .getPartId(),

                                newQuantity
                        );


        // =====================================================
        // SUCCESS
        // =====================================================

        if (
                result != -1
        ) {

            Toast.makeText(
                    this,

                    sparePart.getPartName() +
                            " stock updated to " +
                            newQuantity,

                    Toast.LENGTH_SHORT
            ).show();


            /*
             * Reload from database so:
             *
             * Current Stock
             * EditText
             * Availability
             *
             * all display the newly stored value.
             */
            loadInventory();


        } else {

            // =================================================
            // FAILURE
            // =================================================

            Toast.makeText(
                    this,
                    "Unable to update branch inventory",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =========================================================
    // RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        /*
         * If the admin returned after creating/editing
         * a spare part, refresh the inventory list.
         */
        if (
                selectedBranch != null &&
                        sparePartDAO != null
        ) {

            loadInventory();
        }
    }


    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();


        if (
                branchDao != null
        ) {

            branchDao.close();
        }


        if (
                sparePartDAO != null
        ) {

            sparePartDAO.close();
        }
    }
}