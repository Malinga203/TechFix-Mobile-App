package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.techfix.app.R;
import com.techfix.app.adapters.HistoryAdapter;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RepairHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerRepairHistory;
    private TextView tvNoRepairHistory;
    private SearchView searchRepairHistory;

    private RepairDAO repairDAO;
    private HistoryAdapter historyAdapter;

    private final List<Repair> allRepairs =
            new ArrayList<>();

    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_repair_history
        );

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarRepairHistory
                );

        recyclerRepairHistory =
                findViewById(
                        R.id.recyclerRepairHistory
                );

        tvNoRepairHistory =
                findViewById(
                        R.id.tvNoRepairHistory
                );

        searchRepairHistory =
                findViewById(
                        R.id.searchRepairHistory
                );

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        repairDAO =
                new RepairDAO(this);

        historyAdapter =
                new HistoryAdapter(
                        new ArrayList<>(),
                        this::openRepairDetails
                );

        recyclerRepairHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerRepairHistory.setAdapter(
                historyAdapter
        );

        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {

        allRepairs.clear();

        allRepairs.addAll(
                repairDAO.getRepairHistory()
        );

        applySearch();
    }

    private void setupSearch() {

        searchRepairHistory.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(
                            String query
                    ) {

                        currentSearch =
                                query == null
                                        ? ""
                                        : query.trim();

                        applySearch();

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

                        applySearch();

                        return true;
                    }
                }
        );
    }

    private void applySearch() {

        List<Repair> filtered =
                new ArrayList<>();

        for (Repair repair : allRepairs) {

            if (matchesSearch(
                    repair,
                    currentSearch
            )) {

                filtered.add(repair);
            }
        }

        historyAdapter.setRepairs(
                filtered
        );

        tvNoRepairHistory.setVisibility(
                filtered.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );

        recyclerRepairHistory.setVisibility(
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