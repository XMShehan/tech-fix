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

public class UpdateBranchActivity extends AppCompatActivity {

    EditText edtBranchId;
    EditText edtBranchName;
    EditText edtAddress;
    EditText edtPhone;
    EditText edtEmail;

    Spinner spinnerStatus;

    Button btnCancel;
    Button btnUpdateBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_branch);

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
        edtBranchId = findViewById(R.id.edtBranchId);
        edtBranchName = findViewById(R.id.edtBranchName);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateBranch = findViewById(R.id.btnUpdateBranch);

        // Status options
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

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Update Branch button
        btnUpdateBranch.setOnClickListener(v -> {

            String branchId = edtBranchId.getText().toString();
            String branchName = edtBranchName.getText().toString();
            String address = edtAddress.getText().toString();
            String phone = edtPhone.getText().toString();
            String email = edtEmail.getText().toString();
            String status = spinnerStatus.getSelectedItem().toString();

            // Database will be connected later.
            finish();
        });
    }
}