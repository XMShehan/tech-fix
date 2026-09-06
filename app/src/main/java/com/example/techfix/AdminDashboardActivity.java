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

        setContentView(R.layout.activity_admin_dashboard);

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
                findViewById(R.id.btnManageServices);

        btnManageServices.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageServicesActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // MANAGE BRANCHES
        // =====================================================

        Button btnManageBranches =
                findViewById(R.id.btnManageBranches);

        btnManageBranches.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageBranchesActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // TECHNICIANS
        // =====================================================

        Button btnTechnicians =
                findViewById(R.id.btnTechnicians);

        btnTechnicians.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageTechniciansActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // INVENTORY
        // =====================================================

        Button btnInventory =
                findViewById(R.id.btnInventory);

        btnInventory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageInventoryActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // APPOINTMENTS
        // =====================================================

        Button btnAppointments =
                findViewById(R.id.btnAppointments);

        btnAppointments.setOnClickListener(v -> {

            // Appointment management can be connected later
        });

        // =====================================================
        // REPAIR STATUS
        // =====================================================

        Button btnRepairStatus =
                findViewById(R.id.btnRepairStatus);

        btnRepairStatus.setOnClickListener(v -> {

            // Repair status functionality can be connected later
        });

        // =====================================================
        // CUSTOMER FEEDBACK
        // =====================================================

        Button btnFeedback =
                findViewById(R.id.btnFeedback);

        btnFeedback.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            CustomerFeedbackActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // PAYMENTS
        // =====================================================

        Button btnPayments =
                findViewById(R.id.btnPayments);

        btnPayments.setOnClickListener(v -> {

            // Payment functionality can be connected later
        });

        // =====================================================
        // MANAGE JOBS
        // =====================================================

        Button btnManageJobs =
                findViewById(R.id.btnManageJobs);

        btnManageJobs.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminDashboardActivity.this,
                            ManageJobsActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // LOGOUT
        // =====================================================

        Button btnLogout =
                findViewById(R.id.btnLogout);

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