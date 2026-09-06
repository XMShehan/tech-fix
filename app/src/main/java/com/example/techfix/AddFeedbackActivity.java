package com.example.techfix;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddFeedbackActivity extends AppCompatActivity {

    EditText edtCustomerName;
    EditText edtCustomerEmail;
    EditText edtFeedback;

    Button btnCancel;
    Button btnSubmitFeedback;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_feedback);

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

        edtCustomerName =
                findViewById(R.id.edtCustomerName);

        edtCustomerEmail =
                findViewById(R.id.edtCustomerEmail);

        edtFeedback =
                findViewById(R.id.edtFeedback);

        btnCancel =
                findViewById(R.id.btnCancel);

        btnSubmitFeedback =
                findViewById(R.id.btnSubmitFeedback);

        databaseHelper =
                new DatabaseHelper(this);

        // Cancel

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Submit Feedback

        btnSubmitFeedback.setOnClickListener(v -> {

            String customerName =
                    edtCustomerName.getText()
                            .toString()
                            .trim();

            String customerEmail =
                    edtCustomerEmail.getText()
                            .toString()
                            .trim();

            String feedback =
                    edtFeedback.getText()
                            .toString()
                            .trim();

            if (customerName.isEmpty()
                    || customerEmail.isEmpty()
                    || feedback.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "customerName",
                    customerName
            );

            values.put(
                    "customerEmail",
                    customerEmail
            );

            values.put(
                    "feedback",
                    feedback
            );

            long result =
                    db.insert(
                            "feedback",
                            null,
                            values
                    );

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Feedback added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to add feedback",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}