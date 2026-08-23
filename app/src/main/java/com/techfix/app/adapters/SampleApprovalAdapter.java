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
import java.util.Locale;

public class SampleApprovalAdapter
        extends RecyclerView.Adapter<SampleApprovalAdapter.SampleViewHolder> {

    public interface OnSampleActionListener {

        void onSetAsSample(
                RepairMedia media
        );


        void onRemoveSample(
                RepairMedia media
        );
    }


    private final List<RepairMedia> items =
            new ArrayList<>();


    private final OnSampleActionListener listener;


    public SampleApprovalAdapter(
            OnSampleActionListener listener
    ) {

        this.listener =
                listener;
    }


    public void setItems(
            List<RepairMedia> newItems
    ) {

        items.clear();


        if (newItems != null) {

            items.addAll(
                    newItems
            );
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
                items.get(
                        position
                );


        showImage(
                holder.imgSample,
                media.getImageUri()
        );


        String device =
                TextUtils.isEmpty(
                        media.getDeviceName()
                )
                        ? "Repair"
                        : media.getDeviceName();


        String service =
                TextUtils.isEmpty(
                        media.getServiceName()
                )
                        ? ""
                        : media.getServiceName();


        holder.tvTitle.setText(
                service.isEmpty()

                        ? device

                        : device +
                        " • " +
                        service
        );


        holder.tvCaption.setText(
                TextUtils.isEmpty(
                        media.getCaption()
                )
                        ? "No description"
                        : media.getCaption()
        );


        String stage =
                TextUtils.isEmpty(
                        media.getRepairStage()
                )
                        ? "Repair Update"
                        : media.getReadableStage();


        String technician =
                media.getTechnicianId() > 0

                        ? "Technician #" +
                        media.getTechnicianId()

                        : "Technician";


        holder.tvMeta.setText(
                String.format(

                        Locale.getDefault(),

                        "Repair R-%03d • %s • %s",

                        media.getRepairId(),
                        stage,
                        technician
                )
        );


        if (media.isSample()) {

            holder.tvSampleStatus.setText(
                    "Public Sample • Visible to customers"
            );


            holder.btnApprove.setVisibility(
                    View.GONE
            );


            holder.btnReject.setVisibility(
                    View.VISIBLE
            );


            holder.btnReject.setText(
                    "Remove Sample"
            );


        } else {

            holder.tvSampleStatus.setText(
                    "Repair Media • Not selected as a sample"
            );


            holder.btnApprove.setVisibility(
                    View.VISIBLE
            );


            holder.btnReject.setVisibility(
                    View.GONE
            );


            holder.btnApprove.setText(
                    "Approve as Sample"
            );
        }


        holder.btnApprove
                .setOnClickListener(
                        view -> {

                            if (listener != null) {

                                listener.onSetAsSample(
                                        media
                                );
                            }
                        }
                );


        holder.btnReject
                .setOnClickListener(
                        view -> {

                            if (listener != null) {

                                listener.onRemoveSample(
                                        media
                                );
                            }
                        }
                );
    }


    private void showImage(
            ImageView imageView,
            String uriValue
    ) {

        imageView.setImageURI(
                null
        );


        imageView.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );


        imageView.setImageResource(
                android.R.drawable.ic_menu_camera
        );


        if (
                TextUtils.isEmpty(
                        uriValue
                )
        ) {

            return;
        }


        try {

            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );


            imageView.setImageURI(
                    Uri.parse(
                            uriValue
                    )
            );


        } catch (
                Exception ignored
        ) {

            imageView.setScaleType(
                    ImageView.ScaleType.CENTER_INSIDE
            );


            imageView.setImageResource(
                    android.R.drawable.ic_menu_camera
            );
        }
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

        TextView tvSampleStatus;


        Button btnApprove;

        Button btnReject;


        SampleViewHolder(
                @NonNull View itemView
        ) {

            super(
                    itemView
            );


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


            tvSampleStatus =
                    itemView.findViewById(
                            R.id.tvApprovalSampleStatus
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