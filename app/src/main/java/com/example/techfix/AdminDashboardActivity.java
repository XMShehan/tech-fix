package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_admin_dashboard
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
        // MANAGE SERVICES
        // =====================================================

        Button btnManageServices =
                findViewById(
                        R.id.btnManageServices
                );

        btnManageServices.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageServicesActivity.class
                    )
            );
        });

        // =====================================================
        // MANAGE BRANCHES
        // =====================================================

        Button btnManageBranches =
                findViewById(
                        R.id.btnManageBranches
                );

        btnManageBranches.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageBranchesActivity.class
                    )
            );
        });

        // =====================================================
        // TECHNICIANS
        // =====================================================

        Button btnTechnicians =
                findViewById(
                        R.id.btnTechnicians
                );

        btnTechnicians.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageTechniciansActivity.class
                    )
            );
        });

        // =====================================================
        // INVENTORY
        // =====================================================

        Button btnInventory =
                findViewById(
                        R.id.btnInventory
                );

        btnInventory.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageInventoryActivity.class
                    )
            );
        });

        // =====================================================
        // APPOINTMENTS
        // =====================================================

        Button btnAppointments =
                findViewById(
                        R.id.btnAppointments
                );

        btnAppointments.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageAppointmentsActivity.class
                    )
            );
        });

        // =====================================================
        // REPAIR STATUS
        // =====================================================

        Button btnRepairStatus =
                findViewById(
                        R.id.btnRepairStatus
                );

        btnRepairStatus.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            RepairStatusActivity.class
                    )
            );
        });

        // =====================================================
        // CUSTOMER FEEDBACK
        // =====================================================

        Button btnFeedback =
                findViewById(
                        R.id.btnFeedback
                );

        btnFeedback.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            AdminFeedbackActivity.class
                    )
            );
        });

        // =====================================================
        // PAYMENTS
        // =====================================================

        Button btnPayments =
                findViewById(
                        R.id.btnPayments
                );

        btnPayments.setOnClickListener(v -> {

            // Payment functionality can be connected later
        });

        // =====================================================
        // MANAGE JOBS
        // =====================================================

        Button btnManageJobs =
                findViewById(
                        R.id.btnManageJobs
                );

        btnManageJobs.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageJobsActivity.class
                    )
            );
        });

        // =====================================================
        // LOGOUT
        // =====================================================

        Button btnLogout =
                findViewById(
                        R.id.btnLogout
                );

        btnLogout.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }
}