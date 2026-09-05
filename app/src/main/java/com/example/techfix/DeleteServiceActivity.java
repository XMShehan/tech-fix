package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteServiceActivity extends AppCompatActivity {

    EditText edtServiceId;

    Button btnCancel;
    Button btnDeleteService;

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

        // Service ID
        edtServiceId = findViewById(R.id.edtServiceId);

        btnCancel = findViewById(R.id.btnCancel);
        btnDeleteService = findViewById(R.id.btnDeleteService);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete Service button
        btnDeleteService.setOnClickListener(v -> {
            finish();
        });
    }
}