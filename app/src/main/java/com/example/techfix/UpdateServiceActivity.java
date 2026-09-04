package com.example.techfix;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateServiceActivity extends AppCompatActivity {

    EditText edtServiceName;
    EditText edtDescription;
    EditText edtPrice;
    EditText edtDuration;

    Spinner spinnerStatus;

    Button btnCancel;
    Button btnUpdateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_service);

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

        edtServiceName = findViewById(R.id.edtServiceName);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtDuration = findViewById(R.id.edtDuration);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateService = findViewById(R.id.btnUpdateService);

        String[] statusOptions = {
                "Active",
                "Inactive"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnUpdateService.setOnClickListener(v -> {
            finish();
        });
    }
}