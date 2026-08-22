package com.techfix.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.SparePartAdminAdapter;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.models.SparePart;

import java.util.List;

public class SparePartManagementActivity
        extends AppCompatActivity
        implements SparePartAdminAdapter.OnSparePartActionListener {

    private RecyclerView recyclerAdminSpareParts;

    private TextView txtNoAdminSpareParts;

    private Button btnAddSparePart;

    private SparePartDAO sparePartDAO;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_spare_part_management
        );

        sparePartDAO =
                new SparePartDAO(this);

        recyclerAdminSpareParts =
                findViewById(
                        R.id.recyclerAdminSpareParts
                );

        txtNoAdminSpareParts =
                findViewById(
                        R.id.txtNoAdminSpareParts
                );

        btnAddSparePart =
                findViewById(
                        R.id.btnAddSparePart
                );

        recyclerAdminSpareParts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        btnAddSparePart.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    AddEditSparePartActivity.class
                            );

                    startActivity(
                            intent
                    );
                }
        );
    }


    @Override
    protected void onResume() {

        super.onResume();

        loadSpareParts();
    }


    private void loadSpareParts() {

        List<SparePart> spareParts =
                sparePartDAO.getAllSpareParts();

        if (
                spareParts == null ||
                        spareParts.isEmpty()
        ) {

            txtNoAdminSpareParts.setVisibility(
                    View.VISIBLE
            );

            recyclerAdminSpareParts.setVisibility(
                    View.GONE
            );

            return;
        }

        txtNoAdminSpareParts.setVisibility(
                View.GONE
        );

        recyclerAdminSpareParts.setVisibility(
                View.VISIBLE
        );

        SparePartAdminAdapter adapter =
                new SparePartAdminAdapter(
                        spareParts,
                        this
                );

        recyclerAdminSpareParts.setAdapter(
                adapter
        );
    }


    @Override
    public void onEdit(
            SparePart sparePart
    ) {

        Intent intent =
                new Intent(
                        this,
                        AddEditSparePartActivity.class
                );

        intent.putExtra(
                AddEditSparePartActivity.EXTRA_PART_ID,
                sparePart.getPartId()
        );

        startActivity(
                intent
        );
    }


    @Override
    public void onDelete(
            SparePart sparePart
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Spare Part"
                )
                .setMessage(
                        "Delete \"" +
                                sparePart.getPartName() +
                                "\"?"
                )
                .setPositiveButton(
                        "Delete",
                        (
                                dialog,
                                which
                        ) -> {

                            int rows =
                                    sparePartDAO
                                            .deleteSparePart(
                                                    sparePart.getPartId()
                                            );

                            if (rows > 0) {

                                Toast.makeText(
                                        this,
                                        "Spare part deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadSpareParts();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to delete spare part",
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


    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (sparePartDAO != null) {

            sparePartDAO.close();
        }
    }
}