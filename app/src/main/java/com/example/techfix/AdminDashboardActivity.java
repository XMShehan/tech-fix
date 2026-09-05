package com.example.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        //Manage Services button
        Button btnMangeServices = findViewById(R.id.btnManageServices);

        btnMangeServices.setOnClickListener( v -> {
            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    ManageServicesActivity.class
            );
            startActivity(intent);
        });

        // Manage Branches button
        Button btnManageBranches = findViewById(R.id.btnManageBranches);

        btnManageBranches.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    ManageBranchesActivity.class
            );

            startActivity(intent);
        });

        Button btnTechnicians = findViewById(R.id.btnTechnicians);

        btnTechnicians.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    ManageTechniciansActivity.class
            );

            startActivity(intent);
        });

    }
}