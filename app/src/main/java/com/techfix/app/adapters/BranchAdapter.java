package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.Branch;

import java.util.List;

public class BranchAdapter
        extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    private final List<Branch> branchList;

    public BranchAdapter(List<Branch> branchList) {
        this.branchList = branchList;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_branch,
                        parent,
                        false
                );

        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BranchViewHolder holder,
            int position
    ) {

        Branch branch = branchList.get(position);

        holder.txtBranchName.setText(
                branch.getBranchName()
        );

        holder.txtBranchAddress.setText(
                branch.getAddress()
        );
    }

    @Override
    public int getItemCount() {
        return branchList.size();
    }

    public static class BranchViewHolder
            extends RecyclerView.ViewHolder {

        private final TextView txtBranchName;
        private final TextView txtBranchAddress;

        public BranchViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            txtBranchName =
                    itemView.findViewById(
                            R.id.txtBranchName
                    );

            txtBranchAddress =
                    itemView.findViewById(
                            R.id.txtBranchAddress
                    );
        }
    }
}