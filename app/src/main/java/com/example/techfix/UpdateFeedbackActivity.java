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

public class UpdateFeedbackActivity extends AppCompatActivity {

    EditText edtFeedbackId;
    EditText edtCustomerName;
    EditText edtCustomerEmail;
    EditText edtFeedback;

    Button btnCancel;
    Button btnUpdateFeedback;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_update_feedback);

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

        edtFeedbackId =
                findViewById(R.id.edtFeedbackId);

        edtCustomerName =
                findViewById(R.id.edtCustomerName);

        edtCustomerEmail =
                findViewById(R.id.edtCustomerEmail);

        edtFeedback =
                findViewById(R.id.edtFeedback);

        btnCancel =
                findViewById(R.id.btnCancel);

        btnUpdateFeedback =
                findViewById(R.id.btnUpdateFeedback);

        databaseHelper =
                new DatabaseHelper(this);

        // Get data from CustomerFeedbackActivity

        int feedbackId =
                getIntent().getIntExtra(
                        "feedbackId",
                        -1
                );

        String customerName =
                getIntent().getStringExtra(
                        "customerName"
                );

        String customerEmail =
                getIntent().getStringExtra(
                        "customerEmail"
                );

        String feedback =
                getIntent().getStringExtra(
                        "feedback"
                );

        // Display existing data

        edtFeedbackId.setText(
                String.valueOf(feedbackId)
        );

        edtCustomerName.setText(
                customerName
        );

        edtCustomerEmail.setText(
                customerEmail
        );

        edtFeedback.setText(
                feedback
        );

        // Cancel

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // Update

        btnUpdateFeedback.setOnClickListener(v -> {

            String name =
                    edtCustomerName.getText()
                            .toString()
                            .trim();

            String email =
                    edtCustomerEmail.getText()
                            .toString()
                            .trim();

            String feedbackText =
                    edtFeedback.getText()
                            .toString()
                            .trim();

            if (name.isEmpty()
                    || email.isEmpty()
                    || feedbackText.isEmpty()) {

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
                    name
            );

            values.put(
                    "customerEmail",
                    email
            );

            values.put(
                    "feedback",
                    feedbackText
            );

            int result =
                    db.update(
                            "feedback",
                            values,
                            "id = ?",
                            new String[]{
                                    String.valueOf(feedbackId)
                            }
                    );

            if (result > 0) {

                Toast.makeText(
                        this,
                        "Feedback updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Feedback not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}