package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName;
    EditText edtEmail;
    EditText edtPhone;
    EditText edtPassword;

    Button btnRegister;
    Button btnBackToLogin;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // Find views
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Database
        databaseHelper = new DatabaseHelper(this);

        // Register button
        btnRegister.setOnClickListener(v -> registerCustomer());

        // Back to Login
        btnBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerCustomer() {

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validate fields
        if (name.isEmpty() ||
                email.isEmpty() ||
                phone.isEmpty() ||
                password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Get database
        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        // Prepare customer data
        ContentValues values = new ContentValues();

        values.put("customerName", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("password", password);

        // Insert customer
        long result =
                db.insert(
                        "customers",
                        null,
                        values
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
            ).show();

            // Return to Login
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration failed. Email may already exist.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}