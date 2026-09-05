package com.example.techfix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyAppointmentsActivity extends AppCompatActivity {

    LinearLayout appointmentListContainer;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_appointments);

        appointmentListContainer =
                findViewById(R.id.appointmentListContainer);

        databaseHelper = new DatabaseHelper(this);

        loadAppointments();
    }

    private void loadAppointments() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT productService, category, price, branch, " +
                        "appointmentDate, appointmentTime " +
                        "FROM appointments",
                null
        );

        if (cursor.getCount() == 0) {

            TextView noAppointments =
                    new TextView(this);

            noAppointments.setText(
                    "No appointments found"
            );

            noAppointments.setTextSize(18);
            noAppointments.setGravity(Gravity.CENTER);
            noAppointments.setPadding(
                    0, 50, 0, 50
            );

            appointmentListContainer.addView(
                    noAppointments
            );

            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {

            String productService =
                    cursor.getString(0);

            String category =
                    cursor.getString(1);

            double price =
                    cursor.getDouble(2);

            String branch =
                    cursor.getString(3);

            String date =
                    cursor.getString(4);

            String time =
                    cursor.getString(5);

            createAppointmentCard(
                    productService,
                    category,
                    price,
                    branch,
                    date,
                    time
            );
        }

        cursor.close();
    }

    private void createAppointmentCard(
            String productService,
            String category,
            double price,
            String branch,
            String date,
            String time) {

        // Main card
        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                25, 25, 25, 25
        );

        card.setBackgroundColor(
                Color.rgb(245, 247, 250)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0, 0, 0, 20
        );

        card.setLayoutParams(cardParams);

        // Product / Service
        TextView productText =
                new TextView(this);

        productText.setText(
                productService
        );

        productText.setTextSize(21);
        productText.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(productText);

        // Status
        TextView statusText =
                new TextView(this);

        statusText.setText(
                "Confirmed"
        );

        statusText.setTextSize(14);
        statusText.setTypeface(
                null,
                Typeface.BOLD
        );

        statusText.setPadding(
                0, 8, 0, 8
        );

        card.addView(statusText);

        // Category
        TextView categoryText =
                new TextView(this);

        categoryText.setText(
                "Category: " + category
        );

        categoryText.setTextSize(15);

        card.addView(categoryText);

        // Price
        TextView priceText =
                new TextView(this);

        priceText.setText(
                "Price: Rs. " +
                        String.format("%.2f", price)
        );

        priceText.setTextSize(15);

        LinearLayout.LayoutParams priceParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        priceParams.setMargins(
                0, 8, 0, 0
        );

        priceText.setLayoutParams(
                priceParams
        );

        card.addView(priceText);

        // Branch
        TextView branchText =
                new TextView(this);

        branchText.setText(
                "Branch: " + branch
        );

        branchText.setTextSize(15);

        LinearLayout.LayoutParams branchParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        branchParams.setMargins(
                0, 8, 0, 0
        );

        branchText.setLayoutParams(
                branchParams
        );

        card.addView(branchText);

        // Date and Time
        TextView dateTimeText =
                new TextView(this);

        dateTimeText.setText(
                "Date: " + date +
                        "    Time: " + time
        );

        dateTimeText.setTextSize(15);

        LinearLayout.LayoutParams dateTimeParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        dateTimeParams.setMargins(
                0, 8, 0, 0
        );

        dateTimeText.setLayoutParams(
                dateTimeParams
        );

        card.addView(dateTimeText);

        appointmentListContainer.addView(card);
    }
}