package com.techfix.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.models.Branch;

import java.util.List;

public class BranchAdapter
        extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    public interface OnBranchActionListener {

        void onEdit(Branch branch);

        void onDelete(Branch branch);
    }

    private final List<Branch> branchList;
    private final OnBranchActionListener listener;

    public BranchAdapter(
            List<Branch> branchList,
            OnBranchActionListener listener
    ) {

        this.branchList = branchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
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

        Branch branch =
                branchList.get(position);

        holder.txtBranchName.setText(
                branch.getBranchName()
        );

        holder.txtBranchAddress.setText(
                branch.getAddress()
        );

        holder.btnEditBranch
                .setOnClickListener(
                        view ->
                                listener.onEdit(branch)
                );

        holder.btnDeleteBranch
                .setOnClickListener(
                        view ->
                                listener.onDelete(branch)
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

        private final ImageButton btnEditBranch;
        private final ImageButton btnDeleteBranch;

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

            btnEditBranch =
                    itemView.findViewById(
                            R.id.btnEditBranch
                    );

            btnDeleteBranch =
                    itemView.findViewById(
                            R.id.btnDeleteBranch
                    );
        }
    }
}