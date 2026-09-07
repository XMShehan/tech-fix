package com.example.techfix;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CustomerFeedbackActivity extends AppCompatActivity {

    private EditText edtSearchFeedback;
    private Button btnAddFeedback;
    private LinearLayout feedbackContainer;

    private DatabaseHelper databaseHelper;

    private String customerId;
    private String customerName;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_customer_feedback
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
        // CONNECT UI
        // =====================================================

        edtSearchFeedback =
                findViewById(
                        R.id.edtSearchFeedback
                );

        btnAddFeedback =
                findViewById(
                        R.id.btnAddFeedback
                );

        feedbackContainer =
                findViewById(
                        R.id.feedbackContainer
                );

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET LOGGED-IN CUSTOMER
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
        // VALIDATE CUSTOMER
        // =====================================================

        if (customerId == null
                || customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer information missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // =====================================================
        // ADD FEEDBACK
        // =====================================================

        btnAddFeedback.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerFeedbackActivity.this,
                            FeedbackActivity.class
                    );

            intent.putExtra(
                    "customerId",
                    customerId
            );

            intent.putExtra(
                    "customerName",
                    customerName
            );

            intent.putExtra(
                    "customerEmail",
                    customerEmail
            );

            startActivity(intent);
        });

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearchFeedback.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        loadFeedback(
                                s.toString().trim()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {
                    }
                }
        );

        // =====================================================
        // INITIAL LOAD
        // =====================================================

        loadFeedback("");
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null
                && customerId != null) {

            loadFeedback(
                    edtSearchFeedback
                            .getText()
                            .toString()
                            .trim()
            );
        }
    }

    // =========================================================
    // LOAD CUSTOMER'S FEEDBACK
    // =========================================================

    private void loadFeedback(
            String searchText) {

        feedbackContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor;

        if (searchText.isEmpty()) {

            cursor = db.rawQuery(
                    "SELECT feedbackId, customerName, " +
                            "rating, comment, date " +
                            "FROM feedback " +
                            "WHERE customerId = ? " +
                            "ORDER BY feedbackId DESC",

                    new String[]{
                            customerId
                    }
            );

        } else {

            cursor = db.rawQuery(
                    "SELECT feedbackId, customerName, " +
                            "rating, comment, date " +
                            "FROM feedback " +
                            "WHERE customerId = ? " +
                            "AND (comment LIKE ? " +
                            "OR customerName LIKE ?) " +
                            "ORDER BY feedbackId DESC",

                    new String[]{
                            customerId,
                            "%" + searchText + "%",
                            "%" + searchText + "%"
                    }
            );
        }

        // =====================================================
        // NO FEEDBACK
        // =====================================================

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "You have not submitted any feedback yet."
            );

            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setGravity(
                    Gravity.CENTER
            );

            emptyText.setPadding(
                    20,
                    40,
                    20,
                    40
            );

            feedbackContainer.addView(
                    emptyText
            );

            cursor.close();

            return;
        }

        // =====================================================
        // DISPLAY FEEDBACK
        // =====================================================

        while (cursor.moveToNext()) {

            int feedbackId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "feedbackId"
                            )
                    );

            String name =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "customerName"
                            )
                    );

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

            String date =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "date"
                            )
                    );

            // =================================================
            // FEEDBACK CARD
            // =================================================

            LinearLayout card =
                    new LinearLayout(this);

            card.setOrientation(
                    LinearLayout.VERTICAL
            );

            card.setPadding(
                    20,
                    20,
                    20,
                    20
            );

            card.setBackgroundColor(
                    Color.WHITE
            );

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(
                    0,
                    0,
                    0,
                    20
            );

            card.setLayoutParams(
                    cardParams
            );

            // =================================================
            // TITLE
            // =================================================

            TextView title =
                    new TextView(this);

            title.setText(
                    "Feedback #" + feedbackId
            );

            title.setTextSize(18);
            title.setTextColor(Color.BLACK);
            title.setTypeface(
                    null,
                    Typeface.BOLD
            );

            card.addView(title);

            // =================================================
            // RATING
            // =================================================

            TextView txtRating =
                    new TextView(this);

            txtRating.setText(
                    "Rating: " + getStars(rating)
            );

            txtRating.setTextSize(16);
            txtRating.setTextColor(Color.DKGRAY);
            txtRating.setPadding(
                    0,
                    10,
                    0,
                    5
            );

            card.addView(txtRating);

            // =================================================
            // COMMENT
            // =================================================

            TextView txtComment =
                    new TextView(this);

            txtComment.setText(
                    "Comment: " + comment
            );

            txtComment.setTextSize(15);
            txtComment.setTextColor(Color.DKGRAY);
            txtComment.setPadding(
                    0,
                    5,
                    0,
                    5
            );

            card.addView(txtComment);

            // =================================================
            // DATE
            // =================================================

            TextView txtDate =
                    new TextView(this);

            txtDate.setText(
                    "Date: " + date
            );

            txtDate.setTextSize(14);
            txtDate.setTextColor(Color.GRAY);
            txtDate.setPadding(
                    0,
                    5,
                    0,
                    10
            );

            card.addView(txtDate);

            // =================================================
            // BUTTON ROW
            // =================================================

            LinearLayout buttonRow =
                    new LinearLayout(this);

            buttonRow.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            buttonRow.setGravity(
                    Gravity.CENTER
            );

            Button btnEdit =
                    new Button(this);

            btnEdit.setText(
                    "Edit"
            );

            Button btnDelete =
                    new Button(this);

            btnDelete.setText(
                    "Delete"
            );

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );

            buttonParams.setMargins(
                    5,
                    5,
                    5,
                    5
            );

            buttonRow.addView(
                    btnEdit,
                    buttonParams
            );

            buttonRow.addView(
                    btnDelete,
                    buttonParams
            );

            card.addView(
                    buttonRow
            );

            // =================================================
            // EDIT
            // =================================================

            btnEdit.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                CustomerFeedbackActivity.this,
                                UpdateFeedbackActivity.class
                        );

                intent.putExtra(
                        "feedbackId",
                        feedbackId
                );

                intent.putExtra(
                        "customerId",
                        customerId
                );

                startActivity(intent);
            });

            // =================================================
            // DELETE
            // =================================================

            btnDelete.setOnClickListener(v -> {

                showDeleteConfirmation(
                        feedbackId
                );
            });

            feedbackContainer.addView(
                    card
            );
        }

        cursor.close();
    }

    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private void showDeleteConfirmation(
            int feedbackId) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Feedback"
                )
                .setMessage(
                        "Are you sure you want to delete this feedback?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            SQLiteDatabase db =
                                    databaseHelper
                                            .getWritableDatabase();

                            int deleted =
                                    db.delete(
                                            "feedback",
                                            "feedbackId = ? AND customerId = ?",
                                            new String[]{
                                                    String.valueOf(
                                                            feedbackId
                                                    ),
                                                    customerId
                                            }
                                    );

                            if (deleted > 0) {

                                Toast.makeText(
                                        this,
                                        "Feedback deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadFeedback(
                                        edtSearchFeedback
                                                .getText()
                                                .toString()
                                                .trim()
                                );

                            } else {

                                Toast.makeText(
                                        this,
                                        "Unable to delete feedback",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // =========================================================
    // STAR DISPLAY
    // =========================================================

    private String getStars(
            int rating) {

        StringBuilder stars =
                new StringBuilder();

        for (int i = 0; i < rating; i++) {

            stars.append("★");
        }

        for (int i = rating; i < 5; i++) {

            stars.append("☆");
        }

        return stars.toString();
    }
}