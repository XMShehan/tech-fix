package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtPassword;

    private Button btnRegister;
    private Button btnBackToLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_register
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        edtName =
                findViewById(R.id.edtName);

        edtEmail =
                findViewById(R.id.edtEmail);

        edtPhone =
                findViewById(R.id.edtPhone);

        edtPassword =
                findViewById(R.id.edtPassword);

        btnRegister =
                findViewById(R.id.btnRegister);

        btnBackToLogin =
                findViewById(R.id.btnBackToLogin);

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

                        int drawableWidth =
                                edtPassword
                                        .getCompoundDrawables()[2]
                                        .getBounds()
                                        .width();

                        if (event.getX() >=
                                edtPassword.getWidth()
                                        - drawableWidth
                                        - 40) {

                            if (edtPassword.getInputType()
                                    == (InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                                edtPassword.setInputType(
                                        InputType.TYPE_CLASS_TEXT |
                                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                );

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        R.drawable.ic_visibility,
                                        0
                                );

                            } else {

                                edtPassword.setInputType(
                                        InputType.TYPE_CLASS_TEXT |
                                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                                );

                                edtPassword.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        R.drawable.ic_visibility_off,
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
        // REGISTER
        // =====================================================

        btnRegister.setOnClickListener(
                v -> registerCustomer()
        );

        // =====================================================
        // BACK TO LOGIN
        // =====================================================

        btnBackToLogin.setOnClickListener(
                v -> finish()
        );
    }

    // =====================================================
    // REGISTER CUSTOMER
    // =====================================================

    private void registerCustomer() {

        String name =
                edtName.getText()
                        .toString()
                        .trim();

        String email =
                edtEmail.getText()
                        .toString()
                        .trim();

        String phone =
                edtPhone.getText()
                        .toString()
                        .trim();

        String password =
                edtPassword.getText()
                        .toString()
                        .trim();

        // =====================================================
        // NAME VALIDATION
        // =====================================================

        if (name.isEmpty()) {

            edtName.setError(
                    "Enter your full name"
            );

            edtName.requestFocus();

            return;
        }

        // =====================================================
        // EMAIL VALIDATION
        // =====================================================

        if (email.isEmpty()) {

            edtEmail.setError(
                    "Enter your email address"
            );

            edtEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            edtEmail.setError(
                    "Enter a valid email address"
            );

            edtEmail.requestFocus();

            return;
        }

        // =====================================================
        // PHONE VALIDATION
        // =====================================================

        if (phone.isEmpty()) {

            edtPhone.setError(
                    "Enter your phone number"
            );

            edtPhone.requestFocus();

            return;
        }

        String digitsOnly =
                phone.replaceAll(
                        "[^0-9]",
                        ""
                );

        if (digitsOnly.length() < 9 ||
                digitsOnly.length() > 12) {

            edtPhone.setError(
                    "Enter a valid phone number"
            );

            edtPhone.requestFocus();

            return;
        }

        // =====================================================
        // PASSWORD VALIDATION
        // =====================================================

        if (password.isEmpty()) {

            edtPassword.setError(
                    "Enter a password"
            );

            edtPassword.requestFocus();

            return;
        }

        if (password.length() < 6) {

            edtPassword.setError(
                    "Password must be at least 6 characters"
            );

            edtPassword.requestFocus();

            return;
        }

        // =====================================================
        // DATABASE
        // =====================================================

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        // =====================================================
        // CHECK EXISTING EMAIL
        // =====================================================

        android.database.Cursor cursor =
                null;

        try {

            cursor = db.rawQuery(
                    "SELECT customerId " +
                            "FROM customers " +
                            "WHERE email = ?",
                    new String[]{
                            email
                    }
            );

            if (cursor.moveToFirst()) {

                edtEmail.setError(
                        "This email is already registered"
                );

                edtEmail.requestFocus();

                return;
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        // =====================================================
        // CUSTOMER DATA
        // =====================================================

        ContentValues values =
                new ContentValues();

        values.put(
                "customerName",
                name
        );

        values.put(
                "email",
                email
        );

        values.put(
                "phone",
                phone
        );

        values.put(
                "password",
                password
        );

        // =====================================================
        // INSERT CUSTOMER
        // =====================================================

        long result =
                db.insert(
                        "customers",
                        null,
                        values
                );

        // =====================================================
        // RESULT
        // =====================================================

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
            ).show();

            // Return to login
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration failed. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}