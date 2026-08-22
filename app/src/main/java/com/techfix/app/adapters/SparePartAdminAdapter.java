package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.SparePart;

import java.util.List;
import java.util.Locale;

public class SparePartAdminAdapter
        extends RecyclerView.Adapter<SparePartAdminAdapter.SparePartViewHolder> {

    public interface OnSparePartActionListener {

        void onEdit(
                SparePart sparePart
        );

        void onDelete(
                SparePart sparePart
        );
    }

    private final List<SparePart> spareParts;

    private final OnSparePartActionListener listener;


    public SparePartAdminAdapter(
            List<SparePart> spareParts,
            OnSparePartActionListener listener
    ) {

        this.spareParts =
                spareParts;

        this.listener =
                listener;
    }


    @NonNull
    @Override
    public SparePartViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_admin_spare_part,
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
                spareParts.get(
                        position
                );

        holder.txtName.setText(
                sparePart.getPartName()
        );

        holder.txtDescription.setText(
                sparePart.getDescription()
        );

        holder.txtCompatibleModels.setText(
                "Compatible: " +
                        sparePart.getCompatibleModels()
        );

        holder.txtPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        sparePart.getPrice()
                )
        );

        holder.btnEdit.setOnClickListener(
                view ->
                        listener.onEdit(
                                sparePart
                        )
        );

        holder.btnDelete.setOnClickListener(
                view ->
                        listener.onDelete(
                                sparePart
                        )
        );
    }


    @Override
    public int getItemCount() {

        return spareParts == null
                ? 0
                : spareParts.size();
    }


    static class SparePartViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtDescription;
        private final TextView txtCompatibleModels;
        private final TextView txtPrice;

        private final Button btnEdit;
        private final Button btnDelete;


        public SparePartViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtName =
                    itemView.findViewById(
                            R.id.txtAdminPartName
                    );

            txtDescription =
                    itemView.findViewById(
                            R.id.txtAdminPartDescription
                    );

            txtCompatibleModels =
                    itemView.findViewById(
                            R.id.txtAdminPartCompatibleModels
                    );

            txtPrice =
                    itemView.findViewById(
                            R.id.txtAdminPartPrice
                    );

            btnEdit =
                    itemView.findViewById(
                            R.id.btnEditSparePart
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDeleteSparePart
                    );
        }
    }
}