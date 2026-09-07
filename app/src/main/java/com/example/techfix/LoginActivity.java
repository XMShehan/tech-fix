package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;

    private Button btnLogin;
    private Button btnRegister;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_login
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        edtEmail =
                findViewById(R.id.edtEmail);

        edtPassword =
                findViewById(R.id.edtPassword);

        btnLogin =
                findViewById(R.id.btnLogin);

        btnRegister =
                findViewById(R.id.btnRegister);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // PASSWORD VISIBILITY
        // =====================================================

        edtPassword.setOnTouchListener(
                (v, event) -> {

                    if (event.getAction() ==
                            MotionEvent.ACTION_UP) {

                        /*
                         * Check whether the user touched
                         * the drawable on the right side.
                         */

                        if (event.getRawX() >=
                                (edtPassword.getRight()
                                        - edtPassword
                                        .getCompoundDrawables()[2]
                                        .getBounds()
                                        .width()
                                        - 40)) {

                            if (edtPassword
                                    .getTransformationMethod()
                                    == null) {

                                // Hide password

                                edtPassword
                                        .setTransformationMethod(
                                                PasswordTransformationMethod
                                                        .getInstance()
                                        );

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        R.drawable.ic_visibility_off,
                                        0
                                );

                            } else {

                                // Show password

                                edtPassword
                                        .setTransformationMethod(
                                                null
                                        );

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        R.drawable.ic_visibility,
                                        0
                                );
                            }

                            edtPassword.setSelection(
                                    edtPassword.length()
                            );

                            return true;
                        }
                    }

                    return false;
                }
        );

        // =====================================================
        // LOGIN
        // =====================================================

        btnLogin.setOnClickListener(
                v -> loginUser()
        );

        // =====================================================
        // REGISTER
        // =====================================================

        btnRegister.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    );

            startActivity(intent);
        });
    }

    // =====================================================
    // LOGIN USER
    // =====================================================

    private void loginUser() {

        String email =
                edtEmail.getText()
                        .toString()
                        .trim();

        String password =
                edtPassword.getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDATE EMPTY FIELDS
        // =====================================================

        if (email.isEmpty()) {

            edtEmail.setError(
                    "Enter your email"
            );

            edtEmail.requestFocus();

            return;
        }

        if (password.isEmpty()) {

            edtPassword.setError(
                    "Enter your password"
            );

            edtPassword.requestFocus();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        // =====================================================
        // CHECK CUSTOMER
        // =====================================================

        Cursor customerCursor =
                db.query(
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

            Intent intent =
                    new Intent(
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
        // CHECK ADMIN
        // =====================================================

        Cursor adminCursor =
                db.query(
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

            Intent intent =
                    new Intent(
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
        // CHECK TECHNICIAN
        // =====================================================

        Cursor technicianCursor =
                db.query(
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

            Intent intent =
                    new Intent(
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