package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.techfix.app.R;
import com.techfix.app.adapters.SparePartAdapter;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.models.PartSelection;
import com.techfix.app.models.SparePart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SparePartActivity
        extends AppCompatActivity {

    public static final String EXTRA_SELECTED_PARTS =
            "extra_selected_parts";

    private RecyclerView recyclerSpareParts;

    private TextView tvNoParts;
    private TextView tvSelectedPartsSummary;

    private Button btnConfirmSelectedParts;
    private Button btnClearSelectedParts;

    private SparePartDAO sparePartDAO;

    private SparePartAdapter adapter;

    private ArrayList<PartSelection> initialSelections =
            new ArrayList<>();

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_spare_part
        );

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbar_spare_parts
                );

        setSupportActionBar(
                toolbar
        );

        if (
                getSupportActionBar()
                        !=
                        null
        ) {

            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(
                            true
                    );
        }

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        recyclerSpareParts =
                findViewById(
                        R.id.recycler_spare_parts
                );

        tvNoParts =
                findViewById(
                        R.id.tv_no_parts
                );

        tvSelectedPartsSummary =
                findViewById(
                        R.id.tv_selected_parts_summary
                );

        btnConfirmSelectedParts =
                findViewById(
                        R.id.btn_confirm_selected_parts
                );

        btnClearSelectedParts =
                findViewById(
                        R.id.btn_clear_selected_parts
                );

        sparePartDAO =
                new SparePartDAO(
                        this
                );

        readInitialSelections();

        recyclerSpareParts.setLayoutManager(
                new LinearLayoutManager(
                        this
                )
        );

        btnConfirmSelectedParts
                .setOnClickListener(
                        view ->
                                returnSelections()
                );

        btnClearSelectedParts
                .setOnClickListener(
                        view -> {

                            if (adapter != null) {

                                adapter.clearSelections();
                            }
                        }
                );

        loadSpareParts();
    }

    @SuppressWarnings("unchecked")
    private void readInitialSelections() {

        Serializable serializable =
                getIntent()
                        .getSerializableExtra(
                                EXTRA_SELECTED_PARTS
                        );

        if (
                serializable
                        instanceof
                        ArrayList<?>
        ) {

            ArrayList<?> list =
                    (ArrayList<?>) serializable;

            for (Object item : list) {

                if (
                        item
                                instanceof
                                PartSelection
                ) {

                    initialSelections.add(
                            (PartSelection) item
                    );
                }
            }
        }
    }

    private void loadSpareParts() {

        List<SparePart> spareParts =
                sparePartDAO
                        .getAllSpareParts();

        if (spareParts.isEmpty()) {

            tvNoParts.setVisibility(
                    View.VISIBLE
            );

            recyclerSpareParts.setVisibility(
                    View.GONE
            );

            btnConfirmSelectedParts.setEnabled(
                    false
            );

            btnClearSelectedParts.setEnabled(
                    false
            );

            updateSelectionSummary(
                    new ArrayList<>()
            );

            return;
        }

        tvNoParts.setVisibility(
                View.GONE
        );

        recyclerSpareParts.setVisibility(
                View.VISIBLE
        );

        btnConfirmSelectedParts.setEnabled(
                true
        );

        btnClearSelectedParts.setEnabled(
                true
        );

        adapter =
                new SparePartAdapter(
                        spareParts,
                        initialSelections,
                        this::updateSelectionSummary
                );

        recyclerSpareParts.setAdapter(
                adapter
        );

        updateSelectionSummary(
                adapter.getSelectedParts()
        );
    }

    private void updateSelectionSummary(
            ArrayList<PartSelection> selections
    ) {

        if (
                selections == null
                        ||
                        selections.isEmpty()
        ) {

            tvSelectedPartsSummary.setText(
                    "No spare parts selected"
            );

            btnClearSelectedParts.setEnabled(
                    false
            );

            return;
        }

        int totalUnits =
                0;

        double totalPrice =
                0.0;

        for (
                PartSelection selection
                :
                selections
        ) {

            totalUnits +=
                    selection.getQuantity();

            totalPrice +=
                    selection.getTotalPrice();
        }

        tvSelectedPartsSummary.setText(
                String.format(
                        Locale.getDefault(),
                        "%d different part(s) • %d total unit(s) • LKR %,.2f",
                        selections.size(),
                        totalUnits,
                        totalPrice
                )
        );

        btnClearSelectedParts.setEnabled(
                true
        );
    }

    private void returnSelections() {

        ArrayList<PartSelection> selections =
                adapter == null
                        ?
                        new ArrayList<>()
                        :
                        adapter.getSelectedParts();

        Intent resultIntent =
                new Intent();

        resultIntent.putExtra(
                EXTRA_SELECTED_PARTS,
                selections
        );

        setResult(
                RESULT_OK,
                resultIntent
        );

        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (sparePartDAO != null) {

            sparePartDAO.close();
        }
    }
}
