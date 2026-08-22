package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.RepairAdapter;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.util.List;

public class TechnicianRepairsActivity
        extends AppCompatActivity
        implements RepairAdapter.OnRepairClickListener {

    private RecyclerView recyclerTechnicianRepairs;
    private TextView txtNoTechnicianRepairs;

    private RepairDAO repairDAO;

    private int technicianId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician_repairs
        );

        technicianId =
                getIntent().getIntExtra(
                        TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                        1
                );

        recyclerTechnicianRepairs =
                findViewById(
                        R.id.recyclerTechnicianRepairs
                );

        txtNoTechnicianRepairs =
                findViewById(
                        R.id.txtNoTechnicianRepairs
                );

        repairDAO =
                new RepairDAO(this);

        recyclerTechnicianRepairs.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadRepairs();
    }

    private void loadRepairs() {

        List<Repair> repairs =
                repairDAO.getRepairsByTechnician(
                        technicianId
                );

        if (repairs.isEmpty()) {

            txtNoTechnicianRepairs.setVisibility(
                    View.VISIBLE
            );

            recyclerTechnicianRepairs.setVisibility(
                    View.GONE
            );

            return;
        }

        txtNoTechnicianRepairs.setVisibility(
                View.GONE
        );

        recyclerTechnicianRepairs.setVisibility(
                View.VISIBLE
        );

        RepairAdapter adapter =
                new RepairAdapter(
                        repairs,
                        this
                );

        recyclerTechnicianRepairs.setAdapter(
                adapter
        );
    }

    @Override
    public void onRepairClick(
            Repair repair
    ) {

        Intent intent =
                new Intent(
                        this,
                        TechnicianRepairDetailsActivity.class
                );

        intent.putExtra(
                TechnicianRepairDetailsActivity.EXTRA_REPAIR_ID,
                repair.getRepairId()
        );

        startActivity(intent);
    }
}