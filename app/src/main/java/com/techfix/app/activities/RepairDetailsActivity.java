package com.techfix.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.techfix.app.R;
import com.techfix.app.database.RepairDAO;
import com.techfix.app.models.Repair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RepairDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_REPAIR_ID =
            "repair_id";

    private RepairDAO repairDAO;
    private Repair repair;

    private long repairId;

    private ImageView imgRepair;
    private Chip chipStatus;

    private TextView tvRepairId;
    private TextView tvDevice;
    private TextView tvService;
    private TextView tvProblem;
    private TextView tvTechnician;
    private TextView tvRepairDate;
    private TextView tvUpdatedAt;
    private TextView tvCostLabel;
    private TextView tvCost;
    private TextView tvProgress;

    private ProgressBar progressRepair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_repair_details
        );

        repairId =
                getIntent().getLongExtra(
                        EXTRA_REPAIR_ID,
                        -1
                );

        if (repairId <= 0) {

            Toast.makeText(
                    this,
                    "Invalid repair record",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        MaterialToolbar toolbar =
                findViewById(
                        R.id.toolbarRepairDetails
                );

        imgRepair =
                findViewById(
                        R.id.imgDetailRepair
                );

        chipStatus =
                findViewById(
                        R.id.chipDetailStatus
                );

        tvRepairId =
                findViewById(
                        R.id.tvDetailRepairId
                );

        tvDevice =
                findViewById(
                        R.id.tvDetailDevice
                );

        tvService =
                findViewById(
                        R.id.tvDetailService
                );

        tvProblem =
                findViewById(
                        R.id.tvDetailProblem
                );

        tvTechnician =
                findViewById(
                        R.id.tvDetailTechnician
                );

        tvRepairDate =
                findViewById(
                        R.id.tvDetailRepairDate
                );

        tvUpdatedAt =
                findViewById(
                        R.id.tvDetailUpdatedAt
                );

        tvCostLabel =
                findViewById(
                        R.id.tvDetailCostLabel
                );

        tvCost =
                findViewById(
                        R.id.tvDetailCost
                );

        tvProgress =
                findViewById(
                        R.id.tvDetailProgress
                );

        progressRepair =
                findViewById(
                        R.id.progressDetailRepair
                );

        Button btnProgressPhotos =
                findViewById(
                        R.id.btnViewProgressPhotos
                );

        repairDAO =
                new RepairDAO(this);

        toolbar.setNavigationOnClickListener(
                view -> finish()
        );

        btnProgressPhotos.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    RepairDetailsActivity.this,
                                    RepairProgressActivity.class
                            );

                    intent.putExtra(
                            RepairProgressActivity.EXTRA_REPAIR_ID,
                            repairId
                    );

                    startActivity(intent);
                }
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadRepair();
    }

    private void loadRepair() {

        repair =
                repairDAO.getRepairById(
                        repairId
                );

        if (repair == null) {

            Toast.makeText(
                    this,
                    "Repair record not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        bindRepair();
    }

    private void bindRepair() {

        tvRepairId.setText(
                String.format(
                        Locale.getDefault(),
                        "R-%03d",
                        repair.getRepairId()
                )
        );

        chipStatus.setText(
                repair.getReadableStatus()
        );

        tvDevice.setText(
                safeText(
                        repair.getDeviceName(),
                        "Unknown device"
                )
        );

        tvService.setText(
                safeText(
                        repair.getServiceName(),
                        "Unknown service"
                )
        );

        tvProblem.setText(
                safeText(
                        repair.getProblemDescription(),
                        "No issue description"
                )
        );

        tvTechnician.setText(
                repair.getTechnicianId() > 0
                        ? "Technician #" + repair.getTechnicianId()
                        : "Not assigned yet"
        );

        tvRepairDate.setText(
                formatDate(
                        repair.getCreatedAt()
                )
        );

        tvUpdatedAt.setText(
                formatDateTime(
                        repair.getUpdatedAt()
                )
        );

        int progress =
                repair.getStatusProgress();

        progressRepair.setProgress(
                progress
        );

        tvProgress.setText(
                String.format(
                        Locale.getDefault(),
                        "%d%%",
                        progress
                )
        );

        if (repair.isCompleted()) {

            tvCostLabel.setText(
                    "Final cost"
            );

            tvCost.setText(
                    formatMoney(
                            repair.getFinalCost()
                    )
            );

        } else {

            tvCostLabel.setText(
                    "Estimated cost"
            );

            tvCost.setText(
                    formatMoney(
                            repair.getEstimatedCost()
                    )
            );
        }

        showRepairImage();
    }

    private void showRepairImage() {

        imgRepair.setImageURI(null);

        imgRepair.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );

        imgRepair.setImageResource(
                android.R.drawable.ic_menu_camera
        );

        if (TextUtils.isEmpty(
                repair.getImageUri()
        )) {
            return;
        }

        try {

            imgRepair.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );

            imgRepair.setImageURI(
                    Uri.parse(
                            repair.getImageUri()
                    )
            );

        } catch (Exception ignored) {

            imgRepair.setScaleType(
                    ImageView.ScaleType.CENTER_INSIDE
            );

            imgRepair.setImageResource(
                    android.R.drawable.ic_menu_camera
            );
        }
    }

    private String formatMoney(
            double amount
    ) {

        return String.format(
                Locale.getDefault(),
                "Rs. %,.2f",
                Math.max(
                        0,
                        amount
                )
        );
    }

    private String formatDate(
            String value
    ) {

        Date date =
                parseDate(
                        value
                );

        if (date == null) {

            return safeText(
                    value,
                    "-"
            );
        }

        return new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(
                date
        );
    }

    private String formatDateTime(
            String value
    ) {

        Date date =
                parseDate(
                        value
                );

        if (date == null) {

            return safeText(
                    value,
                    "-"
            );
        }

        return new SimpleDateFormat(
                "dd MMM yyyy, h:mm a",
                Locale.getDefault()
        ).format(
                date
        );
    }

    private Date parseDate(
            String value
    ) {

        if (TextUtils.isEmpty(
                value
        )) {
            return null;
        }

        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm"
        };

        for (String pattern : patterns) {

            try {

                return new SimpleDateFormat(
                        pattern,
                        Locale.getDefault()
                ).parse(
                        value
                );

            } catch (ParseException ignored) {

            }
        }

        return null;
    }

    private String safeText(
            String value,
            String fallback
    ) {

        return TextUtils.isEmpty(
                value
        )
                ? fallback
                : value.trim();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (repairDAO != null) {
            repairDAO.close();
        }
    }
}