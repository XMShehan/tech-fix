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
        btnLogin.setOnClickListener(v -> loginCustomer());

        // Register button
        btnRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginCustomer() {

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

        Cursor cursor = db.query(
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

        if (cursor.moveToFirst()) {

            String customerId =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("customerId")
                    );

            String customerName =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("customerName")
                    );

            Toast.makeText(
                    this,
                    "Welcome " + customerName,
                    Toast.LENGTH_SHORT
            ).show();

            cursor.close();

            // Open Customer Dashboard
            Intent intent = new Intent(
                    LoginActivity.this,
                    CustomerDashboard.class
            );

            // Send customer information
            intent.putExtra("customerId", customerId);
            intent.putExtra("customerName", customerName);
            intent.putExtra("customerEmail", email);

            startActivity(intent);

            finish();

        } else {

            cursor.close();

            Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}