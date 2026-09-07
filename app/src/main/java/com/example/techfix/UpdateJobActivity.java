package com.example.techfix;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class UpdateJobActivity extends AppCompatActivity {

    TextView txtJobId;
    TextView txtProductService;
    TextView txtCurrentStatus;

    Button btnStartJob;
    Button btnOngoing;
    Button btnAddPhoto;
    Button btnFinished;

    ImageView imgRepairPhoto;

    DatabaseHelper databaseHelper;

    int jobId;
    String technicianId;
    String productService;
    String currentStatus;

    // Photo information
    private Bitmap repairPhotoBitmap;
    private boolean photoAttached = false;

    private static final int CAMERA_PERMISSION_REQUEST = 300;

    // =====================================================
    // CAMERA RESULT
    // =====================================================

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            Bundle extras =
                                    result.getData().getExtras();

                            if (extras != null) {

                                Bitmap photo =
                                        (Bitmap) extras.get("data");

                                if (photo != null) {

                                    repairPhotoBitmap = photo;

                                    imgRepairPhoto.setImageBitmap(
                                            photo
                                    );

                                    photoAttached = true;

                                    Toast.makeText(
                                            this,
                                            "Repair photo attached",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    updateButtonStates();
                                }
                            }
                        }
                    }
            );

    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_update_job
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

        btnAddPhoto =
                findViewById(R.id.btnAddPhoto);

        btnFinished =
                findViewById(R.id.btnFinished);

        imgRepairPhoto =
                findViewById(R.id.imgRepairPhoto);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET JOB INFORMATION
        // =====================================================

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

        if (currentStatus == null ||
                currentStatus.trim().isEmpty()) {

            currentStatus = "PENDING";
        }

        // Display information
        txtJobId.setText(
                "Job #" + jobId
        );

        txtProductService.setText(
                "Product / Service: " +
                        (
                                productService != null
                                        ? productService
                                        : ""
                        )
        );

        txtCurrentStatus.setText(
                "Current Status: " +
                        currentStatus
        );

        // =====================================================
        // START JOB
        // =====================================================

        btnStartJob.setOnClickListener(v -> {

            updateJobStatus(
                    "STARTED"
            );
        });

        // =====================================================
        // ONGOING
        // =====================================================

        btnOngoing.setOnClickListener(v -> {

            updateJobStatus(
                    "ONGOING"
            );
        });

        // =====================================================
        // ADD PHOTO
        // =====================================================

        btnAddPhoto.setOnClickListener(v -> {

            openCamera();
        });

        // =====================================================
        // FINISHED
        // =====================================================

        btnFinished.setOnClickListener(v -> {

            if (!photoAttached) {

                Toast.makeText(
                        this,
                        "Please add a repair photo before finishing the job",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            finishJob();
        });

        // Initial button state
        updateButtonStates();
    }

    // =====================================================
    // OPEN CAMERA
    // =====================================================

    private void openCamera() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_PERMISSION_REQUEST
            );

            return;
        }

        Intent cameraIntent =
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );

        if (cameraIntent.resolveActivity(
                getPackageManager()
        ) != null) {

            cameraLauncher.launch(
                    cameraIntent
            );

        } else {

            Toast.makeText(
                    this,
                    "Camera is not available",
                    Toast.LENGTH_SHORT
            ).show();
        }
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
                                String.valueOf(
                                        jobId
                                ),
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
    // FINISH JOB
    // =====================================================

    private void finishJob() {

        if (repairPhotoBitmap == null) {

            Toast.makeText(
                    this,
                    "Please add a repair photo",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        /*
         * Store a simple reference indicating that
         * a repair photo was attached.
         *
         * The actual bitmap is displayed in the activity.
         * For this project, we store the photo reference
         * in the jobs.photoPath column.
         */
        String photoReference =
                "Repair photo attached - Job #" +
                        jobId;

        ContentValues values =
                new ContentValues();

        values.put(
                "status",
                "FINISHED"
        );

        values.put(
                "photoPath",
                photoReference
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
                                String.valueOf(
                                        jobId
                                ),
                                technicianId
                        }
                );

        if (result > 0) {

            currentStatus =
                    "FINISHED";

            txtCurrentStatus.setText(
                    "Current Status: FINISHED"
            );

            Toast.makeText(
                    this,
                    "Job finished successfully",
                    Toast.LENGTH_LONG
            ).show();

            updateButtonStates();

        } else {

            Toast.makeText(
                    this,
                    "Failed to finish job",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
    // UPDATE BUTTON STATES
    // =====================================================

    private void updateButtonStates() {

        if (currentStatus == null) {

            currentStatus =
                    "PENDING";
        }

        // -------------------------------------------------
        // PENDING
        // -------------------------------------------------

        if (currentStatus.equals("PENDING")) {

            btnStartJob.setEnabled(true);

            btnOngoing.setEnabled(false);

            btnAddPhoto.setEnabled(false);

            btnFinished.setEnabled(false);
        }

        // -------------------------------------------------
        // STARTED
        // -------------------------------------------------

        else if (currentStatus.equals("STARTED")) {

            btnStartJob.setEnabled(false);

            btnOngoing.setEnabled(true);

            btnAddPhoto.setEnabled(false);

            btnFinished.setEnabled(false);
        }

        // -------------------------------------------------
        // ONGOING
        // -------------------------------------------------

        else if (currentStatus.equals("ONGOING")) {

            btnStartJob.setEnabled(false);

            btnOngoing.setEnabled(false);

            btnAddPhoto.setEnabled(true);

            btnFinished.setEnabled(
                    photoAttached
            );
        }

        // -------------------------------------------------
        // FINISHED
        // -------------------------------------------------

        else if (currentStatus.equals("FINISHED")) {

            btnStartJob.setEnabled(false);

            btnOngoing.setEnabled(false);

            btnAddPhoto.setEnabled(false);

            btnFinished.setEnabled(false);
        }
    }

    // =====================================================
    // CAMERA PERMISSION RESULT
    // =====================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}