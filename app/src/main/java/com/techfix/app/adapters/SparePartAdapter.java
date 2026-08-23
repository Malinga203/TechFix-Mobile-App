package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.PartSelection;
import com.techfix.app.models.SparePart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SparePartAdapter
        extends RecyclerView.Adapter<SparePartAdapter.SparePartViewHolder> {

    public interface OnSelectionChangedListener {

        void onSelectionChanged(
                ArrayList<PartSelection> selectedParts
        );
    }

    private static final int MAX_QUANTITY_PER_PART = 99;

    private final List<SparePart> sparePartList;

    private final Map<Integer, Integer> quantityMap =
            new HashMap<>();

    private final OnSelectionChangedListener listener;

    public SparePartAdapter(
            List<SparePart> sparePartList,
            List<PartSelection> initialSelections,
            OnSelectionChangedListener listener
    ) {

        this.sparePartList =
                sparePartList;

        this.listener =
                listener;

        if (initialSelections != null) {

            for (
                    PartSelection selection
                    :
                    initialSelections
            ) {

                if (
                        selection != null
                                &&
                                selection.getPartId() > 0
                                &&
                                selection.getQuantity() > 0
                ) {

                    quantityMap.put(
                            selection.getPartId(),
                            selection.getQuantity()
                    );
                }
            }
        }
    }

    @NonNull
    @Override
    public SparePartViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_spare_part,
                                parent,
                                false
                        );

        return new SparePartViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull SparePartViewHolder holder,
            int position
    ) {

        SparePart sparePart =
                sparePartList.get(
                        position
                );

        holder.tvPartName.setText(
                sparePart.getPartName()
        );

        holder.tvPartDescription.setText(
                sparePart.getDescription()
        );

        holder.tvPartCompatible.setText(
                holder.itemView
                        .getContext()
                        .getString(
                                R.string.label_compatible_with
                        )
                        + " "
                        + sparePart.getCompatibleModels()
        );

        holder.tvPartPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        sparePart.getPrice()
                )
        );

        int quantity =
                getQuantity(
                        sparePart.getPartId()
                );

        holder.tvQuantity.setText(
                String.valueOf(
                        quantity
                )
        );

        holder.btnMinus.setEnabled(
                quantity > 0
        );

        holder.tvSelectedStatus.setVisibility(
                quantity > 0
                        ? View.VISIBLE
                        : View.GONE
        );

        holder.tvSelectedStatus.setText(
                quantity > 0
                        ?
                        "Selected • Qty " +
                                quantity
                        :
                        ""
        );

        holder.btnMinus.setOnClickListener(
                view ->
                        changeQuantity(
                                sparePart,
                                -1
                        )
        );

        holder.btnPlus.setOnClickListener(
                view ->
                        changeQuantity(
                                sparePart,
                                1
                        )
        );
    }

    private void changeQuantity(
            SparePart sparePart,
            int change
    ) {

        int partId =
                sparePart.getPartId();

        int current =
                getQuantity(
                        partId
                );

        int updated =
                current + change;

        if (updated < 0) {
            updated = 0;
        }

        if (
                updated >
                        MAX_QUANTITY_PER_PART
        ) {

            updated =
                    MAX_QUANTITY_PER_PART;
        }

        if (updated == 0) {

            quantityMap.remove(
                    partId
            );

        } else {

            quantityMap.put(
                    partId,
                    updated
            );
        }

        notifyDataSetChanged();

        notifySelectionChanged();
    }

    public int getQuantity(
            int partId
    ) {

        Integer quantity =
                quantityMap.get(
                        partId
                );

        return quantity == null
                ? 0
                : quantity;
    }

    public ArrayList<PartSelection> getSelectedParts() {

        ArrayList<PartSelection> result =
                new ArrayList<>();

        for (
                SparePart sparePart
                :
                sparePartList
        ) {

            int quantity =
                    getQuantity(
                            sparePart.getPartId()
                    );

            if (quantity <= 0) {
                continue;
            }

            result.add(
                    new PartSelection(
                            sparePart.getPartId(),
                            sparePart.getPartName(),
                            sparePart.getPrice(),
                            quantity
                    )
            );
        }

        return result;
    }

    public void clearSelections() {

        quantityMap.clear();

        notifyDataSetChanged();

        notifySelectionChanged();
    }

    private void notifySelectionChanged() {

        if (listener != null) {

            listener.onSelectionChanged(
                    getSelectedParts()
            );
        }
    }

    @Override
    public int getItemCount() {

        return sparePartList.size();
    }

    static class SparePartViewHolder
            extends RecyclerView.ViewHolder {

        final TextView tvPartName;
        final TextView tvPartDescription;
        final TextView tvPartCompatible;
        final TextView tvPartPrice;
        final TextView tvQuantity;
        final TextView tvSelectedStatus;

        final Button btnMinus;
        final Button btnPlus;

        SparePartViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvPartName =
                    itemView.findViewById(
                            R.id.tv_part_name
                    );

            tvPartDescription =
                    itemView.findViewById(
                            R.id.tv_part_description
                    );

            tvPartCompatible =
                    itemView.findViewById(
                            R.id.tv_part_compatible
                    );

            tvPartPrice =
                    itemView.findViewById(
                            R.id.tv_part_price
                    );

            tvQuantity =
                    itemView.findViewById(
                            R.id.tv_part_quantity
                    );

            tvSelectedStatus =
                    itemView.findViewById(
                            R.id.tv_part_selected_status
                    );

            btnMinus =
                    itemView.findViewById(
                            R.id.btn_part_minus
                    );

            btnPlus =
                    itemView.findViewById(
                            R.id.btn_part_plus
                    );
        }
    }
}
