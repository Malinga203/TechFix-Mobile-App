package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.Technician;

import java.util.List;

public class TechnicianAdapter
        extends RecyclerView.Adapter<TechnicianAdapter.TechnicianViewHolder> {

    public interface OnTechnicianActionListener {

        void onEdit(
                Technician technician
        );

        void onDelete(
                Technician technician
        );
    }

    private final List<Technician> technicianList;

    private final OnTechnicianActionListener listener;

    public TechnicianAdapter(
            List<Technician> technicianList,
            OnTechnicianActionListener listener
    ) {

        this.technicianList =
                technicianList;

        this.listener =
                listener;
    }

    @NonNull
    @Override
    public TechnicianViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_technician,
                                parent,
                                false
                        );

        return new TechnicianViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull TechnicianViewHolder holder,
            int position
    ) {

        Technician technician =
                technicianList.get(
                        position
                );

        holder.txtTechnicianName
                .setText(
                        technician.getName()
                );

        holder.txtSpecialization
                .setText(
                        technician.getSpecialization()
                );

        holder.txtTechnicianPhone
                .setText(
                        technician.getPhone()
                );

        if (technician.isAvailable()) {

            holder.txtAvailability
                    .setText(
                            "Available"
                    );

        } else {

            holder.txtAvailability
                    .setText(
                            "Not Available"
                    );
        }

        holder.btnEditTechnician
                .setOnClickListener(
                        view ->
                                listener.onEdit(
                                        technician
                                )
                );

        holder.btnDeleteTechnician
                .setOnClickListener(
                        view ->
                                listener.onDelete(
                                        technician
                                )
                );
    }

    @Override
    public int getItemCount() {

        return technicianList.size();
    }

    public static class TechnicianViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtTechnicianName;
        private final TextView txtSpecialization;
        private final TextView txtTechnicianPhone;
        private final TextView txtAvailability;

        private final ImageButton btnEditTechnician;
        private final ImageButton btnDeleteTechnician;

        public TechnicianViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtTechnicianName =
                    itemView.findViewById(
                            R.id.txtTechnicianName
                    );

            txtSpecialization =
                    itemView.findViewById(
                            R.id.txtSpecialization
                    );

            txtTechnicianPhone =
                    itemView.findViewById(
                            R.id.txtTechnicianPhone
                    );

            txtAvailability =
                    itemView.findViewById(
                            R.id.txtAvailability
                    );

            btnEditTechnician =
                    itemView.findViewById(
                            R.id.btnEditTechnician
                    );

            btnDeleteTechnician =
                    itemView.findViewById(
                            R.id.btnDeleteTechnician
                    );
        }
    }
}