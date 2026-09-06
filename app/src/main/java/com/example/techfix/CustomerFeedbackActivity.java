package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CustomerFeedbackActivity extends AppCompatActivity {

    EditText edtSearchFeedback;

    Button btnAddFeedback;
    Button btnDeleteFeedback;

    LinearLayout feedbackContainer;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_customer_feedback);

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
        edtSearchFeedback =
                findViewById(R.id.edtSearchFeedback);

        btnAddFeedback =
                findViewById(R.id.btnAddFeedback);

        btnDeleteFeedback =
                findViewById(R.id.btnDeleteFeedback);

        feedbackContainer =
                findViewById(R.id.feedbackContainer);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // =========================
        // Add Feedback
        // =========================

        btnAddFeedback.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerFeedbackActivity.this,
                            AddFeedbackActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // Delete Feedback
        // =========================

        btnDeleteFeedback.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CustomerFeedbackActivity.this,
                            DeleteFeedbackActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // Search Feedback
        // =========================

        edtSearchFeedback.addTextChangedListener(
                new TextWatcher() {

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
                            Editable s) {
                    }
                }
        );

        // Load feedback
        loadFeedback("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {

            loadFeedback(
                    edtSearchFeedback
                            .getText()
                            .toString()
                            .trim()
            );
        }
    }

    // =========================
    // Load Feedback
    // =========================

    private void loadFeedback(String searchText) {

        feedbackContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor;

        if (searchText.isEmpty()) {

            cursor = db.rawQuery(
                    "SELECT id, customerName, customerEmail, feedback " +
                            "FROM feedback " +
                            "ORDER BY id DESC",
                    null
            );

        } else {

            cursor = db.rawQuery(
                    "SELECT id, customerName, customerEmail, feedback " +
                            "FROM feedback " +
                            "WHERE customerName LIKE ? " +
                            "OR customerEmail LIKE ? " +
                            "OR feedback LIKE ? " +
                            "ORDER BY id DESC",
                    new String[]{
                            "%" + searchText + "%",
                            "%" + searchText + "%",
                            "%" + searchText + "%"
                    }
            );
        }

        if (cursor.getCount() == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No feedback available"
            );

            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);

            emptyText.setPadding(
                    10,
                    20,
                    10,
                    20
            );

            feedbackContainer.addView(
                    emptyText
            );

        } else {

            while (cursor.moveToNext()) {

                int feedbackId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String customerName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "customerName"
                                )
                        );

                String customerEmail =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "customerEmail"
                                )
                        );

                String feedback =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "feedback"
                                )
                        );

                // =========================
                // Feedback Layout
                // =========================

                LinearLayout feedbackLayout =
                        new LinearLayout(this);

                feedbackLayout.setOrientation(
                        LinearLayout.VERTICAL
                );

                feedbackLayout.setPadding(
                        5,
                        15,
                        5,
                        15
                );

                // Feedback ID

                TextView txtId =
                        new TextView(this);

                txtId.setText(
                        "Feedback ID: " + feedbackId
                );

                txtId.setTextSize(16);
                txtId.setTextColor(Color.BLACK);

                feedbackLayout.addView(txtId);

                // Customer Name

                TextView txtName =
                        new TextView(this);

                txtName.setText(
                        "Customer Name: " + customerName
                );

                txtName.setTextSize(16);
                txtName.setTextColor(Color.BLACK);

                feedbackLayout.addView(txtName);

                // Customer Email

                TextView txtEmail =
                        new TextView(this);

                txtEmail.setText(
                        "Customer Email: " + customerEmail
                );

                txtEmail.setTextSize(16);
                txtEmail.setTextColor(Color.BLACK);

                feedbackLayout.addView(txtEmail);

                // Feedback

                TextView txtFeedback =
                        new TextView(this);

                txtFeedback.setText(
                        "Feedback: " + feedback
                );

                txtFeedback.setTextSize(16);
                txtFeedback.setTextColor(Color.BLACK);

                feedbackLayout.addView(txtFeedback);

                // =========================
                // Update Button
                // =========================

                Button btnUpdate =
                        new Button(this);

                btnUpdate.setText(
                        "UPDATE FEEDBACK"
                );

                LinearLayout.LayoutParams buttonParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                buttonParams.setMargins(
                        5,
                        10,
                        5,
                        10
                );

                btnUpdate.setLayoutParams(
                        buttonParams
                );

                feedbackLayout.addView(
                        btnUpdate
                );

                // =========================
                // Update Navigation
                // =========================

                btnUpdate.setOnClickListener(v -> {

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
                            "customerName",
                            customerName
                    );

                    intent.putExtra(
                            "customerEmail",
                            customerEmail
                    );

                    intent.putExtra(
                            "feedback",
                            feedback
                    );

                    startActivity(intent);
                });

                // Add to container

                feedbackContainer.addView(
                        feedbackLayout
                );
            }
        }

        cursor.close();
    }
}