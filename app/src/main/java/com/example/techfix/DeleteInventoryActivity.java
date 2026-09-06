package com.example.techfix;

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

public class DeleteInventoryActivity extends AppCompatActivity {

    EditText edtInventoryId;

    Button btnCancel;
    Button btnDeleteInventory;

    DatabaseHelper databaseHelper;

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

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete button
        btnDeleteInventory.setOnClickListener(v -> {

            String inventoryId =
                    edtInventoryId.getText().toString().trim();

            // Check empty
            if (inventoryId.isEmpty()) {

                Toast.makeText(
                        DeleteInventoryActivity.this,
                        "Please enter Inventory ID",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Delete inventory
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            int result = db.delete(
                    "inventory",
                    "id = ?",
                    new String[]{inventoryId}
            );

            if (result > 0) {

                Toast.makeText(
                        DeleteInventoryActivity.this,
                        "Inventory deleted successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        DeleteInventoryActivity.this,
                        "Inventory ID not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}