package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.SparePart;

import java.util.List;
import java.util.Locale;

public class SparePartAdapter extends RecyclerView.Adapter<SparePartAdapter.SparePartViewHolder> {

    public interface OnPartAddListener {
        void onPartAdd(SparePart sparePart);
    }

    private final List<SparePart> sparePartList;
    private final OnPartAddListener listener;

    public SparePartAdapter(
            List<SparePart> sparePartList,
            OnPartAddListener listener
    ) {
        this.sparePartList = sparePartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SparePartViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_spare_part, parent, false);

        return new SparePartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SparePartViewHolder holder,
            int position
    ) {

        SparePart sparePart =
                sparePartList.get(position);

        holder.tvPartName.setText(sparePart.getPartName());

        holder.tvPartDescription.setText(sparePart.getDescription());

        holder.tvPartCompatible.setText(
                holder.itemView.getContext().getString(
                        R.string.label_compatible_with) + " " +
                        sparePart.getCompatibleModels()
        );

        holder.tvPartPrice.setText(
                String.format(Locale.US, "$%.2f", sparePart.getPrice())
        );

        if (sparePart.getStockQuantity() > 0) {

            holder.tvPartStock.setText(
                    holder.itemView.getContext().getString(
                            R.string.label_stock) + " (" + sparePart.getStockQuantity() + ")"
            );

            holder.tvPartStock.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.status_completed
                    )
            );

            holder.btnAddToBooking.setEnabled(true);

        } else {

            holder.tvPartStock.setText(R.string.label_out_of_stock);

            holder.tvPartStock.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.status_pending
                    )
            );

            holder.btnAddToBooking.setEnabled(false);
        }

        holder.btnAddToBooking.setOnClickListener(v ->
                listener.onPartAdd(sparePart)
        );
    }

    @Override
    public int getItemCount() {
        return sparePartList.size();
    }

    static class SparePartViewHolder extends RecyclerView.ViewHolder {

        final TextView tvPartName;
        final TextView tvPartDescription;
        final TextView tvPartCompatible;
        final TextView tvPartPrice;
        final TextView tvPartStock;
        final Button btnAddToBooking;

        SparePartViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPartName =
                    itemView.findViewById(R.id.tv_part_name);

            tvPartDescription =
                    itemView.findViewById(R.id.tv_part_description);

            tvPartCompatible =
                    itemView.findViewById(R.id.tv_part_compatible);

            tvPartPrice =
                    itemView.findViewById(R.id.tv_part_price);

            tvPartStock =
                    itemView.findViewById(R.id.tv_part_stock);

            btnAddToBooking =
                    itemView.findViewById(R.id.btn_add_to_booking);
        }
    }
}
