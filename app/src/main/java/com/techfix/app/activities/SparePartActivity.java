package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import com.techfix.app.R;
import com.techfix.app.adapters.SparePartAdapter;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.models.SparePart;

import java.util.List;

public class SparePartActivity extends AppCompatActivity
        implements SparePartAdapter.OnPartAddListener {

    public static final String EXTRA_SELECTED_PART =
            "extra_selected_part";

    private RecyclerView recyclerSpareParts;
    private TextView tvNoParts;
    private SparePartDAO sparePartDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spare_part);

        MaterialToolbar toolbar =
                findViewById(R.id.toolbar_spare_parts);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerSpareParts =
                findViewById(R.id.recycler_spare_parts);

        tvNoParts =
                findViewById(R.id.tv_no_parts);

        sparePartDAO =
                new SparePartDAO(this);

        recyclerSpareParts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadSpareParts();
    }

    private void loadSpareParts() {

        List<SparePart> spareParts =
                sparePartDAO.getAllSpareParts();

        if (spareParts.isEmpty()) {

            tvNoParts.setVisibility(View.VISIBLE);

            recyclerSpareParts.setVisibility(View.GONE);

            return;
        }

        tvNoParts.setVisibility(View.GONE);

        recyclerSpareParts.setVisibility(View.VISIBLE);

        SparePartAdapter adapter =
                new SparePartAdapter(spareParts, this);

        recyclerSpareParts.setAdapter(adapter);
    }

    @Override
    public void onPartAdd(SparePart sparePart) {

        Intent resultIntent =
                new Intent();

        resultIntent.putExtra(
                EXTRA_SELECTED_PART,
                sparePart.getPartId()
        );

        resultIntent.putExtra(
                EXTRA_SELECTED_PART + "_name",
                sparePart.getPartName()
        );

        resultIntent.putExtra(
                EXTRA_SELECTED_PART + "_price",
                sparePart.getPrice()
        );

        setResult(RESULT_OK, resultIntent);

        finish();
    }
}
