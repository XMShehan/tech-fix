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

public class AddInventoryActivity extends AppCompatActivity{

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

                    Insets systemBars = insets.getInsets(
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

            String productName = edtProductName.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String priceText = edtPrice.getText().toString().trim();
            String quantityText = edtQuantity.getText().toString().trim();

            // Validation
            if (productName.isEmpty() ||
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

            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            // Insert into database
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("productName", productName);
            values.put("category", category);
            values.put("price", price);
            values.put("quantity", quantity);

            long result = db.insert(
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
                        "Failed to add inventory",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}