package com.techfix.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.techfix.app.R;
import com.techfix.app.adapters.RepairMediaAdapter;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.database.RepairMediaDAO;
import com.techfix.app.models.Repair;
import com.techfix.app.models.RepairMedia;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.List;
import java.util.Locale;

public class RepairProgressActivity
        extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID =
            "repair_id";

    private RepairDAO repairDAO;
    private RepairMediaDAO repairMediaDAO;

    private RepairMediaAdapter adapter;

    private long repairId;

    private RecyclerView recyclerProgress;
    private TextView tvRepairInfo;
    private TextView tvNoProgressImages;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_repair_progress
        );

        SessionManager sessionManager =
                new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        boolean isCustomer =
                sessionManager.isCustomer();

        boolean isAdmin =
                sessionManager.isAdmin();

        if (!isCustomer && !isAdmin) {

            Toast.makeText(
                    this,
                    "Access denied",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        repairId =
                getIntent().getLongExtra(
                        EXTRA_REPAIR_ID,
                        -1
                );

        if (repairId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid repair",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        repairDAO =
                new RepairDAO(this);

        repairMediaDAO =
                new RepairMediaDAO(this);

        Repair repair =
                repairDAO.getRepairById(
                        repairId
                );

        if (repair == null) {

            Toast.makeText(
                    this,
                    "Repair not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        // Customers can only view their own repair updates.
        if (isCustomer
                && repair.getCustomerId() > 0
                && repair.getCustomerId()
                != sessionManager.getUserId()) {

            Toast.makeText(
                    this,
                    "You cannot view another customer's repair",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarRepairProgress
                );

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        tvRepairInfo =
                findViewById(
                        R.id.tvProgressRepairInfo
                );

        tvNoProgressImages =
                findViewById(
                        R.id.tvNoProgressImages
                );

        recyclerProgress =
                findViewById(
                        R.id.recyclerRepairProgress
                );

        adapter =
                new RepairMediaAdapter();

        recyclerProgress.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerProgress.setAdapter(
                adapter
        );

        tvRepairInfo.setText(
                String.format(
                        Locale.getDefault(),
                        "R-%03d • %s\n%s • %d%%",
                        repair.getRepairId(),
                        repair.getDeviceName(),
                        repair.getReadableStatus(),
                        repair.getStatusProgress()
                )
        );

        loadProgressImages();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (repairMediaDAO != null) {
            loadProgressImages();
        }
    }

    private void loadProgressImages() {

        List<RepairMedia> media =
                repairMediaDAO
                        .getProgressImagesForRepair(
                                repairId
                        );

        if (media == null) {
            return;
        }

        adapter.setItems(
                media
        );

        boolean empty =
                media.isEmpty();

        tvNoProgressImages.setVisibility(
                empty
                        ? View.VISIBLE
                        : View.GONE
        );

        recyclerProgress.setVisibility(
                empty
                        ? View.GONE
                        : View.VISIBLE
        );
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (repairDAO != null) {
            repairDAO.close();
        }

        if (repairMediaDAO != null) {
            repairMediaDAO.close();
        }
    }
}