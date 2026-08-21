package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.RepairAdapter;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RepairTrackingActivity extends AppCompatActivity {

    private RecyclerView recyclerRepairs;
    private TextView tvNoActiveRepairs;
    private SearchView searchRepairs;
    private Spinner spinnerRepairStatus;

    private RepairDAO repairDAO;
    private RepairAdapter repairAdapter;

    private final List<Repair> allRepairs = new ArrayList<>();

    private String currentSearch = "";
    private String selectedStatus = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_tracking);

        recyclerRepairs =
                findViewById(
                        R.id.recyclerRepairs
                );

        tvNoActiveRepairs =
                findViewById(
                        R.id.tvNoActiveRepairs
                );

        searchRepairs =
                findViewById(
                        R.id.searchRepairs
                );

        spinnerRepairStatus =
                findViewById(
                        R.id.spinnerRepairStatus
                );

        Button btnViewHistory =
                findViewById(
                        R.id.btnViewHistory
                );

        repairDAO = new RepairDAO(this);

        repairAdapter = new RepairAdapter(
                new ArrayList<>(),
                this::openRepairDetails
        );

        recyclerRepairs.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerRepairs.setAdapter(
                repairAdapter
        );

        setupSearch();
        setupStatusFilter();

        btnViewHistory.setOnClickListener(
                view -> startActivity(
                        new Intent(
                                this,
                                RepairHistoryActivity.class
                        )
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRepairs();
    }

    private void loadRepairs() {

        allRepairs.clear();

        allRepairs.addAll(
                repairDAO.getActiveRepairs()
        );

        applyFilters();
    }

    private void setupSearch() {

        searchRepairs.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(
                            String query
                    ) {

                        currentSearch =
                                query == null
                                        ? ""
                                        : query.trim();

                        applyFilters();

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(
                            String newText
                    ) {

                        currentSearch =
                                newText == null
                                        ? ""
                                        : newText.trim();

                        applyFilters();

                        return true;
                    }
                }
        );
    }

    private void setupStatusFilter() {

        String[] labels = {
                "All statuses",
                "Pending",
                "Diagnosing",
                "Repairing",
                "Ready for collection"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        labels
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRepairStatus.setAdapter(
                spinnerAdapter
        );

        spinnerRepairStatus.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        selectedStatus =
                                statusFromPosition(position);

                        applyFilters();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    private String statusFromPosition(int position) {

        switch (position) {

            case 1:
                return Repair.STATUS_PENDING;

            case 2:
                return Repair.STATUS_DIAGNOSING;

            case 3:
                return Repair.STATUS_REPAIRING;

            case 4:
                return Repair.STATUS_READY_FOR_COLLECTION;

            default:
                return "ALL";
        }
    }

    private void applyFilters() {

        List<Repair> filtered = new ArrayList<>();

        for (Repair repair : allRepairs) {

            boolean matchesStatus =
                    "ALL".equals(selectedStatus)
                            || selectedStatus.equals(
                            repair.getStatus()
                    );

            boolean matchesSearch =
                    matchesSearch(
                            repair,
                            currentSearch
                    );

            if (matchesStatus && matchesSearch) {
                filtered.add(repair);
            }
        }

        repairAdapter.setRepairs(filtered);

        tvNoActiveRepairs.setVisibility(
                filtered.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );

        recyclerRepairs.setVisibility(
                filtered.isEmpty()
                        ? View.GONE
                        : View.VISIBLE
        );
    }

    private boolean matchesSearch(
            Repair repair,
            String query
    ) {

        if (TextUtils.isEmpty(query)) {
            return true;
        }

        String value =
                query.toLowerCase(
                        Locale.getDefault()
                );

        String repairCode =
                String.format(
                        Locale.getDefault(),
                        "R-%03d",
                        repair.getRepairId()
                ).toLowerCase(
                        Locale.getDefault()
                );

        return repairCode.contains(value)
                || contains(
                repair.getDeviceName(),
                value
        )
                || contains(
                repair.getServiceName(),
                value
        )
                || contains(
                repair.getProblemDescription(),
                value
        )
                || contains(
                repair.getReadableStatus(),
                value
        );
    }

    private boolean contains(
            String source,
            String query
    ) {

        return source != null
                && source.toLowerCase(
                Locale.getDefault()
        ).contains(query);
    }

    private void openRepairDetails(
            Repair repair
    ) {

        Intent intent =
                new Intent(
                        this,
                        RepairDetailsActivity.class
                );

        intent.putExtra(
                RepairDetailsActivity.EXTRA_REPAIR_ID,
                repair.getRepairId()
        );

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (repairDAO != null) {
            repairDAO.close();
        }
    }
}