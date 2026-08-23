package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.models.Appointment;

import java.util.List;

public class AppointmentAdapter
        extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentActionListener {

        void onViewQr(
                Appointment appointment
        );

        void onShareQr(
                Appointment appointment
        );
    }


    private final List<Appointment> appointments;

    private final OnAppointmentActionListener listener;


    public AppointmentAdapter(
            List<Appointment> appointments,
            OnAppointmentActionListener listener
    ) {

        this.appointments =
                appointments;

        this.listener =
                listener;
    }


    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_appointment,
                                parent,
                                false
                        );


        return new AppointmentViewHolder(
                view
        );
    }


    @Override
    public void onBindViewHolder(
            @NonNull AppointmentViewHolder holder,
            int position
    ) {

        Appointment appointment =
                appointments.get(
                        position
                );


        holder.txtAppointmentId.setText(
                "Appointment #" +
                        appointment.getAppointmentId()
        );


        String appointmentCode =
                appointment.getAppointmentCode();


        if (
                appointmentCode == null ||
                        appointmentCode.trim().isEmpty()
        ) {

            holder.txtAppointmentCode.setText(
                    "Code: Not available"
            );


            holder.btnViewQr.setEnabled(
                    false
            );


            holder.btnShareQr.setEnabled(
                    false
            );


        } else {

            holder.txtAppointmentCode.setText(
                    "Code: " +
                            appointmentCode
            );


            holder.btnViewQr.setEnabled(
                    true
            );


            holder.btnShareQr.setEnabled(
                    true
            );
        }


        holder.txtAppointmentDevice.setText(
                "Device: " +
                        appointment.getDeviceModel()
        );


        holder.txtAppointmentDate.setText(
                "Date: " +
                        appointment.getAppointmentDate()
        );


        holder.txtAppointmentTime.setText(
                "Time: " +
                        appointment.getAppointmentTime()
        );


        holder.txtAppointmentStatus.setText(
                "Status: " +
                        appointment.getStatus()
        );


        // =====================================================
        // VIEW QR
        // =====================================================

        holder.btnViewQr.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onViewQr(
                                appointment
                        );
                    }
                }
        );


        // =====================================================
        // SHARE QR
        // =====================================================

        holder.btnShareQr.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onShareQr(
                                appointment
                        );
                    }
                }
        );
    }


    @Override
    public int getItemCount() {

        return appointments.size();
    }


    static class AppointmentViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAppointmentId;
        TextView txtAppointmentCode;
        TextView txtAppointmentDevice;
        TextView txtAppointmentDate;
        TextView txtAppointmentTime;
        TextView txtAppointmentStatus;

        MaterialButton btnViewQr;
        MaterialButton btnShareQr;


        AppointmentViewHolder(
                @NonNull View itemView
        ) {

            super(
                    itemView
            );


            txtAppointmentId =
                    itemView.findViewById(
                            R.id.txtAppointmentId
                    );


            txtAppointmentCode =
                    itemView.findViewById(
                            R.id.txtAppointmentCode
                    );


            txtAppointmentDevice =
                    itemView.findViewById(
                            R.id.txtAppointmentDevice
                    );


            txtAppointmentDate =
                    itemView.findViewById(
                            R.id.txtAppointmentDate
                    );


            txtAppointmentTime =
                    itemView.findViewById(
                            R.id.txtAppointmentTime
                    );


            txtAppointmentStatus =
                    itemView.findViewById(
                            R.id.txtAppointmentStatus
                    );


            btnViewQr =
                    itemView.findViewById(
                            R.id.btnViewAppointmentQr
                    );


            btnShareQr =
                    itemView.findViewById(
                            R.id.btnShareAppointmentQr
                    );
        }
    }
}