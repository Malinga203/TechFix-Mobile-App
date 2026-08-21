package com.techfix.app.adapters;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.Repair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnHistoryClickListener {
        void onHistoryClick(Repair repair);
    }

    private List<Repair> repairs;
    private final OnHistoryClickListener listener;

    public HistoryAdapter(
            List<Repair> repairs,
            OnHistoryClickListener listener
    ) {

        this.repairs = repairs == null
                ? Collections.emptyList()
                : repairs;

        this.listener = listener;

        setHasStableIds(true);
    }

    public void setRepairs(List<Repair> repairs) {

        this.repairs = repairs == null
                ? Collections.emptyList()
                : repairs;

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return repairs.get(position).getRepairId();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_history,
                        parent,
                        false
                );

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HistoryViewHolder holder,
            int position
    ) {

        Repair repair = repairs.get(position);

        holder.tvRepairId.setText(
                String.format(
                        Locale.getDefault(),
                        "R-%03d",
                        repair.getRepairId()
                )
        );

        holder.tvDevice.setText(
                safeText(
                        repair.getDeviceName(),
                        "Unknown device"
                )
        );

        holder.tvService.setText(
                safeText(
                        repair.getServiceName(),
                        "Unknown service"
                )
        );

        holder.tvDate.setText(
                "Completed · "
                        + formatDate(
                        repair.getCompletedAt()
                )
        );

        holder.tvCost.setText(
                String.format(
                        Locale.getDefault(),
                        "Rs. %,.2f",
                        Math.max(0, repair.getFinalCost())
                )
        );

        holder.imgRepair.setImageURI(null);
        holder.imgRepair.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.imgRepair.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        if (!TextUtils.isEmpty(repair.getImageUri())) {

            try {

                holder.imgRepair.setScaleType(
                        ImageView.ScaleType.CENTER_CROP
                );

                holder.imgRepair.setImageURI(
                        Uri.parse(repair.getImageUri())
                );

            } catch (Exception ignored) {

                holder.imgRepair.setScaleType(
                        ImageView.ScaleType.CENTER_INSIDE
                );

                holder.imgRepair.setImageResource(
                        android.R.drawable.ic_menu_camera
                );
            }
        }

        holder.itemView.setOnClickListener(
                view -> {

                    if (listener != null) {
                        listener.onHistoryClick(repair);
                    }
                }
        );
    }

    private String formatDate(String value) {

        Date date = parseDate(value);

        if (date == null) {
            return safeText(value, "-");
        }

        return new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(date);
    }

    private Date parseDate(String value) {

        if (TextUtils.isEmpty(value)) {
            return null;
        }

        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm"
        };

        for (String pattern : patterns) {

            try {

                return new SimpleDateFormat(
                        pattern,
                        Locale.getDefault()
                ).parse(value);

            } catch (ParseException ignored) {
            }
        }

        return null;
    }

    private String safeText(
            String value,
            String fallback
    ) {

        return TextUtils.isEmpty(value)
                ? fallback
                : value.trim();
    }

    @Override
    public int getItemCount() {
        return repairs.size();
    }

    static class HistoryViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgRepair;

        TextView tvRepairId;
        TextView tvDevice;
        TextView tvService;
        TextView tvDate;
        TextView tvCost;

        HistoryViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            imgRepair =
                    itemView.findViewById(
                            R.id.imgHistoryRepair
                    );

            tvRepairId =
                    itemView.findViewById(
                            R.id.tvHistoryRepairId
                    );

            tvDevice =
                    itemView.findViewById(
                            R.id.tvHistoryDevice
                    );

            tvService =
                    itemView.findViewById(
                            R.id.tvHistoryService
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tvHistoryDate
                    );

            tvCost =
                    itemView.findViewById(
                            R.id.tvHistoryCost
                    );
        }
    }
}