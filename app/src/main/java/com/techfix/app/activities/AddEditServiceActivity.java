package com.techfix.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.RepairService;
import com.techfix.app.utils.RepairCategoryUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEditServiceActivity
        extends AppCompatActivity {

    public static final String EXTRA_SERVICE_ID =
            "extra_service_id";


    private EditText edtServiceName;

    private EditText edtServiceDescription;

    private EditText edtServicePrice;

    private EditText edtServiceDuration;


    private Spinner spinnerServiceType;

    private Spinner spinnerServiceCategory;


    private Button btnSaveService;


    private ServiceDAO serviceDAO;


    private int serviceId =
            -1;


    private RepairService existingService;


    private final List<String> serviceTypes =
            Arrays.asList(
                    RepairCategoryUtils.TYPE_MOBILE,
                    RepairCategoryUtils.TYPE_COMPUTER
            );


    private List<String> currentCategories =
            new ArrayList<>();


    private boolean loadingExistingService =
            false;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );

        setContentView(
                R.layout.activity_add_edit_service
        );


        serviceDAO =
                new ServiceDAO(this);


        bindViews();


        setupTypeSpinner();


        serviceId =
                getIntent().getIntExtra(
                        EXTRA_SERVICE_ID,
                        -1
                );


        if (serviceId > 0) {

            loadService();

        } else {

            updateCategorySpinner(
                    RepairCategoryUtils.TYPE_MOBILE,
                    null
            );
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


        spinnerServiceType =
                findViewById(
                        R.id.spinnerServiceType
                );


        spinnerServiceCategory =
                findViewById(
                        R.id.spinnerServiceCategory
                );


        btnSaveService =
                findViewById(
                        R.id.btnSaveService
                );
    }


    private void setupTypeSpinner() {

        List<String> displayTypes =
                Arrays.asList(
                        "Mobile",
                        "Computer"
                );


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        displayTypes
                );


        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerServiceType.setAdapter(
                adapter
        );


        spinnerServiceType.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        if (loadingExistingService) {

                            return;
                        }


                        String selectedType =
                                serviceTypes.get(
                                        position
                                );


                        updateCategorySpinner(
                                selectedType,
                                null
                        );
                    }


                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                }
        );
    }


    private void updateCategorySpinner(
            String type,
            String categoryToSelect
    ) {

        currentCategories =
                new ArrayList<>(
                        RepairCategoryUtils
                                .getCategoriesForType(
                                        type
                                )
                );


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        currentCategories
                );


        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerServiceCategory.setAdapter(
                adapter
        );


        if (categoryToSelect != null) {

            int position =
                    currentCategories.indexOf(
                            categoryToSelect
                    );


            if (position >= 0) {

                spinnerServiceCategory
                        .setSelection(
                                position
                        );
            }
        }
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


        loadingExistingService =
                true;


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


        String existingType =
                existingService.getServiceType();


        if (
                RepairCategoryUtils.TYPE_COMPUTER
                        .equalsIgnoreCase(
                                existingType
                        )
        ) {

            spinnerServiceType.setSelection(
                    1
            );

        } else {

            spinnerServiceType.setSelection(
                    0
            );
        }


        updateCategorySpinner(
                existingType,
                existingService.getCategory()
        );


        loadingExistingService =
                false;


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


        if (
                spinnerServiceType
                        .getSelectedItemPosition()
                        ==
                        AdapterView.INVALID_POSITION
        ) {

            Toast.makeText(
                    this,
                    "Select device type",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (
                spinnerServiceCategory
                        .getSelectedItem()
                        ==
                        null
        ) {

            Toast.makeText(
                    this,
                    "Select repair category",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        double price;

        int duration;


        try {

            price =
                    Double.parseDouble(
                            priceText
                    );

        } catch (
                NumberFormatException e
        ) {

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

        } catch (
                NumberFormatException e
        ) {

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


        String serviceType =
                serviceTypes.get(
                        spinnerServiceType
                                .getSelectedItemPosition()
                );


        String category =
                spinnerServiceCategory
                        .getSelectedItem()
                        .toString();


        if (serviceId > 0) {

            updateService(
                    name,
                    description,
                    price,
                    duration,
                    serviceType,
                    category
            );

        } else {

            insertService(
                    name,
                    description,
                    price,
                    duration,
                    serviceType,
                    category
            );
        }
    }


    private void insertService(
            String name,
            String description,
            double price,
            int duration,
            String serviceType,
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


        service.setServiceType(
                serviceType
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
            String serviceType,
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


        existingService.setServiceType(
                serviceType
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