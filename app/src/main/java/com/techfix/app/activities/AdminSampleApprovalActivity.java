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
import com.techfix.app.adapters.SampleApprovalAdapter;
import com.techfix.app.database.RepairMediaDAO;
import com.techfix.app.models.RepairMedia;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.List;

public class AdminSampleApprovalActivity
        extends AppCompatActivity {

    private RepairMediaDAO repairMediaDAO;
    private SampleApprovalAdapter adapter;

    private RecyclerView recyclerSamples;
    private TextView tvNoSamples;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        SessionManager sessionManager =
                new SessionManager(this);

        if (!sessionManager.isLoggedIn()
                || !sessionManager.isAdmin()) {

            finish();
            return;
        }

        setContentView(
                R.layout.activity_admin_sample_approval
        );

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarSampleApproval
                );

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        recyclerSamples =
                findViewById(
                        R.id.recyclerSampleApproval
                );

        tvNoSamples =
                findViewById(
                        R.id.tvNoPendingSamples
                );

        repairMediaDAO =
                new RepairMediaDAO(this);

        adapter =
                new SampleApprovalAdapter(
                        new SampleApprovalAdapter
                                .OnApprovalActionListener() {

                            @Override
                            public void onApprove(
                                    RepairMedia media
                            ) {

                                updateApproval(
                                        media,
                                        RepairMedia.APPROVAL_APPROVED
                                );
                            }

                            @Override
                            public void onReject(
                                    RepairMedia media
                            ) {

                                updateApproval(
                                        media,
                                        RepairMedia.APPROVAL_REJECTED
                                );
                            }
                        }
                );

        recyclerSamples.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerSamples.setAdapter(
                adapter
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadPendingSamples();
    }

    private void updateApproval(
            RepairMedia media,
            String status
    ) {

        boolean updated =
                repairMediaDAO
                        .updateSampleApproval(
                                media.getMediaId(),
                                status
                        );

        if (!updated) {

            Toast.makeText(
                    this,
                    "Unable to update sample",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Toast.makeText(
                this,
                RepairMedia.APPROVAL_APPROVED.equals(
                        status
                )
                        ? "Sample approved"
                        : "Sample rejected",
                Toast.LENGTH_SHORT
        ).show();

        loadPendingSamples();
    }

    private void loadPendingSamples() {

        List<RepairMedia> samples =
                repairMediaDAO
                        .getPendingSampleImages();

        adapter.setItems(
                samples
        );

        boolean empty =
                samples.isEmpty();

        tvNoSamples.setVisibility(
                empty
                        ? View.VISIBLE
                        : View.GONE
        );

        recyclerSamples.setVisibility(
                empty
                        ? View.GONE
                        : View.VISIBLE
        );
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (repairMediaDAO != null) {
            repairMediaDAO.close();
        }
    }
}