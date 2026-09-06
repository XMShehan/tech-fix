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

public class DeleteFeedbackActivity extends AppCompatActivity {

    EditText edtFeedbackId;
    Button btnDeleteFeedback;
    Button btnCancel;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_delete_feedback);

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

        // Find views
        edtFeedbackId =
                findViewById(R.id.edtFeedbackId);

        btnDeleteFeedback =
                findViewById(R.id.btnDeleteFeedback);

        btnCancel =
                findViewById(R.id.btnCancel);

        // Database
        databaseHelper =
                new DatabaseHelper(this);

        // Delete Feedback
        btnDeleteFeedback.setOnClickListener(v -> {

            String feedbackId =
                    edtFeedbackId.getText()
                            .toString()
                            .trim();

            // Validation
            if (feedbackId.isEmpty()) {

                Toast.makeText(
                        DeleteFeedbackActivity.this,
                        "Please enter Feedback ID",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            int result =
                    db.delete(
                            "feedback",
                            "id = ?",
                            new String[]{feedbackId}
                    );

            if (result > 0) {

                Toast.makeText(
                        DeleteFeedbackActivity.this,
                        "Feedback deleted successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        DeleteFeedbackActivity.this,
                        "Feedback not found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Cancel
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }
}