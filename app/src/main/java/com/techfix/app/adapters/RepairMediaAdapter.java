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

import com.google.android.material.chip.Chip;
import com.techfix.app.R;
import com.techfix.app.models.RepairMedia;

import java.util.ArrayList;
import java.util.List;

public class RepairMediaAdapter
        extends RecyclerView.Adapter<RepairMediaAdapter.MediaViewHolder> {

    private final List<RepairMedia> items =
            new ArrayList<>();

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
    public MediaViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_repair_media,
                                parent,
                                false
                        );

        return new MediaViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull MediaViewHolder holder,
            int position
    ) {

        RepairMedia media =
                items.get(position);

        showImage(
                holder.imgMedia,
                media.getImageUri()
        );

        if (media.isSample()) {

            String device =
                    TextUtils.isEmpty(
                            media.getDeviceName()
                    )
                            ? "Repair Sample"
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
                            : device + " • " + service
            );

            holder.chipType.setText(
                    RepairMedia.APPROVAL_APPROVED.equals(
                            media.getApprovalStatus()
                    )
                            ? "Approved Sample"
                            : media.getApprovalStatus()
            );

        } else {

            holder.tvTitle.setText(
                    media.getReadableStage()
                            + " Update"
            );

            holder.chipType.setText(
                    "Progress"
            );
        }

        String caption =
                media.getCaption();

        holder.tvCaption.setText(
                TextUtils.isEmpty(caption)
                        ? "No additional note"
                        : caption
        );

        String technician =
                media.getTechnicianId() > 0
                        ? "Technician #" +
                        media.getTechnicianId()
                        : "Technician";

        String created =
                TextUtils.isEmpty(
                        media.getCreatedAt()
                )
                        ? ""
                        : " • " +
                        media.getCreatedAt();

        holder.tvMeta.setText(
                technician + created
        );
    }

    private void showImage(
            ImageView imageView,
            String uriValue
    ) {

        imageView.setImageURI(null);
        imageView.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        imageView.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        if (TextUtils.isEmpty(uriValue)) {
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

        } catch (Exception ignored) {

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

    static class MediaViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgMedia;
        TextView tvTitle;
        TextView tvCaption;
        TextView tvMeta;
        Chip chipType;

        MediaViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            imgMedia =
                    itemView.findViewById(
                            R.id.imgMedia
                    );

            tvTitle =
                    itemView.findViewById(
                            R.id.tvMediaTitle
                    );

            tvCaption =
                    itemView.findViewById(
                            R.id.tvMediaCaption
                    );

            tvMeta =
                    itemView.findViewById(
                            R.id.tvMediaMeta
                    );

            chipType =
                    itemView.findViewById(
                            R.id.chipMediaType
                    );
        }
    }
}