package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateJobActivity extends AppCompatActivity {

    TextView txtJobId;
    TextView txtProductService;
    TextView txtCurrentStatus;

    Button btnStartJob;
    Button btnOngoing;
    Button btnFinished;

    DatabaseHelper databaseHelper;

    int jobId;
    String technicianId;
    String productService;
    String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_job);

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
        txtJobId =
                findViewById(R.id.txtJobId);

        txtProductService =
                findViewById(R.id.txtProductService);

        txtCurrentStatus =
                findViewById(R.id.txtCurrentStatus);

        btnStartJob =
                findViewById(R.id.btnStartJob);

        btnOngoing =
                findViewById(R.id.btnOngoing);

        btnFinished =
                findViewById(R.id.btnFinished);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // Get data from MyJobsActivity
        jobId =
                getIntent().getIntExtra(
                        "jobId",
                        -1
                );

        technicianId =
                getIntent().getStringExtra(
                        "technicianId"
                );

        productService =
                getIntent().getStringExtra(
                        "productService"
                );

        currentStatus =
                getIntent().getStringExtra(
                        "status"
                );

        // Display information
        txtJobId.setText(
                "Job #" + jobId
        );

        txtProductService.setText(
                "Product / Service: " +
                        (productService != null
                                ? productService
                                : "")
        );

        txtCurrentStatus.setText(
                "Current Status: " +
                        (currentStatus != null
                                ? currentStatus
                                : "PENDING")
        );

        // =====================================================
        // START JOB
        // =====================================================

        btnStartJob.setOnClickListener(v -> {

            updateJobStatus("STARTED");
        });

        // =====================================================
        // ONGOING
        // =====================================================

        btnOngoing.setOnClickListener(v -> {

            updateJobStatus("ONGOING");
        });

        // =====================================================
        // FINISHED
        // =====================================================

        btnFinished.setOnClickListener(v -> {

            updateJobStatus("FINISHED");
        });

        // Update button state
        updateButtonStates();
    }

    // =====================================================
    // UPDATE JOB STATUS
    // =====================================================

    private void updateJobStatus(
            String newStatus
    ) {

        if (jobId == -1) {

            Toast.makeText(
                    this,
                    "Invalid job",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "status",
                newStatus
        );

        values.put(
                "updatedAt",
                String.valueOf(
                        System.currentTimeMillis()
                )
        );

        int result =
                db.update(
                        "jobs",
                        values,
                        "jobId = ? AND technicianId = ?",
                        new String[]{
                                String.valueOf(jobId),
                                technicianId
                        }
                );

        if (result > 0) {

            currentStatus =
                    newStatus;

            txtCurrentStatus.setText(
                    "Current Status: " +
                            currentStatus
            );

            Toast.makeText(
                    this,
                    "Job status updated to " +
                            newStatus,
                    Toast.LENGTH_SHORT
            ).show();

            updateButtonStates();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update job",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
    // UPDATE BUTTON STATES
    // =====================================================

    private void updateButtonStates() {

        if (currentStatus == null) {
            currentStatus = "PENDING";
        }

        // PENDING
        if (currentStatus.equals("PENDING")) {

            btnStartJob.setEnabled(true);
            btnOngoing.setEnabled(false);
            btnFinished.setEnabled(false);

        }

        // STARTED
        else if (currentStatus.equals("STARTED")) {

            btnStartJob.setEnabled(false);
            btnOngoing.setEnabled(true);
            btnFinished.setEnabled(false);

        }

        // ONGOING
        else if (currentStatus.equals("ONGOING")) {

            btnStartJob.setEnabled(false);
            btnOngoing.setEnabled(false);
            btnFinished.setEnabled(true);

        }

        // FINISHED
        else if (currentStatus.equals("FINISHED")) {

            btnStartJob.setEnabled(false);
            btnOngoing.setEnabled(false);
            btnFinished.setEnabled(false);
        }
    }
}