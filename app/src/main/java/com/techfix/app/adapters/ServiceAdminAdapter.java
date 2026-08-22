package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.RepairService;

import java.util.List;
import java.util.Locale;

public class ServiceAdminAdapter
        extends RecyclerView.Adapter<ServiceAdminAdapter.ServiceViewHolder> {

    public interface OnServiceActionListener {

        void onEdit(
                RepairService service
        );

        void onDelete(
                RepairService service
        );
    }

    private final List<RepairService> services;

    private final OnServiceActionListener listener;

    public ServiceAdminAdapter(
            List<RepairService> services,
            OnServiceActionListener listener
    ) {

        this.services =
                services;

        this.listener =
                listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_admin_service,
                                parent,
                                false
                        );

        return new ServiceViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ServiceViewHolder holder,
            int position
    ) {

        RepairService service =
                services.get(
                        position
                );

        holder.txtName.setText(
                service.getServiceName()
        );

        holder.txtCategory.setText(
                "Category: " +
                        service.getCategory()
        );

        holder.txtDescription.setText(
                service.getDescription()
        );

        holder.txtPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        service.getPrice()
                )
        );

        holder.txtDuration.setText(
                service.getDurationMinutes() +
                        " minutes"
        );

        holder.btnEdit.setOnClickListener(
                view ->
                        listener.onEdit(
                                service
                        )
        );

        holder.btnDelete.setOnClickListener(
                view ->
                        listener.onDelete(
                                service
                        )
        );
    }

    @Override
    public int getItemCount() {

        return services == null
                ? 0
                : services.size();
    }

    static class ServiceViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtCategory;
        private final TextView txtDescription;
        private final TextView txtPrice;
        private final TextView txtDuration;

        private final Button btnEdit;
        private final Button btnDelete;

        public ServiceViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtName =
                    itemView.findViewById(
                            R.id.txtAdminServiceName
                    );

            txtCategory =
                    itemView.findViewById(
                            R.id.txtAdminServiceCategory
                    );

            txtDescription =
                    itemView.findViewById(
                            R.id.txtAdminServiceDescription
                    );

            txtPrice =
                    itemView.findViewById(
                            R.id.txtAdminServicePrice
                    );

            txtDuration =
                    itemView.findViewById(
                            R.id.txtAdminServiceDuration
                    );

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEditService
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDeleteService
                    );
        }
    }
}