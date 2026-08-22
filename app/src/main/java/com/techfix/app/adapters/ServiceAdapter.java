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

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    public interface OnServiceBookListener {
        void onServiceBook(RepairService service);
    }

    private final List<RepairService> serviceList;
    private final OnServiceBookListener listener;

    public ServiceAdapter(
            List<RepairService> serviceList,
            OnServiceBookListener listener
    ) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_service, parent, false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ServiceViewHolder holder,
            int position
    ) {

        RepairService service =
                serviceList.get(position);

        holder.tvServiceName.setText(service.getServiceName());

        holder.tvServiceDescription.setText(service.getDescription());

        holder.tvServicePrice.setText(
                String.format(Locale.US, "$%.2f", service.getPrice())
        );

        holder.tvServiceDuration.setText(
                String.format(Locale.US, "~%d min", service.getDurationMinutes())
        );

        holder.btnBookNow.setOnClickListener(v ->
                listener.onServiceBook(service)
        );
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        final TextView tvServiceName;
        final TextView tvServiceDescription;
        final TextView tvServicePrice;
        final TextView tvServiceDuration;
        final Button btnBookNow;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceName =
                    itemView.findViewById(R.id.tv_service_name);

            tvServiceDescription =
                    itemView.findViewById(R.id.tv_service_description);

            tvServicePrice =
                    itemView.findViewById(R.id.tv_service_price);

            tvServiceDuration =
                    itemView.findViewById(R.id.tv_service_duration);

            btnBookNow =
                    itemView.findViewById(R.id.btn_book_now);
        }
    }
}
