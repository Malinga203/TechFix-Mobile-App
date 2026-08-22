package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.BranchAdapter;
import com.techfix.app.database.BranchDao;
import com.techfix.app.models.Branch;

import java.util.List;

public class BranchActivity extends AppCompatActivity {

    private RecyclerView recyclerBranches;
    private Button btnAddBranch;

    private BranchDao branchDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_branch
        );

        recyclerBranches =
                findViewById(
                        R.id.recyclerBranches
                );

        btnAddBranch =
                findViewById(
                        R.id.btnAddBranch
                );

        recyclerBranches.setLayoutManager(
                new LinearLayoutManager(this)
        );

        branchDao =
                new BranchDao(this);

        btnAddBranch.setOnClickListener(
                view -> openAddBranch()
        );

        loadBranches();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (branchDao != null) {
            loadBranches();
        }
    }

    private void loadBranches() {

        List<Branch> branches =
                branchDao.getAllBranches();

        BranchAdapter adapter =
                new BranchAdapter(
                        branches,
                        new BranchAdapter.OnBranchActionListener() {

                            @Override
                            public void onEdit(
                                    Branch branch
                            ) {

                                openEditBranch(
                                        branch
                                );
                            }

                            @Override
                            public void onDelete(
                                    Branch branch
                            ) {

                                confirmDelete(
                                        branch
                                );
                            }
                        }
                );

        recyclerBranches.setAdapter(
                adapter
        );
    }

    private void openAddBranch() {

        Intent intent =
                new Intent(
                        BranchActivity.this,
                        AddEditBranchActivity.class
                );

        startActivity(intent);
    }

    private void openEditBranch(
            Branch branch
    ) {

        Intent intent =
                new Intent(
                        BranchActivity.this,
                        AddEditBranchActivity.class
                );

        intent.putExtra(
                "branch_id",
                branch.getBranchId()
        );

        intent.putExtra(
                "branch_name",
                branch.getBranchName()
        );

        intent.putExtra(
                "branch_address",
                branch.getAddress()
        );

        intent.putExtra(
                "branch_latitude",
                branch.getLatitude()
        );

        intent.putExtra(
                "branch_longitude",
                branch.getLongitude()
        );

        startActivity(intent);
    }

    private void confirmDelete(
            Branch branch
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Branch"
                )
                .setMessage(
                        "Are you sure you want to delete "
                                + branch.getBranchName()
                                + "?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            int result =
                                    branchDao.deleteBranch(
                                            branch.getBranchId()
                                    );

                            if (result > 0) {

                                Toast.makeText(
                                        BranchActivity.this,
                                        "Branch deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadBranches();

                            } else {

                                Toast.makeText(
                                        BranchActivity.this,
                                        "Failed to delete branch",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }
}