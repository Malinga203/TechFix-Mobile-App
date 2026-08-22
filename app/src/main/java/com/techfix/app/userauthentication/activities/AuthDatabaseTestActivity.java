package com.techfix.app.userauthentication.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.userauthentication.database.AuthDatabaseHelper;
import com.techfix.app.userauthentication.models.User;

public class AuthDatabaseTestActivity extends AppCompatActivity {

    private static final String TAG = "AUTH_DB_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthDatabaseHelper databaseHelper =
                new AuthDatabaseHelper(this);

        // =====================================================
        // CREATE TEST USER
        // =====================================================

        User testUser = new User(
                "Authentication Test User",
                "auth_test_1@techfix.com",
                "0771234567",
                "123456"
        );

        // =====================================================
        // TEST 1: INSERT USER
        // =====================================================

        long result = databaseHelper.insertUser(testUser);

        if (result != -1) {

            Log.d(
                    TAG,
                    "User inserted successfully. ID: " + result
            );

        } else {

            Log.d(
                    TAG,
                    "User insertion failed."
            );
        }

        // =====================================================
        // TEST 2: CHECK REGISTERED EMAIL
        // =====================================================

        boolean registered = databaseHelper.isEmailRegistered(
                "auth_test_1@techfix.com"
        );

        if (registered) {

            Log.d(
                    TAG,
                    "isEmailRegistered: TRUE"
            );

        } else {

            Log.d(
                    TAG,
                    "isEmailRegistered: FALSE"
            );
        }

        // =====================================================
        // TEST 3: CHECK NON-EXISTING EMAIL
        // =====================================================

        boolean notRegistered = databaseHelper.isEmailRegistered(
                "does_not_exist@techfix.com"
        );

        if (notRegistered) {

            Log.d(
                    TAG,
                    "Non-existing email test: FAILED"
            );

        } else {

            Log.d(
                    TAG,
                    "Non-existing email test: PASSED"
            );
        }

        // =====================================================
        // TEST 4: CORRECT EMAIL + CORRECT PASSWORD
        // =====================================================

        User authenticatedUser =
                databaseHelper.authenticateUser(
                        "auth_test_1@techfix.com",
                        "123456"
                );

        if (authenticatedUser != null) {

            Log.d(
                    TAG,
                    "Authentication SUCCESS"
            );

            Log.d(
                    TAG,
                    "Authenticated user: "
                            + authenticatedUser.getName()
            );

        } else {

            Log.d(
                    TAG,
                    "Authentication FAILED"
            );
        }

        // =====================================================
        // TEST 5: CORRECT EMAIL + WRONG PASSWORD
        // =====================================================

        User wrongPasswordUser =
                databaseHelper.authenticateUser(
                        "auth_test_1@techfix.com",
                        "wrongpassword"
                );

        if (wrongPasswordUser != null) {

            Log.d(
                    TAG,
                    "Wrong password test: FAILED"
            );

        } else {

            Log.d(
                    TAG,
                    "Wrong password test: PASSED"
            );
        }

        databaseHelper.close();
    }
}