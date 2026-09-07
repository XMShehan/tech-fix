package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TechnicianDashboardActivity extends AppCompatActivity {

    TextView txtWelcome;
    TextView txtTechnicianInfo;

    Button btnMyJobs;
    Button btnJobHistory;

    String technicianId;
    String technicianName;
    String technicianEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_technician_dashboard);

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

        // Find views
        txtWelcome =
                findViewById(R.id.txtWelcome);

        txtTechnicianInfo =
                findViewById(R.id.txtTechnicianInfo);

        btnMyJobs =
                findViewById(R.id.btnMyJobs);

        btnJobHistory =
                findViewById(R.id.btnJobHistory);

        // Get technician information from LoginActivity
        technicianId =
                getIntent().getStringExtra("technicianId");

        technicianName =
                getIntent().getStringExtra("technicianName");

        technicianEmail =
                getIntent().getStringExtra("technicianEmail");

        // Display technician information
        if (technicianName != null) {

            txtWelcome.setText(
                    "Welcome, " + technicianName
            );
        }

        String info =
                "Technician ID: " +
                        (technicianId != null ? technicianId : "") +
                        "\nEmail: " +
                        (technicianEmail != null ? technicianEmail : "");

        txtTechnicianInfo.setText(info);

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

            // Tell MyJobsActivity to show completed jobs
            intent.putExtra(
                    "showHistory",
                    true
            );

            startActivity(intent);
        });
    }
}