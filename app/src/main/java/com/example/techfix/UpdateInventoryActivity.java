package com.example.techfix;

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

        edtProductName = findViewById(R.id.edtProductName);
        edtCategory = findViewById(R.id.edtCategory);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);

        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnUpdateInventory = findViewById(R.id.btnUpdateInventory);

        // Get selected inventory data
        String productName = getIntent().getStringExtra("productName");
        String category = getIntent().getStringExtra("category");
        String price = getIntent().getStringExtra("price");
        String quantity = getIntent().getStringExtra("quantity");

        // Display existing data
        edtProductName.setText(productName);
        edtCategory.setText(category);
        edtPrice.setText(price);
        edtQuantity.setText(quantity);

        // Cancel
        btnCancel.setOnClickListener(v -> finish());

        // Update
        btnUpdateInventory.setOnClickListener(v -> {

            Toast.makeText(
                    UpdateInventoryActivity.this,
                    "Inventory updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        });
    }
}