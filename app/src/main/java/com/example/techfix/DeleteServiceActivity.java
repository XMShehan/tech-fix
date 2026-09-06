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

public class DeleteServiceActivity extends AppCompatActivity {

    EditText edtServiceId;

    Button btnCancel;
    Button btnDeleteService;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_delete_service);

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
        edtServiceId = findViewById(R.id.edtServiceId);

        btnCancel = findViewById(R.id.btnCancel);
        btnDeleteService = findViewById(R.id.btnDeleteService);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete Service button
        btnDeleteService.setOnClickListener(v -> {

            String serviceId =
                    edtServiceId.getText().toString().trim();

            // Validation
            if (serviceId.isEmpty()) {

                Toast.makeText(
                        DeleteServiceActivity.this,
                        "Please enter Service ID",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Delete from database
            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            int result = db.delete(
                    "services",
                    "serviceId = ?",
                    new String[]{serviceId}
            );

            if (result > 0) {

                Toast.makeText(
                        DeleteServiceActivity.this,
                        "Service deleted successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        DeleteServiceActivity.this,
                        "Service not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}