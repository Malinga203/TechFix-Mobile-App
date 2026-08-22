package com.techfix.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.AppointmentAdapter;
import com.techfix.app.database.AppointmentDAO;
import com.techfix.app.models.Appointment;
import com.techfix.app.userauthentication.utils.SessionManager;

import java.util.List;

public class MyAppointmentsActivity
        extends AppCompatActivity {

    private RecyclerView recyclerAppointments;
    private TextView txtNoAppointments;

    private AppointmentDAO appointmentDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_my_appointments
        );

        recyclerAppointments =
                findViewById(
                        R.id.recyclerAppointments
                );

        txtNoAppointments =
                findViewById(
                        R.id.txtNoAppointments
                );

        appointmentDAO =
                new AppointmentDAO(this);

        sessionManager =
                new SessionManager(this);

        recyclerAppointments.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadAppointments();
    }

    private void loadAppointments() {

        int userId =
                sessionManager.getUserId();

        List<Appointment> appointments =
                appointmentDAO.getAppointmentsByUser(
                        userId
                );

        if (appointments.isEmpty()) {

            txtNoAppointments.setVisibility(
                    View.VISIBLE
            );

            recyclerAppointments.setVisibility(
                    View.GONE
            );

            return;
        }

        txtNoAppointments.setVisibility(
                View.GONE
        );

        recyclerAppointments.setVisibility(
                View.VISIBLE
        );

        AppointmentAdapter adapter =
                new AppointmentAdapter(
                        appointments
                );

        recyclerAppointments.setAdapter(
                adapter
        );
    }
}