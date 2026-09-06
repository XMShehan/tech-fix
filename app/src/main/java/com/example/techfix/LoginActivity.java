package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail;
    EditText edtPassword;

    Button btnLogin;
    Button btnRegister;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Login button
        btnLogin.setOnClickListener(v -> loginUser());

        // Register button
        btnRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        String email =
                edtEmail.getText().toString().trim();

        String password =
                edtPassword.getText().toString().trim();

        // Check empty fields
        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        // =====================================================
        // FIRST: CHECK CUSTOMER
        // =====================================================

        Cursor customerCursor = db.query(
                "customers",
                new String[]{
                        "customerId",
                        "customerName",
                        "email"
                },
                "email = ? AND password = ?",
                new String[]{
                        email,
                        password
                },
                null,
                null,
                null
        );

        if (customerCursor.moveToFirst()) {

            String customerId =
                    customerCursor.getString(
                            customerCursor.getColumnIndexOrThrow(
                                    "customerId"
                            )
                    );

            String customerName =
                    customerCursor.getString(
                            customerCursor.getColumnIndexOrThrow(
                                    "customerName"
                            )
                    );

            customerCursor.close();

            Toast.makeText(
                    this,
                    "Welcome " + customerName,
                    Toast.LENGTH_SHORT
            ).show();

            // Open Customer Dashboard
            Intent intent = new Intent(
                    LoginActivity.this,
                    CustomerDashboard.class
            );

            intent.putExtra(
                    "customerId",
                    customerId
            );

            intent.putExtra(
                    "customerName",
                    customerName
            );

            intent.putExtra(
                    "customerEmail",
                    email
            );

            startActivity(intent);

            finish();

            return;
        }

        customerCursor.close();

        // =====================================================
        // SECOND: CHECK ADMIN
        // =====================================================

        Cursor adminCursor = db.query(
                "admins",
                new String[]{
                        "adminId",
                        "adminName",
                        "email"
                },
                "email = ? AND password = ?",
                new String[]{
                        email,
                        password
                },
                null,
                null,
                null
        );

        if (adminCursor.moveToFirst()) {

            String adminId =
                    adminCursor.getString(
                            adminCursor.getColumnIndexOrThrow(
                                    "adminId"
                            )
                    );

            String adminName =
                    adminCursor.getString(
                            adminCursor.getColumnIndexOrThrow(
                                    "adminName"
                            )
                    );

            adminCursor.close();

            Toast.makeText(
                    this,
                    "Welcome " + adminName,
                    Toast.LENGTH_SHORT
            ).show();

            // Open Admin Dashboard
            Intent intent = new Intent(
                    LoginActivity.this,
                    AdminDashboardActivity.class
            );

            intent.putExtra(
                    "adminId",
                    adminId
            );

            intent.putExtra(
                    "adminName",
                    adminName
            );

            intent.putExtra(
                    "adminEmail",
                    email
            );

            startActivity(intent);

            finish();

            return;
        }

        adminCursor.close();

        // =====================================================
        // THIRD: CHECK TECHNICIAN
        // =====================================================

        Cursor technicianCursor = db.query(
                "technicians",
                new String[]{
                        "technicianId",
                        "technicianName",
                        "email"
                },
                "email = ? AND password = ?",
                new String[]{
                        email,
                        password
                },
                null,
                null,
                null
        );

        if (technicianCursor.moveToFirst()) {

            String technicianId =
                    technicianCursor.getString(
                            technicianCursor.getColumnIndexOrThrow(
                                    "technicianId"
                            )
                    );

            String technicianName =
                    technicianCursor.getString(
                            technicianCursor.getColumnIndexOrThrow(
                                    "technicianName"
                            )
                    );

            technicianCursor.close();

            Toast.makeText(
                    this,
                    "Welcome " + technicianName,
                    Toast.LENGTH_SHORT
            ).show();

            // Open Technician Dashboard
            Intent intent = new Intent(
                    LoginActivity.this,
                    TechnicianDashboardActivity.class
            );

            intent.putExtra(
                    "technicianId",
                    technicianId
            );

            intent.putExtra(
                    "technicianName",
                    technicianName
            );

            intent.putExtra(
                    "technicianEmail",
                    email
            );

            startActivity(intent);

            finish();

            return;
        }

        technicianCursor.close();

        // =====================================================
        // INVALID LOGIN
        // =====================================================

        Toast.makeText(
                this,
                "Invalid email or password",
                Toast.LENGTH_SHORT
        ).show();
    }
}