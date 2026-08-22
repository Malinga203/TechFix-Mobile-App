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
import com.techfix.app.database.RepairMediaDAO;
import com.techfix.app.models.RepairMedia;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.List;

public class SampleRepairsActivity extends AppCompatActivity {

    private RepairMediaDAO repairMediaDAO;
    private RepairMediaAdapter adapter;

    private RecyclerView recyclerSamples;
    private TextView tvNoSamples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        String role =
                sessionManager.getRole();

        if (!User.ROLE_CUSTOMER.equals(role)) {

            Toast.makeText(
                    this,
                    "Customer access required. Current role: " + role,
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        setContentView(
                R.layout.activity_sample_repairs
        );

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarSampleRepairs
                );

        recyclerSamples =
                findViewById(
                        R.id.recyclerSampleRepairs
                );

        tvNoSamples =
                findViewById(
                        R.id.tvNoSampleRepairs
                );

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        repairMediaDAO =
                new RepairMediaDAO(this);

        adapter =
                new RepairMediaAdapter();

        recyclerSamples.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerSamples.setAdapter(
                adapter
        );

        Toast.makeText(
                this,
                "Sample Repairs opened",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (repairMediaDAO != null) {
            loadSamples();
        }
    }

    private void loadSamples() {

        try {

            List<RepairMedia> samples =
                    repairMediaDAO
                            .getApprovedSampleImages();

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

        } catch (Exception exception) {

            Toast.makeText(
                    this,
                    "Unable to load repair samples: "
                            + exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (repairMediaDAO != null) {
            repairMediaDAO.close();
        }
    }
}