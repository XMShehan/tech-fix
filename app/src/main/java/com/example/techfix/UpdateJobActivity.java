package com.example.techfix;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    // Actual saved photo path
    private String savedPhotoPath = null;

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

                                    // Display photo immediately
                                    imgRepairPhoto.setImageBitmap(
                                            photo
                                    );

                                    // Save actual photo file
                                    saveRepairPhoto(photo);
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

            currentStatus =
                    "PENDING";
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
        // LOAD EXISTING PHOTO
        // =====================================================

        loadExistingPhoto();

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

            if (savedPhotoPath == null ||
                    savedPhotoPath.trim().isEmpty()) {

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
    // SAVE REPAIR PHOTO
    // =====================================================

    private void saveRepairPhoto(
            Bitmap bitmap
    ) {

        FileOutputStream outputStream = null;

        try {

            /*
             * Create a file inside the app's private
             * internal storage.
             */
            File photoFile =
                    new File(
                            getFilesDir(),
                            "repair_job_" +
                                    jobId +
                                    "_" +
                                    System.currentTimeMillis() +
                                    ".jpg"
                    );

            outputStream =
                    new FileOutputStream(
                            photoFile
                    );

            boolean compressed =
                    bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            90,
                            outputStream
                    );

            if (!compressed) {

                Toast.makeText(
                        this,
                        "Failed to save repair photo",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            outputStream.flush();

            savedPhotoPath =
                    photoFile.getAbsolutePath();

            // Save path immediately to database
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "photoPath",
                    savedPhotoPath
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

                Toast.makeText(
                        this,
                        "Repair photo saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                updateButtonStates();

            } else {

                Toast.makeText(
                        this,
                        "Photo saved, but database update failed",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (IOException e) {

            savedPhotoPath = null;

            Toast.makeText(
                    this,
                    "Failed to save repair photo",
                    Toast.LENGTH_LONG
            ).show();

        } finally {

            if (outputStream != null) {

                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    // =====================================================
    // LOAD EXISTING PHOTO
    // =====================================================

    private void loadExistingPhoto() {

        if (jobId == -1 ||
                technicianId == null) {

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT photoPath " +
                                    "FROM jobs " +
                                    "WHERE jobId = ? " +
                                    "AND technicianId = ?",
                            new String[]{
                                    String.valueOf(
                                            jobId
                                    ),
                                    technicianId
                            }
                    );

            if (cursor.moveToFirst()) {

                String photoPath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "photoPath"
                                )
                        );

                if (photoPath != null &&
                        !photoPath.trim().isEmpty()) {

                    File photoFile =
                            new File(photoPath);

                    if (photoFile.exists()) {

                        savedPhotoPath =
                                photoPath;

                        Bitmap bitmap =
                                BitmapFactory.decodeFile(
                                        photoPath
                                );

                        if (bitmap != null) {

                            imgRepairPhoto.setImageBitmap(
                                    bitmap
                            );
                        }
                    }
                }
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
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

        if (savedPhotoPath == null ||
                savedPhotoPath.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Please add a repair photo",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "status",
                "FINISHED"
        );

        values.put(
                "photoPath",
                savedPhotoPath
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

        // PENDING
        if (currentStatus.equals("PENDING")) {

            btnStartJob.setEnabled(true);

            btnOngoing.setEnabled(false);

            btnAddPhoto.setEnabled(false);

            btnFinished.setEnabled(false);
        }

        // STARTED
        else if (currentStatus.equals("STARTED")) {

            btnStartJob.setEnabled(false);

            btnOngoing.setEnabled(true);

            btnAddPhoto.setEnabled(false);

            btnFinished.setEnabled(false);
        }

        // ONGOING
        else if (currentStatus.equals("ONGOING")) {

            btnStartJob.setEnabled(false);

            btnOngoing.setEnabled(false);

            btnAddPhoto.setEnabled(true);

            btnFinished.setEnabled(
                    savedPhotoPath != null &&
                            !savedPhotoPath.trim().isEmpty()
            );
        }

        // FINISHED
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