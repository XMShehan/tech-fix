package com.example.techfix;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    EditText edtName;
    EditText edtComment;

    RatingBar ratingBar;

    Button btnSubmit;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        // Connect UI elements
        edtName = findViewById(R.id.edtName);
        edtComment = findViewById(R.id.edtComment);

        ratingBar = findViewById(R.id.ratingBar);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);

        // Submit Feedback
        btnSubmit.setOnClickListener(v -> {

            String name =
                    edtName.getText()
                            .toString()
                            .trim();

            String comment =
                    edtComment.getText()
                            .toString()
                            .trim();

            float rating =
                    ratingBar.getRating();

            if (name.isEmpty()) {

                edtName.setError("Please enter your name");
                edtName.requestFocus();
                return;
            }

            if (rating == 0) {

                Toast.makeText(
                        FeedbackActivity.this,
                        "Please give a rating",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (comment.isEmpty()) {

                edtComment.setError(
                        "Please enter your feedback"
                );

                edtComment.requestFocus();
                return;
            }

            // Feedback saving will be added later
            Toast.makeText(
                    FeedbackActivity.this,
                    "Thank you for your feedback!",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        });

        // Cancel
        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }
}