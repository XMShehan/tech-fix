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

public class UpdateFeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText edtComment;

    private Button btnUpdate;
    private Button btnCancel;

    private DatabaseHelper databaseHelper;

    private int feedbackId;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_update_feedback
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        ratingBar =
                findViewById(
                        R.id.ratingBar
                );

        edtComment =
                findViewById(
                        R.id.edtComment
                );

        btnUpdate =
                findViewById(
                        R.id.btnUpdate
                );

        btnCancel =
                findViewById(
                        R.id.btnCancel
                );

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET DATA
        // =====================================================

        feedbackId =
                getIntent().getIntExtra(
                        "feedbackId",
                        -1
                );

        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        // =====================================================
        // VALIDATE
        // =====================================================

        if (feedbackId == -1
                || customerId == null
                || customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Feedback information missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // LOAD EXISTING FEEDBACK
        // =====================================================

        loadFeedback();

        // =====================================================
        // UPDATE
        // =====================================================

        btnUpdate.setOnClickListener(
                v -> updateFeedback()
        );

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(
                v -> finish()
        );
    }

    private void loadFeedback() {

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT rating, comment " +
                                "FROM feedback " +
                                "WHERE feedbackId = ? " +
                                "AND customerId = ?",

                        new String[]{
                                String.valueOf(
                                        feedbackId
                                ),
                                customerId
                        }
                );

        if (cursor.moveToFirst()) {

            int rating =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "rating"
                            )
                    );

            String comment =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "comment"
                            )
                    );

            ratingBar.setRating(
                    rating
            );

            edtComment.setText(
                    comment
            );

        } else {

            Toast.makeText(
                    this,
                    "Feedback not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }

        cursor.close();
    }

    private void updateFeedback() {

        float rating =
                ratingBar.getRating();

        String comment =
                edtComment.getText()
                        .toString()
                        .trim();

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
        // UPDATE DATABASE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "rating",
                (int) rating
        );

        values.put(
                "comment",
                comment
        );

        int updated =
                db.update(
                        "feedback",
                        values,
                        "feedbackId = ? AND customerId = ?",
                        new String[]{
                                String.valueOf(
                                        feedbackId
                                ),
                                customerId
                        }
                );

        // =====================================================
        // RESULT
        // =====================================================

        if (updated > 0) {

            Toast.makeText(
                    this,
                    "Feedback updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update feedback",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}