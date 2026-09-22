package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtComment;

    private RatingBar ratingBar;

    private Button btnSubmit;
    private Button btnCancel;

    private DatabaseHelper databaseHelper;

    private String customerId;
    private String customerName;
    private String customerEmail;

    private int jobId;
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_feedback
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        edtName =
                findViewById(R.id.edtName);

        edtComment =
                findViewById(R.id.edtComment);

        ratingBar =
                findViewById(R.id.ratingBar);

        btnSubmit =
                findViewById(R.id.btnSubmit);

        btnCancel =
                findViewById(R.id.btnCancel);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET CUSTOMER
        // =====================================================

        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        customerName =
                getIntent().getStringExtra(
                        "customerName"
                );

        customerEmail =
                getIntent().getStringExtra(
                        "customerEmail"
                );

        jobId =
                getIntent().getIntExtra(
                        "jobId",
                        -1
                );

        appointmentId =
                getIntent().getIntExtra(
                        "appointmentId",
                        -1
                );

        // =====================================================
        // VALIDATE
        // =====================================================

        if (customerId == null ||
                customerId.trim().isEmpty() ||
                customerName == null ||
                customerName.trim().isEmpty() ||
                jobId == -1) {

            Toast.makeText(
                    this,
                    "Repair information missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // VALIDATE FINISHED JOB
        // =====================================================

        if (!isFinishedJob()) {

            Toast.makeText(
                    this,
                    "Feedback is only available for completed repairs",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // CHECK EXISTING FEEDBACK
        // =====================================================

        if (feedbackAlreadyExists()) {

            Toast.makeText(
                    this,
                    "Feedback has already been submitted for this repair",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // DISPLAY CUSTOMER
        // =====================================================

        edtName.setText(
                customerName
        );

        edtName.setEnabled(
                false
        );

        // =====================================================
        // SUBMIT
        // =====================================================

        btnSubmit.setOnClickListener(
                v -> submitFeedback()
        );

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(
                v -> finish()
        );
    }

    // =====================================================
    // CHECK FINISHED JOB
    // =====================================================

    private boolean isFinishedJob() {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT j.jobId " +
                                "FROM jobs j " +
                                "INNER JOIN appointments a " +
                                "ON j.appointmentId = a.appointmentId " +
                                "WHERE j.jobId = ? " +
                                "AND a.customerId = ? " +
                                "AND j.status = 'FINISHED'",

                        new String[]{
                                String.valueOf(jobId),
                                customerId
                        }
                );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // =====================================================
    // CHECK EXISTING FEEDBACK
    // =====================================================

    private boolean feedbackAlreadyExists() {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT feedbackId " +
                                "FROM feedback " +
                                "WHERE customerId = ? " +
                                "AND jobId = ?",

                        new String[]{
                                customerId,
                                String.valueOf(jobId)
                        }
                );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // =====================================================
    // SUBMIT FEEDBACK
    // =====================================================

    private void submitFeedback() {

        String comment =
                edtComment.getText()
                        .toString()
                        .trim();

        float rating =
                ratingBar.getRating();

        // =====================================================
        // VALIDATION
        // =====================================================

        if (rating == 0) {

            Toast.makeText(
                    this,
                    "Please give a rating",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (comment.isEmpty()) {

            edtComment.setError(
                    "Please enter your feedback"
            );

            edtComment.requestFocus();

            return;
        }

        // =====================================================
        // DOUBLE CHECK
        // =====================================================

        if (!isFinishedJob()) {

            Toast.makeText(
                    this,
                    "This repair is no longer available for feedback",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (feedbackAlreadyExists()) {

            Toast.makeText(
                    this,
                    "Feedback has already been submitted for this repair",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // SAVE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "customerId",
                customerId
        );

        values.put(
                "jobId",
                jobId
        );

        values.put(
                "customerName",
                customerName
        );

        values.put(
                "rating",
                (int) rating
        );

        values.put(
                "comment",
                comment
        );

        String currentDate =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        values.put(
                "date",
                currentDate
        );

        long result =
                db.insert(
                        "feedback",
                        null,
                        values
                );

        // =====================================================
        // RESULT
        // =====================================================

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Thank you! Your feedback has been submitted successfully.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to save feedback",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}