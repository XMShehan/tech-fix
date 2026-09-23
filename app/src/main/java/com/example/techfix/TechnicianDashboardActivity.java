package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TechnicianDashboardActivity extends AppCompatActivity {

    TextView txtWelcome;
    TextView txtTechnicianInfo;

    Button btnMyJobs;
    Button btnJobHistory;
    Button btnLogout;

    String technicianId;
    String technicianName;
    String technicianEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_technician_dashboard
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // =====================================================
        // FIND VIEWS
        // =====================================================

        txtWelcome =
                findViewById(R.id.txtWelcome);

        txtTechnicianInfo =
                findViewById(R.id.txtTechnicianInfo);

        btnMyJobs =
                findViewById(R.id.btnMyJobs);

        btnJobHistory =
                findViewById(R.id.btnJobHistory);

        btnLogout =
                findViewById(R.id.btnLogout);

        // =====================================================
        // GET TECHNICIAN INFORMATION
        // =====================================================

        technicianId =
                getIntent().getStringExtra(
                        "technicianId"
                );

        technicianName =
                getIntent().getStringExtra(
                        "technicianName"
                );

        technicianEmail =
                getIntent().getStringExtra(
                        "technicianEmail"
                );

        // =====================================================
        // DISPLAY TECHNICIAN INFORMATION
        // =====================================================

        if (technicianName != null &&
                !technicianName.trim().isEmpty()) {

            txtWelcome.setText(
                    "Welcome, " +
                            technicianName
            );
        }

        String info =
                "Technician ID: " +
                        (
                                technicianId != null
                                        ? technicianId
                                        : ""
                        ) +
                        "\nEmail: " +
                        (
                                technicianEmail != null
                                        ? technicianEmail
                                        : ""
                        );

        txtTechnicianInfo.setText(
                info
        );

        // =====================================================
        // MY JOBS
        // =====================================================

        btnMyJobs.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TechnicianDashboardActivity.this,
                            MyJobsActivity.class
                    );

            intent.putExtra(
                    "technicianId",
                    technicianId
            );

            intent.putExtra(
                    "technicianName",
                    technicianName
            );

            startActivity(intent);
        });

        // =====================================================
        // JOB HISTORY
        // =====================================================

        btnJobHistory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TechnicianDashboardActivity.this,
                            MyJobsActivity.class
                    );

            intent.putExtra(
                    "technicianId",
                    technicianId
            );

            intent.putExtra(
                    "technicianName",
                    technicianName
            );

            intent.putExtra(
                    "showHistory",
                    true
            );

            startActivity(intent);
        });

        // =====================================================
        // LOGOUT
        // =====================================================

        btnLogout.setOnClickListener(v -> {

            showLogoutConfirmation();
        });
    }

    // =========================================================
    // LOGOUT CONFIRMATION
    // =========================================================

    private void showLogoutConfirmation() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Logout"
                )

                .setMessage(
                        "Are you sure you want to logout?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Logout",
                        (dialog, which) -> logout()
                )

                .show();
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        /*
         * Clear the current activity stack and return
         * to the common Login screen.
         *
         * This prevents the technician from pressing
         * Back and returning to the dashboard.
         */

        Intent intent =
                new Intent(
                        TechnicianDashboardActivity.this,
                        LoginActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}