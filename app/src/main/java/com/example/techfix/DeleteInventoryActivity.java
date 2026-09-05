package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteInventoryActivity extends AppCompatActivity {

    EditText edtInventoryId;

    Button btnCancel;
    Button btnDeleteInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_delete_inventory);

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

        // Inventory ID
        edtInventoryId = findViewById(R.id.edtInventoryId);

        btnCancel = findViewById(R.id.btnCancel);
        btnDeleteInventory = findViewById(R.id.btnDeleteInventory);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete button
        btnDeleteInventory.setOnClickListener(v -> {
            finish();
        });
    }
}