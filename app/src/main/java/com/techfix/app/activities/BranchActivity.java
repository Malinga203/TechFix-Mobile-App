package com.techfix.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techfix.app.R;
import com.techfix.app.adapters.BranchAdapter;
import com.techfix.app.database.BranchDao;
import com.techfix.app.models.Branch;

import java.util.List;

public class BranchActivity extends AppCompatActivity {

    private RecyclerView recyclerBranches;
    private BranchDao branchDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

        recyclerBranches =
                findViewById(R.id.recyclerBranches);

        recyclerBranches.setLayoutManager(
                new LinearLayoutManager(this)
        );

        branchDao = new BranchDao(this);

        loadBranches();
    }

    private void loadBranches() {

        List<Branch> branches =
                branchDao.getAllBranches();

        BranchAdapter adapter =
                new BranchAdapter(branches);

        recyclerBranches.setAdapter(adapter);
    }
}