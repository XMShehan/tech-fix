package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageServicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_manage_services);

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

        // Add Service button
        Button btnAddService = findViewById(R.id.btnAddService);

        btnAddService.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageServicesActivity.this,
                    AddServiceActivity.class
            );

            startActivity(intent);
        });

        // Delete Service button
        Button btnDeleteService = findViewById(R.id.btnDeleteService);

        btnDeleteService.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManageServicesActivity.this,
                    DeleteServiceActivity.class
            );

            startActivity(intent);
        });
    }
}