package com.example.techfix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Locale;

public class RepairHistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;

    private DatabaseHelper databaseHelper;

    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_repair_history);

        historyContainer = findViewById(
                R.id.historyContainer
        );

        databaseHelper = new DatabaseHelper(this);

        // Get logged-in customer ID
        customerId = getIntent().getStringExtra(
                "customerId"
        );

        loadRepairHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadRepairHistory();
        }
    }

    // =====================================================
    // LOAD REPAIR HISTORY
    // =====================================================

    private void loadRepairHistory() {

        historyContainer.removeAllViews();

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            showMessage(
                    "Customer information is missing"
            );

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            /*
             * Get completed repairs for the logged-in
             * customer and retrieve technician information.
             */
            cursor = db.rawQuery(
                    "SELECT " +
                            "j.jobId, " +
                            "j.status, " +
                            "j.photoPath, " +
                            "a.appointmentId, " +
                            "a.productService, " +
                            "a.category, " +
                            "a.price, " +
                            "a.branch, " +
                            "a.appointmentDate, " +
                            "a.appointmentTime, " +
                            "j.technicianId, " +
                            "t.technicianName " +

                            "FROM jobs j " +

                            "INNER JOIN appointments a " +
                            "ON j.appointmentId = a.appointmentId " +

                            "LEFT JOIN technicians t " +
                            "ON j.technicianId = t.technicianId " +

                            "WHERE a.customerId = ? " +
                            "AND j.status = 'FINISHED' " +

                            "ORDER BY j.jobId DESC",

                    new String[]{
                            customerId
                    }
            );

            if (!cursor.moveToFirst()) {

                showMessage(
                        "No completed repairs yet"
                );

                return;
            }

            do {

                int jobId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "jobId"
                        )
                );

                int appointmentId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "appointmentId"
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

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentDate"
                                )
                        );

                String time =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "appointmentTime"
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

                String photoPath =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "photoPath"
                                )
                        );

                createHistoryCard(
                        jobId,
                        appointmentId,
                        productService,
                        category,
                        price,
                        branch,
                        date,
                        time,
                        technicianId,
                        technicianName,
                        photoPath
                );

            } while (cursor.moveToNext());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =====================================================
    // CREATE HISTORY CARD
    // =====================================================

    private void createHistoryCard(
            int jobId,
            int appointmentId,
            String productService,
            String category,
            double price,
            String branch,
            String date,
            String time,
            String technicianId,
            String technicianName,
            String photoPath) {

        // =====================================================
        // MAIN CARD
        // =====================================================

        LinearLayout card = new LinearLayout(this);

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
                Color.rgb(226, 234, 240)
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

        TextView title =
                new TextView(this);

        title.setText(
                "Repair #" + jobId
        );

        title.setTextSize(20);

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setTextColor(
                Color.rgb(38, 50, 56)
        );

        card.addView(title);

        // =====================================================
        // PRODUCT
        // =====================================================

        TextView productText =
                new TextView(this);

        productText.setText(
                safeText(productService)
        );

        productText.setTextSize(17);

        productText.setTypeface(
                null,
                Typeface.BOLD
        );

        productText.setTextColor(
                Color.rgb(25, 118, 210)
        );

        LinearLayout.LayoutParams productParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        productParams.setMargins(
                0,
                dp(8),
                0,
                0
        );

        productText.setLayoutParams(
                productParams
        );

        card.addView(productText);

        // =====================================================
        // FINISHED STATUS
        // =====================================================

        TextView status =
                new TextView(this);

        status.setText(
                "Repair Status: FINISHED"
        );

        status.setTextSize(14);

        status.setTypeface(
                null,
                Typeface.BOLD
        );

        status.setTextColor(
                Color.rgb(46, 125, 50)
        );

        status.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        GradientDrawable statusBackground =
                new GradientDrawable();

        statusBackground.setColor(
                Color.rgb(232, 245, 233)
        );

        statusBackground.setCornerRadius(
                dp(10)
        );

        status.setBackground(
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

        status.setLayoutParams(
                statusParams
        );

        card.addView(status);

        // =====================================================
        // INFORMATION
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
                                safeText(date) +
                                "    Time: " +
                                safeText(time)
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
                Color.rgb(55, 71, 79)
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
                Color.rgb(230, 236, 240)
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
        // REPAIR PHOTO
        // =====================================================

        if (photoPath != null &&
                !photoPath.trim().isEmpty()) {

            File photoFile =
                    new File(photoPath);

            if (photoFile.exists()) {

                TextView photoLabel =
                        new TextView(this);

                photoLabel.setText(
                        "Repair Photo"
                );

                photoLabel.setTextSize(
                        14
                );

                photoLabel.setTextColor(
                        Color.rgb(38, 50, 56)
                );

                photoLabel.setTypeface(
                        null,
                        Typeface.BOLD
                );

                photoLabel.setPadding(
                        0,
                        0,
                        0,
                        dp(8)
                );

                card.addView(
                        photoLabel
                );

                Bitmap bitmap =
                        BitmapFactory.decodeFile(
                                photoPath
                        );

                if (bitmap != null) {

                    ImageView photoView =
                            new ImageView(this);

                    photoView.setImageBitmap(
                            bitmap
                    );

                    photoView.setScaleType(
                            ImageView.ScaleType.CENTER_CROP
                    );

                    LinearLayout.LayoutParams imageParams =
                            new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    dp(220)
                            );

                    imageParams.setMargins(
                            0,
                            0,
                            0,
                            dp(4)
                    );

                    photoView.setLayoutParams(
                            imageParams
                    );

                    GradientDrawable imageBackground =
                            new GradientDrawable();

                    imageBackground.setColor(
                            Color.rgb(245, 247, 250)
                    );

                    imageBackground.setCornerRadius(
                            dp(14)
                    );

                    photoView.setBackground(
                            imageBackground
                    );

                    photoView.setClipToOutline(
                            true
                    );

                    photoView.setContentDescription(
                            "Repair photo"
                    );

                    card.addView(
                            photoView
                    );

                } else {

                    card.addView(
                            createInfoText(
                                    "Repair photo could not be loaded"
                            )
                    );
                }

            } else {

                card.addView(
                        createInfoText(
                                "Repair photo is unavailable"
                        )
                );
            }

        } else {

            TextView noPhoto =
                    createInfoText(
                            "No repair photo available"
                    );

            noPhoto.setTextColor(
                    Color.rgb(120, 130, 138)
            );

            card.addView(noPhoto);
        }

        // =====================================================
        // APPOINTMENT NUMBER
        // =====================================================

        TextView appointmentText =
                createInfoText(
                        "Appointment #" +
                                appointmentId
                );

        appointmentText.setTextSize(
                13
        );

        appointmentText.setTextColor(
                Color.rgb(120, 130, 138)
        );

        LinearLayout.LayoutParams appointmentParams =
                (LinearLayout.LayoutParams)
                        appointmentText.getLayoutParams();

        appointmentParams.setMargins(
                0,
                dp(12),
                0,
                0
        );

        appointmentText.setLayoutParams(
                appointmentParams
        );

        card.addView(
                appointmentText
        );

        // =====================================================
        // ADD CARD
        // =====================================================

        historyContainer.addView(
                card
        );
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
                Color.rgb(96, 125, 139)
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

        LinearLayout messageCard =
                new LinearLayout(this);

        messageCard.setOrientation(
                LinearLayout.VERTICAL
        );

        messageCard.setGravity(
                Gravity.CENTER
        );

        messageCard.setPadding(
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
                Color.rgb(226, 234, 240)
        );

        messageCard.setBackground(
                background
        );

        TextView messageText =
                new TextView(this);

        messageText.setText(
                message
        );

        messageText.setTextSize(
                16
        );

        messageText.setTextColor(
                Color.rgb(96, 125, 139)
        );

        messageText.setGravity(
                Gravity.CENTER
        );

        messageCard.addView(
                messageText
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        messageCard.setLayoutParams(
                params
        );

        historyContainer.addView(
                messageCard
        );
    }

    // =====================================================
    // DP HELPER
    // =====================================================

    private int dp(int value) {

        return (int) (
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "Not available";
        }

        return text;
    }
}