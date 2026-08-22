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
import com.techfix.app.adapters.TechnicianAdapter;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.models.Technician;

import java.util.List;

public class TechnicianActivity
        extends AppCompatActivity {

    private RecyclerView recyclerTechnicians;
    private Button btnAddTechnician;

    private TechnicianDao technicianDao;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_technician
        );

        recyclerTechnicians =
                findViewById(
                        R.id.recyclerTechnicians
                );

        btnAddTechnician =
                findViewById(
                        R.id.btnAddTechnician
                );

        recyclerTechnicians.setLayoutManager(
                new LinearLayoutManager(this)
        );

        technicianDao =
                new TechnicianDao(this);

        btnAddTechnician.setOnClickListener(
                view ->
                        openAddTechnician()
        );

        loadTechnicians();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (technicianDao != null) {

            loadTechnicians();
        }
    }

    private void loadTechnicians() {

        List<Technician> technicians =
                technicianDao
                        .getAllTechnicians();

        TechnicianAdapter adapter =
                new TechnicianAdapter(
                        technicians,
                        new TechnicianAdapter
                                .OnTechnicianActionListener() {

                            @Override
                            public void onEdit(
                                    Technician technician
                            ) {

                                openEditTechnician(
                                        technician
                                );
                            }

                            @Override
                            public void onDelete(
                                    Technician technician
                            ) {

                                confirmDelete(
                                        technician
                                );
                            }
                        }
                );

        recyclerTechnicians.setAdapter(
                adapter
        );
    }

    private void openAddTechnician() {

        Intent intent =
                new Intent(
                        TechnicianActivity.this,
                        AddEditTechnicianActivity.class
                );

        startActivity(
                intent
        );
    }

    private void openEditTechnician(
            Technician technician
    ) {

        Intent intent =
                new Intent(
                        TechnicianActivity.this,
                        AddEditTechnicianActivity.class
                );

        intent.putExtra(
                "technician_id",
                technician.getTechnicianId()
        );

        intent.putExtra(
                "technician_name",
                technician.getName()
        );

        intent.putExtra(
                "technician_phone",
                technician.getPhone()
        );

        intent.putExtra(
                "technician_specialization",
                technician.getSpecialization()
        );

        intent.putExtra(
                "technician_available",
                technician.isAvailable()
        );

        intent.putExtra(
                "technician_branch_id",
                technician.getBranchId()
        );

        startActivity(
                intent
        );
    }

    private void confirmDelete(
            Technician technician
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Technician"
                )
                .setMessage(
                        "Are you sure you want to delete "
                                + technician.getName()
                                + "?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            int result =
                                    technicianDao
                                            .deleteTechnician(
                                                    technician
                                                            .getTechnicianId()
                                            );

                            if (result > 0) {

                                Toast.makeText(
                                        this,
                                        "Technician deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadTechnicians();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Failed to delete technician",
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