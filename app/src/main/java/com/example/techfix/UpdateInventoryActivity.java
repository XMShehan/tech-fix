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

public class UpdateInventoryActivity extends AppCompatActivity {

    EditText edtProductName;
    EditText edtCategory;
    EditText edtPrice;
    EditText edtQuantity;

    Button btnCancel;
    Button btnUpdateInventory;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_inventory);

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
        edtProductName =
                findViewById(R.id.edtProductName);

        edtCategory =
                findViewById(R.id.edtCategory);

        edtPrice =
                findViewById(R.id.edtPrice);

        edtQuantity =
                findViewById(R.id.edtQuantity);

        btnCancel =
                findViewById(R.id.btnCancel);

        btnUpdateInventory =
                findViewById(R.id.btnUpdateInventory);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Get selected inventory ID
        int inventoryId =
                getIntent().getIntExtra("inventoryId", -1);

        // Get selected inventory data
        String productName =
                getIntent().getStringExtra("productName");

        String category =
                getIntent().getStringExtra("category");

        String price =
                getIntent().getStringExtra("price");

        String quantity =
                getIntent().getStringExtra("quantity");

        // Display existing data
        if (productName != null) {
            edtProductName.setText(productName);
        }

        if (category != null) {
            edtCategory.setText(category);
        }

        if (price != null) {
            edtPrice.setText(price);
        }

        if (quantity != null) {
            edtQuantity.setText(quantity);
        }

        // Cancel
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Update Inventory
        btnUpdateInventory.setOnClickListener(v -> {

            String newProductName =
                    edtProductName.getText().toString().trim();

            String newCategory =
                    edtCategory.getText().toString().trim();

            String newPrice =
                    edtPrice.getText().toString().trim();

            String newQuantity =
                    edtQuantity.getText().toString().trim();

            // Validation
            if (newProductName.isEmpty() ||
                    newCategory.isEmpty() ||
                    newPrice.isEmpty() ||
                    newQuantity.isEmpty()) {

                Toast.makeText(
                        UpdateInventoryActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (inventoryId == -1) {

                Toast.makeText(
                        UpdateInventoryActivity.this,
                        "Inventory ID not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            try {

                double priceValue =
                        Double.parseDouble(newPrice);

                int quantityValue =
                        Integer.parseInt(newQuantity);

                // Open database
                SQLiteDatabase db =
                        databaseHelper.getWritableDatabase();

                // Updated values
                ContentValues values =
                        new ContentValues();

                values.put(
                        "productName",
                        newProductName
                );

                values.put(
                        "category",
                        newCategory
                );

                values.put(
                        "price",
                        priceValue
                );

                values.put(
                        "quantity",
                        quantityValue
                );

                // Update inventory
                int result =
                        db.update(
                                "inventory",
                                values,
                                "id = ?",
                                new String[]{
                                        String.valueOf(inventoryId)
                                }
                        );

                if (result > 0) {

                    Toast.makeText(
                            UpdateInventoryActivity.this,
                            "Inventory updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            UpdateInventoryActivity.this,
                            "Inventory not found",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } catch (NumberFormatException e) {

                Toast.makeText(
                        UpdateInventoryActivity.this,
                        "Please enter valid numbers",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}