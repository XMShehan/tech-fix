package com.example.techfix;

import android.content.ContentValues;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

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
        // GET LOGGED-IN CUSTOMER INFORMATION
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

        // =====================================================
        // VALIDATE CUSTOMER INFORMATION
        // =====================================================

        if (customerId == null
                || customerId.trim().isEmpty()
                || customerName == null
                || customerName.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer information missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // DISPLAY CUSTOMER NAME
        // =====================================================

        edtName.setText(customerName);

        // Customer name comes from login
        // so don't allow manual editing
        edtName.setEnabled(false);

        // =====================================================
        // SUBMIT FEEDBACK
        // =====================================================

        btnSubmit.setOnClickListener(v -> {

            String comment =
                    edtComment.getText()
                            .toString()
                            .trim();

            float rating =
                    ratingBar.getRating();

            // -------------------------------------------------
            // Validate rating
            // -------------------------------------------------

            if (rating == 0) {

                Toast.makeText(
                        FeedbackActivity.this,
                        "Please give a rating",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // -------------------------------------------------
            // Validate comment
            // -------------------------------------------------

            if (comment.isEmpty()) {

                edtComment.setError(
                        "Please enter your feedback"
                );

                edtComment.requestFocus();

                return;
            }

            // =================================================
            // SAVE FEEDBACK
            // =================================================

            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "customerId",
                    customerId
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
                    ).format(new Date());

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

            // =================================================
            // RESULT
            // =================================================

            if (result != -1) {

                Toast.makeText(
                        FeedbackActivity.this,
                        "Thank you! Your feedback has been submitted successfully.",
                        Toast.LENGTH_LONG
                ).show();

                finish();

            } else {

                Toast.makeText(
                        FeedbackActivity.this,
                        "Failed to save feedback",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(v -> {

            finish();
        });
    }
}