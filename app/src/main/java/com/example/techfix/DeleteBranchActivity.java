package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeleteBranchActivity extends AppCompatActivity {

    EditText edtBranchId;

    Button btnCancel;
    Button btnDeleteBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_delete_branch);

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

        // Branch ID
        edtBranchId = findViewById(R.id.edtBranchId);

        btnCancel = findViewById(R.id.btnCancel);
        btnDeleteBranch = findViewById(R.id.btnDeleteBranch);

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Delete Branch button
        btnDeleteBranch.setOnClickListener(v -> {
            finish();
        });
    }
}