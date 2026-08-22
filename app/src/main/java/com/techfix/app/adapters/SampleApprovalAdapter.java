package com.techfix.app.adapters;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.RepairMedia;

import java.util.ArrayList;
import java.util.List;

public class SampleApprovalAdapter
        extends RecyclerView.Adapter<SampleApprovalAdapter.SampleViewHolder> {

    public interface OnApprovalActionListener {

        void onApprove(RepairMedia media);

        void onReject(RepairMedia media);
    }

    private final List<RepairMedia> items =
            new ArrayList<>();

    private final OnApprovalActionListener listener;

    public SampleApprovalAdapter(
            OnApprovalActionListener listener
    ) {

        this.listener =
                listener;
    }

    public void setItems(
            List<RepairMedia> newItems
    ) {

        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_sample_approval,
                                parent,
                                false
                        );

        return new SampleViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull SampleViewHolder holder,
            int position
    ) {

        RepairMedia media =
                items.get(position);

        holder.imgSample.setImageURI(null);

        if (!TextUtils.isEmpty(
                media.getImageUri()
        )) {

            holder.imgSample.setImageURI(
                    Uri.parse(
                            media.getImageUri()
                    )
            );
        }

        holder.tvTitle.setText(
                media.getDeviceName()
                        + " • "
                        + media.getServiceName()
        );

        holder.tvCaption.setText(
                TextUtils.isEmpty(
                        media.getCaption()
                )
                        ? "No description"
                        : media.getCaption()
        );

        holder.tvMeta.setText(
                "Repair R-" +
                        String.format(
                                "%03d",
                                media.getRepairId()
                        ) +
                        " • Technician #" +
                        media.getTechnicianId()
        );

        holder.btnApprove.setOnClickListener(
                view ->
                        listener.onApprove(
                                media
                        )
        );

        holder.btnReject.setOnClickListener(
                view ->
                        listener.onReject(
                                media
                        )
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SampleViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgSample;

        TextView tvTitle;
        TextView tvCaption;
        TextView tvMeta;

        Button btnApprove;
        Button btnReject;

        SampleViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            imgSample =
                    itemView.findViewById(
                            R.id.imgApprovalSample
                    );

            tvTitle =
                    itemView.findViewById(
                            R.id.tvApprovalTitle
                    );

            tvCaption =
                    itemView.findViewById(
                            R.id.tvApprovalCaption
                    );

            tvMeta =
                    itemView.findViewById(
                            R.id.tvApprovalMeta
                    );

            btnApprove =
                    itemView.findViewById(
                            R.id.btnApproveSample
                    );

            btnReject =
                    itemView.findViewById(
                            R.id.btnRejectSample
                    );
        }
    }
}