package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.RepairService;

public class AddEditServiceActivity
        extends AppCompatActivity {

    public static final String EXTRA_SERVICE_ID =
            "extra_service_id";

    private EditText edtServiceName;
    private EditText edtServiceDescription;
    private EditText edtServicePrice;
    private EditText edtServiceDuration;
    private EditText edtServiceCategory;

    private Button btnSaveService;

    private ServiceDAO serviceDAO;

    private int serviceId = -1;

    private RepairService existingService;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_service
        );

        serviceDAO =
                new ServiceDAO(this);

        bindViews();

        serviceId =
                getIntent().getIntExtra(
                        EXTRA_SERVICE_ID,
                        -1
                );

        if (serviceId > 0) {

            loadService();
        }

        btnSaveService.setOnClickListener(
                view -> saveService()
        );
    }


    private void bindViews() {

        edtServiceName =
                findViewById(
                        R.id.edtServiceName
                );

        edtServiceDescription =
                findViewById(
                        R.id.edtServiceDescription
                );

        edtServicePrice =
                findViewById(
                        R.id.edtServicePrice
                );

        edtServiceDuration =
                findViewById(
                        R.id.edtServiceDuration
                );

        edtServiceCategory =
                findViewById(
                        R.id.edtServiceCategory
                );

        btnSaveService =
                findViewById(
                        R.id.btnSaveService
                );
    }


    private void loadService() {

        existingService =
                serviceDAO.getServiceById(
                        serviceId
                );

        if (existingService == null) {

            Toast.makeText(
                    this,
                    "Service not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        edtServiceName.setText(
                existingService.getServiceName()
        );

        edtServiceDescription.setText(
                existingService.getDescription()
        );

        edtServicePrice.setText(
                String.valueOf(
                        existingService.getPrice()
                )
        );

        edtServiceDuration.setText(
                String.valueOf(
                        existingService.getDurationMinutes()
                )
        );

        edtServiceCategory.setText(
                existingService.getCategory()
        );

        btnSaveService.setText(
                "Update Service"
        );
    }


    private void saveService() {

        String name =
                edtServiceName
                        .getText()
                        .toString()
                        .trim();

        String description =
                edtServiceDescription
                        .getText()
                        .toString()
                        .trim();

        String priceText =
                edtServicePrice
                        .getText()
                        .toString()
                        .trim();

        String durationText =
                edtServiceDuration
                        .getText()
                        .toString()
                        .trim();

        String category =
                edtServiceCategory
                        .getText()
                        .toString()
                        .trim();


        if (name.isEmpty()) {

            edtServiceName.setError(
                    "Enter service name"
            );

            return;
        }

        if (description.isEmpty()) {

            edtServiceDescription.setError(
                    "Enter description"
            );

            return;
        }

        if (priceText.isEmpty()) {

            edtServicePrice.setError(
                    "Enter price"
            );

            return;
        }

        if (durationText.isEmpty()) {

            edtServiceDuration.setError(
                    "Enter duration"
            );

            return;
        }

        if (category.isEmpty()) {

            edtServiceCategory.setError(
                    "Enter category"
            );

            return;
        }


        double price;

        int duration;

        try {

            price =
                    Double.parseDouble(
                            priceText
                    );

        } catch (NumberFormatException e) {

            edtServicePrice.setError(
                    "Invalid price"
            );

            return;
        }

        try {

            duration =
                    Integer.parseInt(
                            durationText
                    );

        } catch (NumberFormatException e) {

            edtServiceDuration.setError(
                    "Invalid duration"
            );

            return;
        }


        if (price <= 0) {

            edtServicePrice.setError(
                    "Price must be greater than 0"
            );

            return;
        }

        if (duration <= 0) {

            edtServiceDuration.setError(
                    "Duration must be greater than 0"
            );

            return;
        }


        if (serviceId > 0) {

            updateService(
                    name,
                    description,
                    price,
                    duration,
                    category
            );

        } else {

            insertService(
                    name,
                    description,
                    price,
                    duration,
                    category
            );
        }
    }


    private void insertService(
            String name,
            String description,
            double price,
            int duration,
            String category
    ) {

        if (
                serviceDAO.serviceNameExists(
                        name
                )
        ) {

            edtServiceName.setError(
                    "Service already exists"
            );

            return;
        }


        RepairService service =
                new RepairService();

        service.setServiceName(
                name
        );

        service.setDescription(
                description
        );

        service.setPrice(
                price
        );

        service.setDurationMinutes(
                duration
        );

        service.setCategory(
                category
        );


        long result =
                serviceDAO.insertService(
                        service
                );


        if (result > 0) {

            Toast.makeText(
                    this,
                    "Service added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to add service",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private void updateService(
            String name,
            String description,
            double price,
            int duration,
            String category
    ) {

        if (existingService == null) {
            return;
        }


        existingService.setServiceName(
                name
        );

        existingService.setDescription(
                description
        );

        existingService.setPrice(
                price
        );

        existingService.setDurationMinutes(
                duration
        );

        existingService.setCategory(
                category
        );


        int rows =
                serviceDAO.updateService(
                        existingService
                );


        if (rows > 0) {

            Toast.makeText(
                    this,
                    "Service updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update service",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (serviceDAO != null) {

            serviceDAO.close();
        }
    }
}