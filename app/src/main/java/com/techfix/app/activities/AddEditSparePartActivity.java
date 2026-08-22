package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.SparePartDAO;
import com.techfix.app.models.SparePart;

public class AddEditSparePartActivity
        extends AppCompatActivity {

    public static final String EXTRA_PART_ID =
            "extra_part_id";

    private EditText edtPartName;
    private EditText edtPartDescription;
    private EditText edtPartPrice;
    private EditText edtCompatibleModels;

    private Button btnSaveSparePart;

    private SparePartDAO sparePartDAO;

    private int partId =
            -1;

    private SparePart existingSparePart;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_spare_part
        );

        sparePartDAO =
                new SparePartDAO(this);

        bindViews();

        partId =
                getIntent().getIntExtra(
                        EXTRA_PART_ID,
                        -1
                );

        if (partId > 0) {

            loadSparePart();
        }

        btnSaveSparePart.setOnClickListener(
                view -> saveSparePart()
        );
    }


    // =========================================================
    // BIND VIEWS
    // =========================================================

    private void bindViews() {

        edtPartName =
                findViewById(
                        R.id.edtPartName
                );

        edtPartDescription =
                findViewById(
                        R.id.edtPartDescription
                );

        edtPartPrice =
                findViewById(
                        R.id.edtPartPrice
                );

        edtCompatibleModels =
                findViewById(
                        R.id.edtCompatibleModels
                );

        btnSaveSparePart =
                findViewById(
                        R.id.btnSaveSparePart
                );
    }


    // =========================================================
    // LOAD EXISTING PART
    // =========================================================

    private void loadSparePart() {

        existingSparePart =
                sparePartDAO.getSparePartById(
                        partId
                );

        if (existingSparePart == null) {

            Toast.makeText(
                    this,
                    "Spare part not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        edtPartName.setText(
                existingSparePart.getPartName()
        );

        edtPartDescription.setText(
                existingSparePart.getDescription()
        );

        edtPartPrice.setText(
                String.valueOf(
                        existingSparePart.getPrice()
                )
        );

        edtCompatibleModels.setText(
                existingSparePart.getCompatibleModels()
        );

        btnSaveSparePart.setText(
                "Update Spare Part"
        );
    }


    // =========================================================
    // SAVE
    // =========================================================

    private void saveSparePart() {

        String partName =
                edtPartName
                        .getText()
                        .toString()
                        .trim();

        String description =
                edtPartDescription
                        .getText()
                        .toString()
                        .trim();

        String priceText =
                edtPartPrice
                        .getText()
                        .toString()
                        .trim();

        String compatibleModels =
                edtCompatibleModels
                        .getText()
                        .toString()
                        .trim();


        // =====================================================
        // VALIDATION
        // =====================================================

        if (partName.isEmpty()) {

            edtPartName.setError(
                    "Enter spare part name"
            );

            edtPartName.requestFocus();

            return;
        }

        if (description.isEmpty()) {

            edtPartDescription.setError(
                    "Enter description"
            );

            edtPartDescription.requestFocus();

            return;
        }

        if (priceText.isEmpty()) {

            edtPartPrice.setError(
                    "Enter price"
            );

            edtPartPrice.requestFocus();

            return;
        }

        if (compatibleModels.isEmpty()) {

            edtCompatibleModels.setError(
                    "Enter compatible models"
            );

            edtCompatibleModels.requestFocus();

            return;
        }


        double price;

        try {

            price =
                    Double.parseDouble(
                            priceText
                    );

        } catch (
                NumberFormatException exception
        ) {

            edtPartPrice.setError(
                    "Enter a valid price"
            );

            return;
        }


        if (price <= 0) {

            edtPartPrice.setError(
                    "Price must be greater than 0"
            );

            return;
        }


        // =====================================================
        // INSERT OR UPDATE
        // =====================================================

        if (partId > 0) {

            updateSparePart(
                    partName,
                    description,
                    price,
                    compatibleModels
            );

        } else {

            insertSparePart(
                    partName,
                    description,
                    price,
                    compatibleModels
            );
        }
    }


    // =========================================================
    // INSERT
    // =========================================================

    private void insertSparePart(
            String partName,
            String description,
            double price,
            String compatibleModels
    ) {

        if (
                sparePartDAO.partNameExists(
                        partName
                )
        ) {

            edtPartName.setError(
                    "Spare part already exists"
            );

            return;
        }


        SparePart sparePart =
                new SparePart();

        sparePart.setPartName(
                partName
        );

        sparePart.setDescription(
                description
        );

        sparePart.setPrice(
                price
        );

        sparePart.setCompatibleModels(
                compatibleModels
        );


        long result =
                sparePartDAO.insertSparePart(
                        sparePart
                );


        if (result > 0) {

            Toast.makeText(
                    this,
                    "Spare part added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to add spare part",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =========================================================
    // UPDATE
    // =========================================================

    private void updateSparePart(
            String partName,
            String description,
            double price,
            String compatibleModels
    ) {

        if (existingSparePart == null) {
            return;
        }

        existingSparePart.setPartName(
                partName
        );

        existingSparePart.setDescription(
                description
        );

        existingSparePart.setPrice(
                price
        );

        existingSparePart.setCompatibleModels(
                compatibleModels
        );


        int rows =
                sparePartDAO.updateSparePart(
                        existingSparePart
                );


        if (rows > 0) {

            Toast.makeText(
                    this,
                    "Spare part updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update spare part",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =========================================================
    // CLOSE
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (sparePartDAO != null) {

            sparePartDAO.close();
        }
    }
}