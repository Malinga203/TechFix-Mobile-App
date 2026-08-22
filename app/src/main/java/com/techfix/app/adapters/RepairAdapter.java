package com.techfix.app.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.techfix.app.R;
import com.techfix.app.models.Repair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepairAdapter
        extends RecyclerView.Adapter<RepairAdapter.RepairViewHolder> {

    public interface OnRepairClickListener {
        void onRepairClick(Repair repair);
    }

    private List<Repair> repairs;
    private final OnRepairClickListener listener;

    public RepairAdapter(
            List<Repair> repairs,
            OnRepairClickListener listener
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
    public RepairViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_repair,
                        parent,
                        false
                );

        return new RepairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RepairViewHolder holder,
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

        holder.tvDeviceName.setText(
                safeText(
                        repair.getDeviceName(),
                        "Unknown device"
                )
        );

        holder.tvServiceName.setText(
                safeText(
                        repair.getServiceName(),
                        "Unknown service"
                )
        );

        holder.tvProblem.setText(
                safeText(
                        repair.getProblemDescription(),
                        "No issue description"
                )
        );

        holder.tvRepairStatus.setText(
                repair.getReadableStatus()
        );

        applyStatusStyle(
                holder.tvRepairStatus,
                repair.getStatus()
        );

        holder.tvTechnician.setText(
                repair.getTechnicianId() > 0
                        ? "Technician #" + repair.getTechnicianId()
                        : "Not assigned yet"
        );

        holder.tvEstimatedCost.setText(
                String.format(
                        Locale.getDefault(),
                        "Rs. %,.2f",
                        Math.max(0, repair.getEstimatedCost())
                )
        );

        holder.tvRepairDate.setText(
                formatDate(
                        repair.getCreatedAt()
                )
        );

        int progress = repair.getStatusProgress();

        holder.progressRepair.setProgress(progress);

        holder.tvProgress.setText(
                String.format(
                        Locale.getDefault(),
                        "%d%%",
                        progress
                )
        );

        holder.tvUpdatedAt.setText(
                "Last updated · "
                        + formatDateTime(
                        repair.getUpdatedAt()
                )
        );

        // Reset the recycled image before loading a new one.
        holder.imgRepair.setImageURI(null);

        holder.imgRepair.setOnClickListener(
                null
        );

        holder.imgRepair.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

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

                holder.imgRepair.setOnClickListener(
                        view -> showRepairImage(
                                view.getContext(),
                                repair.getImageUri()
                        )
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

        holder.btnViewDetails.setOnClickListener(
                view -> {

                    if (listener != null) {
                        listener.onRepairClick(repair);
                    }
                }
        );

        holder.itemView.setOnClickListener(
                view -> {

                    if (listener != null) {
                        listener.onRepairClick(repair);
                    }
                }
        );
    }

    private void applyStatusStyle(
            Chip chip,
            String status
    ) {

        Context context = chip.getContext();

        int background;
        int textColor;

        if (Repair.STATUS_READY_FOR_COLLECTION.equals(status)
                || Repair.STATUS_COMPLETED.equals(status)) {

            background = R.color.status_green_bg;
            textColor = R.color.status_green;

        } else if (Repair.STATUS_DIAGNOSING.equals(status)) {

            background = R.color.status_purple_bg;
            textColor = R.color.status_purple;

        } else if (Repair.STATUS_PENDING.equals(status)) {

            background = R.color.status_orange_bg;
            textColor = R.color.status_orange;

        } else {

            background = R.color.status_blue_bg;
            textColor = R.color.status_blue;
        }

        chip.setChipBackgroundColorResource(background);

        chip.setTextColor(
                ContextCompat.getColor(
                        context,
                        textColor
                )
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

    private String formatDateTime(String value) {

        Date date = parseDate(value);

        if (date == null) {
            return safeText(value, "-");
        }

        return new SimpleDateFormat(
                "dd MMM yyyy, h:mm a",
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

    private void showRepairImage(
            Context context,
            String imageUri
    ) {

        if (TextUtils.isEmpty(imageUri)) {
            return;
        }

        ImageView imageView =
                new ImageView(context);

        int padding =
                (int) (
                        16 *
                                context.getResources()
                                        .getDisplayMetrics()
                                        .density
                );

        imageView.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        imageView.setAdjustViewBounds(
                true
        );

        imageView.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        imageView.setMinimumHeight(
                500
        );

        imageView.setImageURI(
                Uri.parse(imageUri)
        );

        new MaterialAlertDialogBuilder(
                context
        )
                .setTitle(
                        "Device Photo"
                )
                .setView(
                        imageView
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }


    @Override
    public int getItemCount() {
        return repairs.size();
    }

    static class RepairViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgRepair;

        TextView tvRepairId;
        TextView tvDeviceName;
        TextView tvServiceName;
        TextView tvProblem;
        TextView tvTechnician;
        TextView tvEstimatedCost;
        TextView tvRepairDate;
        TextView tvProgress;
        TextView tvUpdatedAt;

        Chip tvRepairStatus;
        ProgressBar progressRepair;
        Button btnViewDetails;

        RepairViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            imgRepair =
                    itemView.findViewById(
                            R.id.imgRepair
                    );

            tvRepairId =
                    itemView.findViewById(
                            R.id.tvRepairId
                    );

            tvDeviceName =
                    itemView.findViewById(
                            R.id.tvDeviceName
                    );

            tvServiceName =
                    itemView.findViewById(
                            R.id.tvServiceName
                    );

            tvProblem =
                    itemView.findViewById(
                            R.id.tvProblem
                    );

            tvRepairStatus =
                    itemView.findViewById(
                            R.id.tvRepairStatus
                    );

            tvTechnician =
                    itemView.findViewById(
                            R.id.tvTechnician
                    );

            tvEstimatedCost =
                    itemView.findViewById(
                            R.id.tvEstimatedCost
                    );

            tvRepairDate =
                    itemView.findViewById(
                            R.id.tvRepairDate
                    );

            progressRepair =
                    itemView.findViewById(
                            R.id.progressRepair
                    );

            tvProgress =
                    itemView.findViewById(
                            R.id.tvProgress
                    );

            tvUpdatedAt =
                    itemView.findViewById(
                            R.id.tvUpdatedAt
                    );

            btnViewDetails =
                    itemView.findViewById(
                            R.id.btnViewDetails
                    );
        }
    }
}