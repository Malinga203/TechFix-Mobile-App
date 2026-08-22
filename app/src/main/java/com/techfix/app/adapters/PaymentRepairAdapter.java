package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.Repair;

import java.util.List;
import java.util.Locale;

public class PaymentRepairAdapter
        extends RecyclerView.Adapter<PaymentRepairAdapter.PaymentRepairViewHolder> {

    public interface OnPaymentClickListener {

        void onPaymentClick(
                Repair repair
        );
    }

    private final List<Repair> repairs;

    private final OnPaymentClickListener listener;


    public PaymentRepairAdapter(
            List<Repair> repairs,
            OnPaymentClickListener listener
    ) {

        this.repairs =
                repairs;

        this.listener =
                listener;
    }


    @NonNull
    @Override
    public PaymentRepairViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_payment_repair,
                                parent,
                                false
                        );

        return new PaymentRepairViewHolder(
                view
        );
    }


    @Override
    public void onBindViewHolder(
            @NonNull PaymentRepairViewHolder holder,
            int position
    ) {

        Repair repair =
                repairs.get(
                        position
                );


        // =====================================================
        // REPAIR ID
        // =====================================================

        holder.txtPaymentRepairId.setText(
                "Repair #" +
                        repair.getRepairId()
        );


        // =====================================================
        // DEVICE
        // =====================================================

        holder.txtPaymentDevice.setText(
                repair.getDeviceName()
        );


        // =====================================================
        // SERVICE
        // =====================================================

        holder.txtPaymentService.setText(
                repair.getServiceName()
        );


        // =====================================================
        // STATUS
        // =====================================================

        holder.txtPaymentRepairStatus.setText(
                repair.getReadableStatus()
        );


        // =====================================================
        // AMOUNT
        // =====================================================

        holder.txtPaymentAmount.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        repair.getFinalCost()
                )
        );


        // =====================================================
        // PAY BUTTON
        // =====================================================

        holder.btnPayRepair.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onPaymentClick(
                                repair
                        );
                    }
                }
        );
    }


    @Override
    public int getItemCount() {

        return repairs == null
                ? 0
                : repairs.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class PaymentRepairViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtPaymentRepairId;

        private final TextView txtPaymentDevice;

        private final TextView txtPaymentService;

        private final TextView txtPaymentRepairStatus;

        private final TextView txtPaymentAmount;

        private final Button btnPayRepair;


        public PaymentRepairViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            txtPaymentRepairId =
                    itemView.findViewById(
                            R.id.txtPaymentRepairId
                    );

            txtPaymentDevice =
                    itemView.findViewById(
                            R.id.txtPaymentDevice
                    );

            txtPaymentService =
                    itemView.findViewById(
                            R.id.txtPaymentService
                    );

            txtPaymentRepairStatus =
                    itemView.findViewById(
                            R.id.txtPaymentRepairStatus
                    );

            txtPaymentAmount =
                    itemView.findViewById(
                            R.id.txtPaymentAmount
                    );

            btnPayRepair =
                    itemView.findViewById(
                            R.id.btnPayRepair
                    );
        }
    }
}