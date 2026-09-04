package com.example.techfix;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProductListActivity extends AppCompatActivity {

    LinearLayout productListContainer;
    Button btnBack;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_list);

        productListContainer = findViewById(R.id.productListContainer);
        btnBack = findViewById(R.id.btnBack);

        databaseHelper = new DatabaseHelper(this);

        loadProducts();

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadProducts() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT productName, category, price, quantity " +
                        "FROM inventory " +
                        "WHERE quantity > 0",
                null
        );

        while (cursor.moveToNext()) {

            String productName = cursor.getString(0);
            String category = cursor.getString(1);
            double price = cursor.getDouble(2);
            int quantity = cursor.getInt(3);

            createProductCard(
                    productName,
                    category,
                    price,
                    quantity
            );
        }

        cursor.close();
    }

    private void createProductCard(
            String productName,
            String category,
            double price,
            int quantity) {

        // Main product card
        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);

        card.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 20);

        card.setLayoutParams(cardParams);

        // Product name
        TextView nameText = new TextView(this);

        nameText.setText(productName);
        nameText.setTextSize(21);
        nameText.setTypeface(null, Typeface.BOLD);

        card.addView(nameText);

        // Category
        TextView categoryText = new TextView(this);

        categoryText.setText("Category: " + category);
        categoryText.setTextSize(15);

        LinearLayout.LayoutParams categoryParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        categoryParams.setMargins(0, 10, 0, 0);

        categoryText.setLayoutParams(categoryParams);

        card.addView(categoryText);

        // Price
        TextView priceText = new TextView(this);

        priceText.setText("Rs. " + String.format("%.2f", price));
        priceText.setTextSize(19);
        priceText.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams priceParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        priceParams.setMargins(0, 15, 0, 0);

        priceText.setLayoutParams(priceParams);

        card.addView(priceText);

        // Quantity
        TextView quantityText = new TextView(this);

        quantityText.setText("Available: " + quantity);
        quantityText.setTextSize(15);

        LinearLayout.LayoutParams quantityParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        quantityParams.setMargins(0, 5, 0, 10);

        quantityText.setLayoutParams(quantityParams);

        card.addView(quantityText);

        // Select button
        Button btnSelect = new Button(this);

        btnSelect.setText("Select Product");

        btnSelect.setGravity(Gravity.CENTER);

        card.addView(btnSelect);

        // Add card to screen
        productListContainer.addView(card);
    }
}