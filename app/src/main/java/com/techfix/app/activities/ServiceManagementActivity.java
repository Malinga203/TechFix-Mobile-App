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
import com.techfix.app.adapters.ServiceAdminAdapter;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.RepairService;

import java.util.List;

public class ServiceManagementActivity
        extends AppCompatActivity
        implements ServiceAdminAdapter.OnServiceActionListener {

    private RecyclerView recyclerServices;

    private TextView txtNoServices;

    private Button btnAddService;

    private ServiceDAO serviceDAO;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_service_management
        );

        serviceDAO =
                new ServiceDAO(this);

        recyclerServices =
                findViewById(
                        R.id.recyclerServices
                );

        txtNoServices =
                findViewById(
                        R.id.txtNoServices
                );

        btnAddService =
                findViewById(
                        R.id.btnAddService
                );

        recyclerServices.setLayoutManager(
                new LinearLayoutManager(this)
        );

        btnAddService.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    AddEditServiceActivity.class
                            );

                    startActivity(intent);
                }
        );
    }


    @Override
    protected void onResume() {

        super.onResume();

        loadServices();
    }


    private void loadServices() {

        List<RepairService> services =
                serviceDAO.getAllServices();

        if (
                services == null ||
                        services.isEmpty()
        ) {

            recyclerServices.setVisibility(
                    View.GONE
            );

            txtNoServices.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        txtNoServices.setVisibility(
                View.GONE
        );

        recyclerServices.setVisibility(
                View.VISIBLE
        );

        ServiceAdminAdapter adapter =
                new ServiceAdminAdapter(
                        services,
                        this
                );

        recyclerServices.setAdapter(
                adapter
        );
    }


    @Override
    public void onEdit(
            RepairService service
    ) {

        Intent intent =
                new Intent(
                        this,
                        AddEditServiceActivity.class
                );

        intent.putExtra(
                AddEditServiceActivity.EXTRA_SERVICE_ID,
                service.getServiceId()
        );

        startActivity(intent);
    }


    @Override
    public void onDelete(
            RepairService service
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Repair Service"
                )
                .setMessage(
                        "Delete \"" +
                                service.getServiceName() +
                                "\"?"
                )
                .setPositiveButton(
                        "Delete",
                        (
                                dialog,
                                which
                        ) -> {

                            int rows =
                                    serviceDAO.deleteService(
                                            service.getServiceId()
                                    );

                            if (rows > 0) {

                                Toast.makeText(
                                        this,
                                        "Service deleted",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadServices();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to delete service",
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

        if (serviceDAO != null) {

            serviceDAO.close();
        }
    }
}