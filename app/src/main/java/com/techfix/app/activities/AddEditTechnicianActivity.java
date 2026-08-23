package com.techfix.app.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.database.BranchDao;
import com.techfix.app.database.TechnicianDao;
import com.techfix.app.database.UserDao;
import com.techfix.app.models.Branch;
import com.techfix.app.models.Technician;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.utils.RepairCategoryUtils;

import java.util.ArrayList;
import java.util.List;

public class AddEditTechnicianActivity
        extends AppCompatActivity {

    private TextView txtTechnicianFormTitle;


    private EditText edtTechnicianName;

    private EditText edtTechnicianPhone;

    private EditText edtTechnicianEmail;

    private EditText edtTechnicianPassword;


    private LinearLayout layoutMobileSpecializations;

    private LinearLayout layoutComputerSpecializations;


    private Spinner spinnerBranch;


    private Switch switchAvailable;


    private Button btnSaveTechnician;


    private TechnicianDao technicianDao;

    private BranchDao branchDao;

    private UserDao userDao;


    private List<Branch> branchList =
            new ArrayList<>();


    private final List<CheckBox> mobileCheckBoxes =
            new ArrayList<>();


    private final List<CheckBox> computerCheckBoxes =
            new ArrayList<>();


    private List<String> existingMobileSpecializations =
            new ArrayList<>();


    private List<String> existingComputerSpecializations =
            new ArrayList<>();


    private int technicianId =
            -1;


    private int existingBranchId =
            -1;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_add_edit_technician
        );


        bindViews();


        technicianDao =
                new TechnicianDao(this);


        branchDao =
                new BranchDao(this);


        userDao =
                new UserDao(this);


        loadIntentData();


        setupSpecializationCheckBoxes();


        setupBranchSpinner();


        btnSaveTechnician.setOnClickListener(
                view -> validateAndSave()
        );
    }


    private void bindViews() {

        txtTechnicianFormTitle =
                findViewById(
                        R.id.txtTechnicianFormTitle
                );


        edtTechnicianName =
                findViewById(
                        R.id.edtTechnicianName
                );


        edtTechnicianPhone =
                findViewById(
                        R.id.edtTechnicianPhone
                );


        edtTechnicianEmail =
                findViewById(
                        R.id.edtTechnicianEmail
                );


        edtTechnicianPassword =
                findViewById(
                        R.id.edtTechnicianPassword
                );


        layoutMobileSpecializations =
                findViewById(
                        R.id.layoutMobileSpecializations
                );


        layoutComputerSpecializations =
                findViewById(
                        R.id.layoutComputerSpecializations
                );


        spinnerBranch =
                findViewById(
                        R.id.spinnerBranch
                );


        switchAvailable =
                findViewById(
                        R.id.switchAvailable
                );


        btnSaveTechnician =
                findViewById(
                        R.id.btnSaveTechnician
                );
    }


    private void loadIntentData() {

        if (
                !getIntent().hasExtra(
                        "technician_id"
                )
        ) {

            return;
        }


        technicianId =
                getIntent().getIntExtra(
                        "technician_id",
                        -1
                );


        Technician technician =
                technicianDao.getTechnicianById(
                        technicianId
                );


        if (technician == null) {

            Toast.makeText(
                    this,
                    "Technician not found",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

            return;
        }


        txtTechnicianFormTitle.setText(
                "Edit Technician"
        );


        btnSaveTechnician.setText(
                "Update Technician"
        );


        edtTechnicianName.setText(
                technician.getName()
        );


        edtTechnicianPhone.setText(
                technician.getPhone()
        );


        switchAvailable.setChecked(
                technician.isAvailable()
        );


        existingMobileSpecializations =
                technician
                        .getMobileSpecializations();


        existingComputerSpecializations =
                technician
                        .getComputerSpecializations();


        existingBranchId =
                technician.getBranchId();


        /*
         * Login information is not edited here.
         */
        edtTechnicianEmail.setEnabled(
                false
        );


        edtTechnicianPassword.setEnabled(
                false
        );


        edtTechnicianEmail.setHint(
                "Login account already created"
        );


        edtTechnicianPassword.setHint(
                "Use change password later"
        );
    }


    private void setupSpecializationCheckBoxes() {

        layoutMobileSpecializations
                .removeAllViews();


        layoutComputerSpecializations
                .removeAllViews();


        mobileCheckBoxes.clear();

        computerCheckBoxes.clear();


        for (
                String category
                :
                RepairCategoryUtils
                        .getMobileCategories()
        ) {

            CheckBox checkBox =
                    createSpecializationCheckBox(
                            category
                    );


            checkBox.setChecked(
                    existingMobileSpecializations
                            .contains(
                                    category
                            )
            );


            layoutMobileSpecializations
                    .addView(
                            checkBox
                    );


            mobileCheckBoxes.add(
                    checkBox
            );
        }


        for (
                String category
                :
                RepairCategoryUtils
                        .getComputerCategories()
        ) {

            CheckBox checkBox =
                    createSpecializationCheckBox(
                            category
                    );


            checkBox.setChecked(
                    existingComputerSpecializations
                            .contains(
                                    category
                            )
            );


            layoutComputerSpecializations
                    .addView(
                            checkBox
                    );


            computerCheckBoxes.add(
                    checkBox
            );
        }
    }


    private CheckBox createSpecializationCheckBox(
            String category
    ) {

        CheckBox checkBox =
                new CheckBox(this);


        checkBox.setText(
                category
        );


        checkBox.setTag(
                category
        );


        checkBox.setPadding(
                4,
                8,
                4,
                8
        );


        return checkBox;
    }


    private List<String> getSelectedMobileSpecializations() {

        List<String> selected =
                new ArrayList<>();


        for (
                CheckBox checkBox
                :
                mobileCheckBoxes
        ) {

            if (checkBox.isChecked()) {

                selected.add(
                        checkBox
                                .getTag()
                                .toString()
                );
            }
        }


        return selected;
    }


    private List<String> getSelectedComputerSpecializations() {

        List<String> selected =
                new ArrayList<>();


        for (
                CheckBox checkBox
                :
                computerCheckBoxes
        ) {

            if (checkBox.isChecked()) {

                selected.add(
                        checkBox
                                .getTag()
                                .toString()
                );
            }
        }


        return selected;
    }


    private void setupBranchSpinner() {

        branchList =
                branchDao.getAllBranches();


        List<String> names =
                new ArrayList<>();


        for (
                Branch branch
                :
                branchList
        ) {

            names.add(
                    branch.getBranchName()
            );
        }


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        names
                );


        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerBranch.setAdapter(
                adapter
        );


        if (existingBranchId != -1) {

            for (
                    int i = 0;
                    i < branchList.size();
                    i++
            ) {

                if (
                        branchList
                                .get(i)
                                .getBranchId()
                                ==
                                existingBranchId
                ) {

                    spinnerBranch.setSelection(
                            i
                    );

                    break;
                }
            }
        }
    }


    private void validateAndSave() {

        String name =
                edtTechnicianName
                        .getText()
                        .toString()
                        .trim();


        String phone =
                edtTechnicianPhone
                        .getText()
                        .toString()
                        .trim();


        if (name.isEmpty()) {

            edtTechnicianName.setError(
                    "Name is required"
            );

            return;
        }


        if (phone.isEmpty()) {

            edtTechnicianPhone.setError(
                    "Phone is required"
            );

            return;
        }


        if (branchList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No branches available",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        List<String> mobileSpecializations =
                getSelectedMobileSpecializations();


        List<String> computerSpecializations =
                getSelectedComputerSpecializations();


        /*
         * Technician may have Mobile only,
         * Computer only,
         * or both.
         *
         * But at least one specialization is required.
         */
        if (
                mobileSpecializations.isEmpty()
                        &&
                        computerSpecializations.isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Select at least one technician specialization",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (technicianId == -1) {

            String email =
                    edtTechnicianEmail
                            .getText()
                            .toString()
                            .trim();


            String password =
                    edtTechnicianPassword
                            .getText()
                            .toString();


            if (email.isEmpty()) {

                edtTechnicianEmail.setError(
                        "Email is required"
                );

                return;
            }


            if (password.isEmpty()) {

                edtTechnicianPassword.setError(
                        "Password is required"
                );

                return;
            }


            if (
                    userDao.isEmailRegistered(
                            email
                    )
            ) {

                edtTechnicianEmail.setError(
                        "Email already registered"
                );

                return;
            }
        }


        Branch branch =
                branchList.get(
                        spinnerBranch
                                .getSelectedItemPosition()
                );


        Technician technician =
                new Technician(
                        technicianId,
                        name,
                        phone,
                        mobileSpecializations,
                        computerSpecializations,
                        switchAvailable.isChecked(),
                        branch.getBranchId()
                );


        if (technicianId == -1) {

            insertTechnicianAndAccount(
                    technician
            );

        } else {

            confirmUpdate(
                    technician
            );
        }
    }


    private void insertTechnicianAndAccount(
            Technician technician
    ) {

        long technicianResult =
                technicianDao.insertTechnician(
                        technician
                );


        if (technicianResult <= 0) {

            Toast.makeText(
                    this,
                    "Unable to add technician",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        User user =
                new User();


        user.setName(
                technician.getName()
        );


        user.setEmail(
                edtTechnicianEmail
                        .getText()
                        .toString()
                        .trim()
        );


        user.setPhone(
                technician.getPhone()
        );


        user.setPassword(
                edtTechnicianPassword
                        .getText()
                        .toString()
        );


        user.setRole(
                User.ROLE_TECHNICIAN
        );


        user.setTechnicianId(
                (int) technicianResult
        );


        long userResult =
                userDao.insertUser(
                        user
                );


        if (userResult > 0) {

            Toast.makeText(
                    this,
                    "Technician account created successfully",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

        } else {

            technicianDao.deleteTechnician(
                    (int) technicianResult
            );


            Toast.makeText(
                    this,
                    "Unable to create technician login account",
                    Toast.LENGTH_LONG
            ).show();
        }
    }


    private void confirmUpdate(
            Technician technician
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Confirm Update"
                )
                .setMessage(
                        "Save technician changes?"
                )
                .setPositiveButton(
                        "Update",
                        (dialog, which) ->
                                updateTechnician(
                                        technician
                                )
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }


    private void updateTechnician(
            Technician technician
    ) {

        int result =
                technicianDao.updateTechnician(
                        technician
                );


        if (result > 0) {

            Toast.makeText(
                    this,
                    "Technician updated",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update technician",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}