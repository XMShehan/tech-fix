package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageInventoryActivity extends AppCompatActivity {

    Button btnAddInventory;
    Button btnDeleteInventory;

    LinearLayout inventoryContainer;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_manage_inventory);

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
        btnAddInventory =
                findViewById(R.id.btnAddInventory);

        btnDeleteInventory =
                findViewById(R.id.btnDeleteInventory);

        inventoryContainer =
                findViewById(R.id.inventoryContainer);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Add Inventory button
        btnAddInventory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageInventoryActivity.this,
                    AddInventoryActivity.class
            );

            startActivity(intent);
        });

        // Delete Inventory button
        btnDeleteInventory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageInventoryActivity.this,
                    DeleteInventoryActivity.class
            );

            startActivity(intent);
        });

        // Load inventory
        loadInventory();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadInventory();
        }
    }

    private void loadInventory() {

        inventoryContainer.removeAllViews();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, productName, category, price, quantity " +
                        "FROM inventory",
                null
        );

        if (cursor.getCount() == 0) {

            TextView emptyText = new TextView(this);

            emptyText.setText("No inventory available");
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setPadding(10, 20, 10, 20);

            inventoryContainer.addView(emptyText);

        } else {

            while (cursor.moveToNext()) {

                int inventoryId =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                        );

                String productName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("productName")
                        );

                String category =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("category")
                        );

                double price =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("price")
                        );

                int quantity =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow("quantity")
                        );

                // Container for one inventory item
                LinearLayout inventoryLayout =
                        new LinearLayout(this);

                inventoryLayout.setOrientation(
                        LinearLayout.VERTICAL
                );

                inventoryLayout.setPadding(
                        12,
                        15,
                        12,
                        15
                );

                // Inventory ID
                TextView txtId =
                        new TextView(this);

                txtId.setText(
                        "Inventory ID: " + inventoryId
                );

                txtId.setTextSize(16);
                txtId.setTextColor(Color.BLACK);

                inventoryLayout.addView(txtId);

                // Product Name
                TextView txtProductName =
                        new TextView(this);

                txtProductName.setText(
                        "Product Name: " + productName
                );

                txtProductName.setTextSize(16);
                txtProductName.setTextColor(Color.BLACK);

                inventoryLayout.addView(txtProductName);

                // Category
                TextView txtCategory =
                        new TextView(this);

                txtCategory.setText(
                        "Category: " + category
                );

                txtCategory.setTextSize(16);
                txtCategory.setTextColor(Color.BLACK);

                inventoryLayout.addView(txtCategory);

                // Price
                TextView txtPrice =
                        new TextView(this);

                txtPrice.setText(
                        "Price: " + price
                );

                txtPrice.setTextSize(16);
                txtPrice.setTextColor(Color.BLACK);

                inventoryLayout.addView(txtPrice);

                // Quantity
                TextView txtQuantity =
                        new TextView(this);

                txtQuantity.setText(
                        "Quantity: " + quantity
                );

                txtQuantity.setTextSize(16);
                txtQuantity.setTextColor(Color.BLACK);

                inventoryLayout.addView(txtQuantity);

                // Add inventory item to container
                inventoryContainer.addView(
                        inventoryLayout
                );
            }
        }

        cursor.close();
    }
}