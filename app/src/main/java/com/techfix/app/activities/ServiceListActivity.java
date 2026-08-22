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
import com.techfix.app.adapters.ServiceAdapter;
import com.techfix.app.database.ServiceDAO;
import com.techfix.app.models.RepairService;

import java.util.List;

public class ServiceListActivity extends AppCompatActivity
        implements ServiceAdapter.OnServiceBookListener {

    private RecyclerView recyclerServices;
    private TextView tvNoServices;
    private ServiceDAO serviceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        MaterialToolbar toolbar =
                findViewById(R.id.toolbar_services);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerServices =
                findViewById(R.id.recycler_services);

        tvNoServices =
                findViewById(R.id.tv_no_services);

        serviceDAO =
                new ServiceDAO(this);

        recyclerServices.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadServices();
    }

    private void loadServices() {

        List<RepairService> services =
                serviceDAO.getAllServices();

        if (services.isEmpty()) {

            tvNoServices.setVisibility(View.VISIBLE);

            recyclerServices.setVisibility(View.GONE);

            return;
        }

        tvNoServices.setVisibility(View.GONE);

        recyclerServices.setVisibility(View.VISIBLE);

        ServiceAdapter adapter =
                new ServiceAdapter(services, this);

        recyclerServices.setAdapter(adapter);
    }

    @Override
    public void onServiceBook(RepairService service) {

        Intent intent =
                new Intent(this, BookRepairActivity.class);

        intent.putExtra(
                BookRepairActivity.EXTRA_SERVICE_ID,
                service.getServiceId()
        );

        startActivity(intent);
    }
}
