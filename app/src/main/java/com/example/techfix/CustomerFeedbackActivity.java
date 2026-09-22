package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CustomerFeedbackActivity extends AppCompatActivity {

    private EditText edtSearchFeedback;
    private LinearLayout feedbackContainer;

    private DatabaseHelper databaseHelper;

    private String customerId;
    private String customerName;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_customer_feedback
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        edtSearchFeedback =
                findViewById(
                        R.id.edtSearchFeedback
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
        // GET CUSTOMER INFORMATION
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

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer information missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

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

                        loadCompletedJobs(
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

        loadCompletedJobs("");
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null &&
                customerId != null) {

            loadCompletedJobs(
                    edtSearchFeedback
                            .getText()
                            .toString()
                            .trim()
            );
        }
    }

    // =====================================================
    // LOAD FINISHED JOBS
    // =====================================================

    private void loadCompletedJobs(
            String searchText) {

        feedbackContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper
                        .getReadableDatabase();

        Cursor cursor = null;

        try {

            String searchPattern =
                    "%" + searchText + "%";

            cursor = db.rawQuery(
                    "SELECT " +
                            "j.jobId, " +
                            "j.technicianId, " +
                            "t.technicianName, " +
                            "a.appointmentId, " +
                            "a.productService, " +
                            "a.category, " +
                            "a.price, " +
                            "a.branch, " +
                            "a.appointmentDate, " +
                            "a.appointmentTime, " +
                            "f.feedbackId, " +
                            "f.rating, " +
                            "f.comment, " +
                            "f.date " +

                            "FROM jobs j " +

                            "INNER JOIN appointments a " +
                            "ON j.appointmentId = a.appointmentId " +

                            "LEFT JOIN technicians t " +
                            "ON j.technicianId = t.technicianId " +

                            "LEFT JOIN feedback f " +
                            "ON f.jobId = j.jobId " +
                            "AND f.customerId = a.customerId " +

                            "WHERE a.customerId = ? " +
                            "AND j.status = 'FINISHED' " +

                            "AND (" +
                            "a.productService LIKE ? " +
                            "OR a.category LIKE ? " +
                            "OR a.branch LIKE ? " +
                            "OR t.technicianName LIKE ? " +
                            "OR f.comment LIKE ? " +
                            "OR a.appointmentDate LIKE ?" +
                            ") " +

                            "ORDER BY j.jobId DESC",

                    new String[]{
                            customerId,
                            searchPattern,
                            searchPattern,
                            searchPattern,
                            searchPattern,
                            searchPattern,
                            searchPattern
                    }
            );

            if (!cursor.moveToFirst()) {

                if (searchText.isEmpty()) {

                    showMessage(
                            "No completed repairs available for feedback."
                    );

                } else {

                    showMessage(
                            "No matching completed repairs found."
                    );
                }

                return;
            }

            do {

                int jobId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "jobId"
                                )
                        );

                int appointmentId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentId"
                                )
                        );

                String technicianId =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianId"
                                )
                        );

                String technicianName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "technicianName"
                                )
                        );

                String productService =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "productService"
                                )
                        );

                String category =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "category"
                                )
                        );

                double price =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        );

                String branch =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branch"
                                )
                        );

                String appointmentDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentDate"
                                )
                        );

                String appointmentTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentTime"
                                )
                        );

                int feedbackId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "feedbackId"
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

                String feedbackDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "date"
                                )
                        );

                createJobFeedbackCard(
                        jobId,
                        appointmentId,
                        technicianId,
                        technicianName,
                        productService,
                        category,
                        price,
                        branch,
                        appointmentDate,
                        appointmentTime,
                        feedbackId,
                        rating,
                        comment,
                        feedbackDate
                );

            } while (cursor.moveToNext());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // CREATE FINISHED JOB CARD
    // =====================================================

    private void createJobFeedbackCard(
            int jobId,
            int appointmentId,
            String technicianId,
            String technicianName,
            String productService,
            String category,
            double price,
            String branch,
            String appointmentDate,
            String appointmentTime,
            int feedbackId,
            int rating,
            String comment,
            String feedbackDate) {

        // =====================================================
        // MAIN CARD
        // =====================================================

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(20)
        );

        GradientDrawable cardBackground =
                new GradientDrawable();

        cardBackground.setColor(
                Color.WHITE
        );

        cardBackground.setCornerRadius(
                dp(18)
        );

        cardBackground.setStroke(
                dp(1),
                Color.rgb(
                        226,
                        234,
                        240
                )
        );

        card.setBackground(
                cardBackground
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                dp(16)
        );

        card.setLayoutParams(
                cardParams
        );

        // =====================================================
        // REPAIR NUMBER
        // =====================================================

        TextView jobText =
                new TextView(this);

        jobText.setText(
                "Repair #" + jobId
        );

        jobText.setTextSize(
                20
        );

        jobText.setTypeface(
                null,
                Typeface.BOLD
        );

        jobText.setTextColor(
                Color.rgb(
                        38,
                        50,
                        56
                )
        );

        card.addView(
                jobText
        );

        // =====================================================
        // PRODUCT
        // =====================================================

        TextView productText =
                new TextView(this);

        productText.setText(
                safeText(productService)
        );

        productText.setTextSize(
                17
        );

        productText.setTypeface(
                null,
                Typeface.BOLD
        );

        productText.setTextColor(
                Color.rgb(
                        25,
                        118,
                        210
                )
        );

        productText.setPadding(
                0,
                dp(8),
                0,
                0
        );

        card.addView(
                productText
        );

        // =====================================================
        // FINISHED STATUS
        // =====================================================

        TextView statusText =
                new TextView(this);

        statusText.setText(
                "Repair Status: FINISHED"
        );

        statusText.setTextSize(
                14
        );

        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        statusText.setTextColor(
                Color.rgb(
                        46,
                        125,
                        50
                )
        );

        statusText.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        GradientDrawable statusBackground =
                new GradientDrawable();

        statusBackground.setColor(
                Color.rgb(
                        232,
                        245,
                        233
                )
        );

        statusBackground.setCornerRadius(
                dp(10)
        );

        statusText.setBackground(
                statusBackground
        );

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        statusParams.setMargins(
                0,
                dp(14),
                0,
                dp(4)
        );

        statusText.setLayoutParams(
                statusParams
        );

        card.addView(
                statusText
        );

        // =====================================================
        // DETAILS
        // =====================================================

        card.addView(
                createInfoText(
                        "Category: " +
                                safeText(category)
                )
        );

        card.addView(
                createInfoText(
                        "Price: Rs. " +
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        price
                                )
                )
        );

        card.addView(
                createInfoText(
                        "Branch: " +
                                safeText(branch)
                )
        );

        card.addView(
                createInfoText(
                        "Date: " +
                                safeText(appointmentDate) +
                                "    Time: " +
                                safeText(appointmentTime)
                )
        );

        // =====================================================
        // TECHNICIAN
        // =====================================================

        String technicianText;

        if (technicianName != null &&
                !technicianName.trim().isEmpty()) {

            technicianText =
                    "Technician: " +
                            technicianName;

        } else if (technicianId != null &&
                !technicianId.trim().isEmpty()) {

            technicianText =
                    "Technician ID: " +
                            technicianId;

        } else {

            technicianText =
                    "Technician: Not available";
        }

        TextView technicianTextView =
                createInfoText(
                        technicianText
                );

        technicianTextView.setTypeface(
                null,
                Typeface.BOLD
        );

        technicianTextView.setTextColor(
                Color.rgb(
                        55,
                        71,
                        79
                )
        );

        card.addView(
                technicianTextView
        );

        // =====================================================
        // DIVIDER
        // =====================================================

        View divider =
                new View(this);

        divider.setBackgroundColor(
                Color.rgb(
                        230,
                        236,
                        240
                )
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.setMargins(
                0,
                dp(16),
                0,
                dp(14)
        );

        divider.setLayoutParams(
                dividerParams
        );

        card.addView(
                divider
        );

        // =====================================================
        // EXISTING FEEDBACK
        // =====================================================

        if (feedbackId > 0) {

            TextView feedbackTitle =
                    new TextView(this);

            feedbackTitle.setText(
                    "Your Feedback"
            );

            feedbackTitle.setTextSize(
                    15
            );

            feedbackTitle.setTypeface(
                    null,
                    Typeface.BOLD
            );

            feedbackTitle.setTextColor(
                    Color.rgb(
                            38,
                            50,
                            56
                    )
            );

            card.addView(
                    feedbackTitle
            );

            // =================================================
            // RATING
            // =================================================

            TextView ratingText =
                    new TextView(this);

            ratingText.setText(
                    "Rating: " +
                            getStars(rating)
            );

            ratingText.setTextSize(
                    17
            );

            ratingText.setTextColor(
                    Color.rgb(
                            245,
                            166,
                            35
                    )
            );

            ratingText.setPadding(
                    0,
                    dp(8),
                    0,
                    dp(4)
            );

            card.addView(
                    ratingText
            );

            // =================================================
            // COMMENT
            // =================================================

            TextView commentText =
                    createInfoText(
                            "Comment: " +
                                    safeText(comment)
                    );

            card.addView(
                    commentText
            );

            // =================================================
            // DATE
            // =================================================

            TextView dateText =
                    createInfoText(
                            "Submitted: " +
                                    safeText(feedbackDate)
                    );

            dateText.setTextSize(
                    13
            );

            card.addView(
                    dateText
            );

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

            LinearLayout.LayoutParams rowParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            rowParams.setMargins(
                    0,
                    dp(14),
                    0,
                    0
            );

            buttonRow.setLayoutParams(
                    rowParams
            );

            // =================================================
            // EDIT BUTTON
            // =================================================

            Button btnEdit =
                    createSecondaryButton(
                            "Edit Feedback"
                    );

            // =================================================
            // DELETE BUTTON
            // =================================================

            Button btnDelete =
                    createDeleteButton(
                            "Delete"
                    );

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            0,
                            dp(48),
                            1
                    );

            buttonParams.setMargins(
                    dp(4),
                    0,
                    dp(4),
                    0
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
            // EDIT ACTION
            // =================================================

            btnEdit.setOnClickListener(
                    v -> {

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

                        startActivity(
                                intent
                        );
                    }
            );

            // =================================================
            // DELETE ACTION
            // =================================================

            btnDelete.setOnClickListener(
                    v -> {

                        showDeleteConfirmation(
                                feedbackId
                        );
                    }
            );

        } else {

            // =================================================
            // NO FEEDBACK YET
            // =================================================

            TextView noFeedback =
                    createInfoText(
                            "You have not submitted feedback for this repair yet."
                    );

            noFeedback.setTextColor(
                    Color.rgb(
                            96,
                            125,
                            139
                    )
            );

            card.addView(
                    noFeedback
            );

            // =================================================
            // GIVE FEEDBACK BUTTON
            // =================================================

            Button btnGiveFeedback =
                    createPrimaryButton(
                            "Give Feedback"
                    );

            LinearLayout.LayoutParams buttonParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(50)
                    );

            buttonParams.setMargins(
                    0,
                    dp(14),
                    0,
                    0
            );

            btnGiveFeedback.setLayoutParams(
                    buttonParams
            );

            card.addView(
                    btnGiveFeedback
            );

            btnGiveFeedback.setOnClickListener(
                    v -> {

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

                        intent.putExtra(
                                "jobId",
                                jobId
                        );

                        intent.putExtra(
                                "appointmentId",
                                appointmentId
                        );

                        startActivity(
                                intent
                        );
                    }
            );
        }

        // =====================================================
        // ADD CARD TO CONTAINER
        // =====================================================

        feedbackContainer.addView(
                card
        );
    }

    // =====================================================
    // PRIMARY BUTTON
    // =====================================================

    private Button createPrimaryButton(
            String text) {

        Button button =
                new Button(this);

        button.setText(
                text
        );

        button.setAllCaps(
                false
        );

        button.setTextSize(
                14
        );

        button.setTextColor(
                Color.WHITE
        );

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        button.setBackgroundResource(
                R.drawable.bg_login_button
        );

        return button;
    }

    // =====================================================
    // SECONDARY BUTTON
    // =====================================================

    private Button createSecondaryButton(
            String text) {

        Button button =
                new Button(this);

        button.setText(
                text
        );

        button.setAllCaps(
                false
        );

        button.setTextSize(
                14
        );

        button.setTextColor(
                Color.rgb(
                        25,
                        118,
                        210
                )
        );

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(10)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        25,
                        118,
                        210
                )
        );

        button.setBackground(
                background
        );

        return button;
    }

    // =====================================================
    // DELETE BUTTON
    // =====================================================

    private Button createDeleteButton(
            String text) {

        Button button =
                new Button(this);

        button.setText(
                text
        );

        button.setAllCaps(
                false
        );

        button.setTextSize(
                14
        );

        button.setTextColor(
                Color.rgb(
                        198,
                        40,
                        40
                )
        );

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(
                        255,
                        245,
                        245
                )
        );

        background.setCornerRadius(
                dp(10)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        198,
                        40,
                        40
                )
        );

        button.setBackground(
                background
        );

        return button;
    }

    // =====================================================
    // DELETE CONFIRMATION
    // =====================================================

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
                                            "feedbackId = ? " +
                                                    "AND customerId = ?",

                                            new String[]{
                                                    String.valueOf(
                                                            feedbackId
                                                    ),
                                                    customerId
                                            }
                                    );

                            if (deleted > 0) {

                                Toast.makeText(
                                        CustomerFeedbackActivity.this,
                                        "Feedback deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadCompletedJobs(
                                        edtSearchFeedback
                                                .getText()
                                                .toString()
                                                .trim()
                                );

                            } else {

                                Toast.makeText(
                                        CustomerFeedbackActivity.this,
                                        "Unable to delete feedback",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // =====================================================
    // STAR DISPLAY
    // =====================================================

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

    // =====================================================
    // INFO TEXT
    // =====================================================

    private TextView createInfoText(
            String text) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(
                14
        );

        textView.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                dp(6),
                0,
                0
        );

        textView.setLayoutParams(
                params
        );

        return textView;
    }

    // =====================================================
    // EMPTY MESSAGE
    // =====================================================

    private void showMessage(
            String message) {

        TextView messageText =
                new TextView(this);

        messageText.setText(
                message
        );

        messageText.setTextSize(
                16
        );

        messageText.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        messageText.setGravity(
                Gravity.CENTER
        );

        messageText.setPadding(
                dp(20),
                dp(40),
                dp(20),
                dp(40)
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(18)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        226,
                        234,
                        240
                )
        );

        messageText.setBackground(
                background
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        feedbackContainer.addView(
                messageText,
                params
        );
    }

    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "Not available";
        }

        return text;
    }

    // =====================================================
    // DP HELPER
    // =====================================================

    private int dp(
            int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}