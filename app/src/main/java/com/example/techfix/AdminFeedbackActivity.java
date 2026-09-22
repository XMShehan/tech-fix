package com.example.techfix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminFeedbackActivity extends AppCompatActivity {

    private EditText edtSearchFeedback;
    private LinearLayout feedbackContainer;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_admin_feedback
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
        // SEARCH
        // =====================================================

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

        // =====================================================
        // INITIAL LOAD
        // =====================================================

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

    // =========================================================
    // LOAD ALL FEEDBACK
    // =========================================================

    private void loadFeedback(
            String searchText) {

        feedbackContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor;

        if (searchText.isEmpty()) {

            cursor = db.rawQuery(
                    "SELECT feedbackId, customerId, " +
                            "customerName, rating, comment, date " +
                            "FROM feedback " +
                            "ORDER BY feedbackId DESC",
                    null
            );

        } else {

            cursor = db.rawQuery(
                    "SELECT feedbackId, customerId, " +
                            "customerName, rating, comment, date " +
                            "FROM feedback " +
                            "WHERE customerName LIKE ? " +
                            "OR comment LIKE ? " +
                            "OR date LIKE ? " +
                            "ORDER BY feedbackId DESC",

                    new String[]{
                            "%" + searchText + "%",
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
                    "No customer feedback available."
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
        // DISPLAY ALL FEEDBACK
        // =====================================================

        while (cursor.moveToNext()) {

            int feedbackId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "feedbackId"
                            )
                    );

            int customerId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "customerId"
                            )
                    );

            String customerName =
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
            // CARD
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
            // FEEDBACK ID
            // =================================================

            TextView txtId =
                    new TextView(this);

            txtId.setText(
                    "Feedback #" + feedbackId
            );

            txtId.setTextSize(18);
            txtId.setTextColor(Color.BLACK);
            txtId.setTypeface(
                    null,
                    Typeface.BOLD
            );

            card.addView(txtId);

            // =================================================
            // CUSTOMER
            // =================================================

            addText(
                    card,
                    "Customer: " + customerName
            );

            addText(
                    card,
                    "Customer ID: " + customerId
            );

            // =================================================
            // RATING
            // =================================================

            addText(
                    card,
                    "Rating: " + getStars(rating)
            );

            // =================================================
            // COMMENT
            // =================================================

            addText(
                    card,
                    "Comment: " + comment
            );

            // =================================================
            // DATE
            // =================================================

            addText(
                    card,
                    "Date: " + date
            );

            feedbackContainer.addView(
                    card
            );
        }

        cursor.close();
    }

    // =========================================================
    // ADD TEXT
    // =========================================================

    private void addText(
            LinearLayout parent,
            String text) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(15);
        textView.setTextColor(Color.DKGRAY);

        textView.setPadding(
                0,
                7,
                0,
                7
        );

        parent.addView(
                textView
        );
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