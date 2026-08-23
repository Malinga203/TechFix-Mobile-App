package com.techfix.app.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.techfix.app.R;
import com.techfix.app.models.SparePart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BranchInventoryAdapter
        extends RecyclerView.Adapter<BranchInventoryAdapter.InventoryViewHolder> {

    // =========================================================
    // LISTENER
    // =========================================================

    public interface OnInventoryActionListener {

        void onUpdateStock(
                SparePart sparePart,
                int currentQuantity,
                int newQuantity
        );
    }


    // =========================================================
    // DATA
    // =========================================================

    private final List<SparePart> spareParts =
            new ArrayList<>();


    /*
     * key   = partId
     * value = quantity at currently selected branch
     */
    private final Map<Integer, Integer> stockMap =
            new HashMap<>();


    private final OnInventoryActionListener listener;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BranchInventoryAdapter(
            OnInventoryActionListener listener
    ) {

        this.listener =
                listener;
    }


    // =========================================================
    // SET DATA
    // =========================================================

    public void setData(
            List<SparePart> newSpareParts,
            Map<Integer, Integer> newStockMap
    ) {

        spareParts.clear();

        stockMap.clear();


        if (newSpareParts != null) {

            spareParts.addAll(
                    newSpareParts
            );
        }


        if (newStockMap != null) {

            stockMap.putAll(
                    newStockMap
            );
        }


        notifyDataSetChanged();
    }


    // =========================================================
    // CREATE HOLDER
    // =========================================================

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_branch_inventory,
                                parent,
                                false
                        );


        return new InventoryViewHolder(
                view
        );
    }


    // =========================================================
    // BIND
    // =========================================================

    @Override
    public void onBindViewHolder(
            @NonNull InventoryViewHolder holder,
            int position
    ) {

        SparePart sparePart =
                spareParts.get(
                        position
                );


        int currentQuantity =
                stockMap.containsKey(
                        sparePart.getPartId()
                )
                        ? stockMap.get(
                        sparePart.getPartId()
                )
                        : 0;


        // =====================================================
        // PART INFORMATION
        // =====================================================

        holder.txtPartName.setText(
                sparePart.getPartName()
        );


        String compatibleModels =
                sparePart.getCompatibleModels();


        if (
                compatibleModels == null ||
                        compatibleModels.trim().isEmpty()
        ) {

            holder.txtCompatibleModels.setText(
                    "Compatible models not specified"
            );

        } else {

            holder.txtCompatibleModels.setText(
                    "Compatible: " +
                            compatibleModels
            );
        }


        holder.txtPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        sparePart.getPrice()
                )
        );


        // =====================================================
        // CURRENT DATABASE STOCK
        // =====================================================

        holder.txtCurrentStock.setText(
                "Current Stock: " +
                        currentQuantity
        );


        // =====================================================
        // REMOVE PREVIOUS WATCHER
        // RecyclerView reuses item views.
        // =====================================================

        if (
                holder.stockTextWatcher != null
        ) {

            holder.edtStock
                    .removeTextChangedListener(
                            holder.stockTextWatcher
                    );
        }


        // =====================================================
        // SET EDITABLE QUANTITY
        // =====================================================

        holder.edtStock.setText(
                String.valueOf(
                        currentQuantity
                )
        );


        holder.edtStock.setSelection(
                holder.edtStock
                        .getText()
                        .length()
        );


        updateStockStatus(
                holder,
                currentQuantity
        );


        // =====================================================
        // WATCH MANUAL ENTRY
        // =====================================================

        holder.stockTextWatcher =
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        int quantity =
                                readQuantity(
                                        holder.edtStock
                                );


                        updateStockStatus(
                                holder,
                                quantity
                        );
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                };


        holder.edtStock
                .addTextChangedListener(
                        holder.stockTextWatcher
                );


        // =====================================================
        // PLUS
        // Only changes EditText.
        // DOES NOT update database.
        // =====================================================

        holder.btnIncrease
                .setOnClickListener(
                        view -> {

                            int quantity =
                                    readQuantity(
                                            holder.edtStock
                                    );


                            /*
                             * maxLength in XML is 6,
                             * so prevent going over 999999.
                             */
                            if (
                                    quantity >= 999999
                            ) {

                                holder.edtStock.setError(
                                        "Maximum quantity is 999999"
                                );

                                return;
                            }


                            quantity++;


                            setQuantityText(
                                    holder,
                                    quantity
                            );
                        }
                );


        // =====================================================
        // MINUS
        // Only changes EditText.
        // DOES NOT update database.
        // =====================================================

        holder.btnDecrease
                .setOnClickListener(
                        view -> {

                            int quantity =
                                    readQuantity(
                                            holder.edtStock
                                    );


                            if (
                                    quantity <= 0
                            ) {

                                return;
                            }


                            quantity--;


                            setQuantityText(
                                    holder,
                                    quantity
                            );
                        }
                );


        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        holder.btnUpdate
                .setOnClickListener(
                        view -> {

                            String quantityText =
                                    holder.edtStock
                                            .getText()
                                            .toString()
                                            .trim();


                            // ---------------------------------
                            // EMPTY
                            // ---------------------------------

                            if (
                                    quantityText.isEmpty()
                            ) {

                                holder.edtStock.setError(
                                        "Enter stock quantity"
                                );

                                holder.edtStock.requestFocus();

                                return;
                            }


                            int newQuantity;


                            // ---------------------------------
                            // NUMBER
                            // ---------------------------------

                            try {

                                newQuantity =
                                        Integer.parseInt(
                                                quantityText
                                        );


                            } catch (
                                    NumberFormatException exception
                            ) {

                                holder.edtStock.setError(
                                        "Enter a valid number"
                                );

                                holder.edtStock.requestFocus();

                                return;
                            }


                            // ---------------------------------
                            // NEGATIVE
                            // ---------------------------------

                            if (
                                    newQuantity < 0
                            ) {

                                holder.edtStock.setError(
                                        "Quantity cannot be negative"
                                );

                                holder.edtStock.requestFocus();

                                return;
                            }


                            // ---------------------------------
                            // MAX
                            // ---------------------------------

                            if (
                                    newQuantity > 999999
                            ) {

                                holder.edtStock.setError(
                                        "Maximum quantity is 999999"
                                );

                                holder.edtStock.requestFocus();

                                return;
                            }


                            // ---------------------------------
                            // NO CHANGE
                            // ---------------------------------

                            if (
                                    newQuantity ==
                                            currentQuantity
                            ) {

                                holder.edtStock.setError(
                                        "Stock quantity has not changed"
                                );

                                return;
                            }


                            // ---------------------------------
                            // SEND TO ACTIVITY
                            // Activity will ask for confirmation.
                            // ---------------------------------

                            if (
                                    listener != null
                            ) {

                                listener.onUpdateStock(
                                        sparePart,
                                        currentQuantity,
                                        newQuantity
                                );
                            }
                        }
                );
    }


    // =========================================================
    // SET QUANTITY IN EDIT TEXT
    // =========================================================

    private void setQuantityText(
            InventoryViewHolder holder,
            int quantity
    ) {

        holder.edtStock.setText(
                String.valueOf(
                        quantity
                )
        );


        holder.edtStock.setSelection(
                holder.edtStock
                        .getText()
                        .length()
        );
    }


    // =========================================================
    // READ QUANTITY
    // =========================================================

    private int readQuantity(
            EditText editText
    ) {

        String value =
                editText
                        .getText()
                        .toString()
                        .trim();


        if (
                value.isEmpty()
        ) {

            return 0;
        }


        try {

            return Integer.parseInt(
                    value
            );


        } catch (
                NumberFormatException exception
        ) {

            return 0;
        }
    }


    // =========================================================
    // UPDATE DISPLAY STATUS
    // =========================================================

    private void updateStockStatus(
            InventoryViewHolder holder,
            int quantity
    ) {

        if (
                quantity > 0
        ) {

            holder.txtStockStatus.setText(
                    "Available"
            );


            holder.btnDecrease.setEnabled(
                    true
            );


        } else {

            holder.txtStockStatus.setText(
                    "Out of Stock"
            );


            holder.btnDecrease.setEnabled(
                    false
            );
        }
    }


    // =========================================================
    // COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return spareParts.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class InventoryViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtPartName;

        TextView txtCompatibleModels;

        TextView txtPrice;

        TextView txtCurrentStock;

        TextView txtStockStatus;


        EditText edtStock;


        MaterialButton btnDecrease;

        MaterialButton btnIncrease;

        MaterialButton btnUpdate;


        TextWatcher stockTextWatcher;


        public InventoryViewHolder(
                @NonNull View itemView
        ) {

            super(
                    itemView
            );


            txtPartName =
                    itemView.findViewById(
                            R.id.txtInventoryPartName
                    );


            txtCompatibleModels =
                    itemView.findViewById(
                            R.id.txtInventoryCompatibleModels
                    );


            txtPrice =
                    itemView.findViewById(
                            R.id.txtInventoryPartPrice
                    );


            txtCurrentStock =
                    itemView.findViewById(
                            R.id.txtInventoryCurrentStock
                    );


            txtStockStatus =
                    itemView.findViewById(
                            R.id.txtInventoryStatus
                    );


            edtStock =
                    itemView.findViewById(
                            R.id.edtInventoryStock
                    );


            btnDecrease =
                    itemView.findViewById(
                            R.id.btnDecreaseInventory
                    );


            btnIncrease =
                    itemView.findViewById(
                            R.id.btnIncreaseInventory
                    );


            btnUpdate =
                    itemView.findViewById(
                            R.id.btnSaveInventoryStock
                    );
        }
    }
}