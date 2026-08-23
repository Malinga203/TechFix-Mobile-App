package com.techfix.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
                new RepairMediaDAO(
                        this
                );


        adapter =
                new SampleApprovalAdapter(

                        new SampleApprovalAdapter
                                .OnSampleActionListener() {


                            @Override
                            public void onSetAsSample(
                                    RepairMedia media
                            ) {

                                confirmSetAsSample(
                                        media
                                );
                            }


                            @Override
                            public void onRemoveSample(
                                    RepairMedia media
                            ) {

                                confirmRemoveSample(
                                        media
                                );
                            }
                        }
                );


        recyclerSamples.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );


        recyclerSamples.setAdapter(
                adapter
        );
    }


    @Override
    protected void onResume() {

        super.onResume();


        loadRepairMedia();
    }


    private void confirmSetAsSample(
            RepairMedia media
    ) {

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Approve as Sample"
                )
                .setMessage(
                        "Make this repair photo visible to all customers as a sample repair?"
                )
                .setPositiveButton(
                        "Approve",

                        (
                                dialog,
                                which
                        ) ->

                                setSampleStatus(
                                        media,
                                        true
                                )
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }


    private void confirmRemoveSample(
            RepairMedia media
    ) {

        new AlertDialog.Builder(
                this
        )
                .setTitle(
                        "Remove Sample"
                )
                .setMessage(
                        "Remove this photo from the customer sample gallery?"
                )
                .setPositiveButton(
                        "Remove",

                        (
                                dialog,
                                which
                        ) ->

                                setSampleStatus(
                                        media,
                                        false
                                )
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }


    private void setSampleStatus(
            RepairMedia media,
            boolean isSample
    ) {

        boolean updated =
                repairMediaDAO
                        .updateSampleSelection(
                                media.getMediaId(),
                                isSample
                        );


        if (!updated) {

            Toast.makeText(
                    this,
                    "Unable to update repair sample",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        Toast.makeText(
                this,

                isSample
                        ? "Photo approved as a sample"
                        : "Photo removed from samples",

                Toast.LENGTH_SHORT
        ).show();


        loadRepairMedia();
    }


    private void loadRepairMedia() {

        List<RepairMedia> mediaList =
                repairMediaDAO
                        .getAllRepairMediaForAdmin();


        adapter.setItems(
                mediaList
        );


        boolean empty =
                mediaList == null ||
                        mediaList.isEmpty();


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