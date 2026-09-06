package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddInventoryActivity extends AppCompatActivity {

    EditText edtInventoryId;
    EditText edtProductName;
    EditText edtCategory;
    EditText edtPrice;
    EditText edtQuantity;

    Button btnCancel;
    Button btnSaveInventory;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_inventory);

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
        edtInventoryId = findViewById(R.id.edtInventoryId);
        edtProductName = findViewById(R.id.edtProductName);
        edtCategory = findViewById(R.id.edtCategory);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);

        btnCancel = findViewById(R.id.btnCancel);
        btnSaveInventory = findViewById(R.id.btnSaveInventory);

        databaseHelper = new DatabaseHelper(this);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Save Inventory button
        btnSaveInventory.setOnClickListener(v -> {

            String inventoryIdText =
                    edtInventoryId.getText().toString().trim();

            String productName =
                    edtProductName.getText().toString().trim();

            String category =
                    edtCategory.getText().toString().trim();

            String priceText =
                    edtPrice.getText().toString().trim();

            String quantityText =
                    edtQuantity.getText().toString().trim();

            // Validation
            if (inventoryIdText.isEmpty() ||
                    productName.isEmpty() ||
                    category.isEmpty() ||
                    priceText.isEmpty() ||
                    quantityText.isEmpty()) {

                Toast.makeText(
                        AddInventoryActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            try {

                int inventoryId =
                        Integer.parseInt(inventoryIdText);

                double price =
                        Double.parseDouble(priceText);

                int quantity =
                        Integer.parseInt(quantityText);

                // Insert into database
                SQLiteDatabase db =
                        databaseHelper.getWritableDatabase();

                ContentValues values =
                        new ContentValues();

                values.put("id", inventoryId);
                values.put("productName", productName);
                values.put("category", category);
                values.put("price", price);
                values.put("quantity", quantity);

                long result =
                        db.insert(
                                "inventory",
                                null,
                                values
                        );

                if (result != -1) {

                    Toast.makeText(
                            AddInventoryActivity.this,
                            "Inventory added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            AddInventoryActivity.this,
                            "Inventory ID already exists",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } catch (NumberFormatException e) {

                Toast.makeText(
                        AddInventoryActivity.this,
                        "Please enter valid numbers",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}