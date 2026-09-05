package com.example.techfix;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    RadioButton radioAutoDetect;
    RadioButton radioManual;

    Spinner spinnerBranch;

    EditText edtProductService;
    EditText edtCategory;
    EditText edtPrice;
    EditText edtDate;
    EditText edtTime;

    Button btnAddPhoto;
    Button btnCancel;
    Button btnConfirm;

    ImageView imgProductPhoto;

    DatabaseHelper databaseHelper;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appointment);

        // ---------------------------------------------
        // CONNECT UI ELEMENTS
        // ---------------------------------------------

        radioAutoDetect = findViewById(R.id.radioAutoDetect);
        radioManual = findViewById(R.id.radioManual);

        spinnerBranch = findViewById(R.id.spinnerBranch);

        edtProductService = findViewById(R.id.edtProductService);
        edtCategory = findViewById(R.id.edtCategory);
        edtPrice = findViewById(R.id.edtPrice);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);

        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        imgProductPhoto = findViewById(R.id.imgProductPhoto);

        databaseHelper = new DatabaseHelper(this);

        // ---------------------------------------------
        // GET PRODUCT DETAILS
        // ---------------------------------------------

        String productName =
                getIntent().getStringExtra("productName");

        String category =
                getIntent().getStringExtra("category");

        double price =
                getIntent().getDoubleExtra("price", 0);

        // ---------------------------------------------
        // DISPLAY PRODUCT DETAILS
        // ---------------------------------------------

        if (productName != null) {
            edtProductService.setText(productName);
        }

        if (category != null) {
            edtCategory.setText(category);
        }

        if (price > 0) {
            edtPrice.setText(
                    "Rs. " + String.format("%.2f", price)
            );
        }

        // Product details should not be edited
        edtProductService.setFocusable(false);
        edtProductService.setClickable(false);

        edtCategory.setFocusable(false);
        edtCategory.setClickable(false);

        edtPrice.setFocusable(false);
        edtPrice.setClickable(false);

        // ---------------------------------------------
        // BRANCH LIST
        // ---------------------------------------------

        String[] branches = {
                "Select Branch",
                "TechFix - Colombo",
                "TechFix - Gampaha",
                "TechFix - Kandy",
                "TechFix - Negombo"
        };

        ArrayAdapter<String> branchAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        branches
                );

        branchAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerBranch.setAdapter(branchAdapter);

        // ---------------------------------------------
        // CAMERA
        // ---------------------------------------------

        cameraLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                Bundle extras =
                                        result.getData().getExtras();

                                if (extras != null) {

                                    android.graphics.Bitmap imageBitmap =
                                            (android.graphics.Bitmap)
                                                    extras.get("data");

                                    if (imageBitmap != null) {

                                        imgProductPhoto.setImageBitmap(
                                                imageBitmap
                                        );

                                        imgProductPhoto.setVisibility(
                                                ImageView.VISIBLE
                                        );

                                        btnAddPhoto.setText(
                                                "Retake Photo"
                                        );

                                        Toast.makeText(
                                                AppointmentActivity.this,
                                                "Photo captured",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                            }
                        }
                );

        // ---------------------------------------------
        // AUTO DETECT BRANCH
        // ---------------------------------------------

        radioAutoDetect.setOnClickListener(v -> {

            radioAutoDetect.setChecked(true);
            radioManual.setChecked(false);

            spinnerBranch.setEnabled(false);
            spinnerBranch.setSelection(0);
        });

        // ---------------------------------------------
        // MANUAL BRANCH
        // ---------------------------------------------

        radioManual.setOnClickListener(v -> {

            radioManual.setChecked(true);
            radioAutoDetect.setChecked(false);

            spinnerBranch.setEnabled(true);
        });

        // ---------------------------------------------
        // ADD PHOTO
        // ---------------------------------------------

        btnAddPhoto.setOnClickListener(v -> {

            Intent cameraIntent =
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            cameraLauncher.launch(cameraIntent);
        });

        // ---------------------------------------------
        // DATE
        // ---------------------------------------------

        edtDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            AppointmentActivity.this,
                            (view, selectedYear,
                             selectedMonth, selectedDay) -> {

                                String selectedDate =
                                        selectedDay + "/" +
                                                (selectedMonth + 1) + "/" +
                                                selectedYear;

                                edtDate.setText(selectedDate);
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });

        // ---------------------------------------------
        // TIME
        // ---------------------------------------------

        edtTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(
                            AppointmentActivity.this,
                            (view, selectedHour,
                             selectedMinute) -> {

                                String selectedTime;

                                if (selectedHour < 12) {

                                    selectedTime = String.format(
                                            "%02d:%02d AM",
                                            selectedHour,
                                            selectedMinute
                                    );

                                } else {

                                    int displayHour =
                                            selectedHour;

                                    if (displayHour > 12) {
                                        displayHour -= 12;
                                    }

                                    selectedTime = String.format(
                                            "%02d:%02d PM",
                                            displayHour,
                                            selectedMinute
                                    );
                                }

                                edtTime.setText(selectedTime);
                            },
                            hour,
                            minute,
                            false
                    );

            timePickerDialog.show();
        });

        // ---------------------------------------------
        // CANCEL
        // ---------------------------------------------

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        // ---------------------------------------------
        // CONFIRM
        // ---------------------------------------------

        btnConfirm.setOnClickListener(v -> {

            // Check Product / Service
            if (edtProductService.getText()
                    .toString()
                    .trim()
                    .isEmpty()) {

                edtProductService.setError(
                        "Product or service is required"
                );

                edtProductService.requestFocus();
                return;
            }

            // Check Date
            if (edtDate.getText()
                    .toString()
                    .trim()
                    .isEmpty()) {

                edtDate.setError(
                        "Please select a date"
                );

                edtDate.requestFocus();
                return;
            }

            // Check Time
            if (edtTime.getText()
                    .toString()
                    .trim()
                    .isEmpty()) {

                edtTime.setError(
                        "Please select a time"
                );

                edtTime.requestFocus();
                return;
            }

            // Check Manual Branch
            if (radioManual.isChecked()
                    && spinnerBranch.getSelectedItemPosition() == 0) {

                Toast.makeText(
                        AppointmentActivity.this,
                        "Please select a branch",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // -----------------------------------------
            // GET BRANCH
            // -----------------------------------------

            String branch;

            if (radioAutoDetect.isChecked()) {

                branch = "Auto Detect Nearest Branch";

            } else {

                branch =
                        spinnerBranch.getSelectedItem().toString();
            }

            // -----------------------------------------
            // GET APPOINTMENT DATA
            // -----------------------------------------

            String productService =
                    edtProductService.getText()
                            .toString()
                            .trim();

            String appointmentCategory =
                    edtCategory.getText()
                            .toString()
                            .trim();

            String priceText =
                    edtPrice.getText()
                            .toString()
                            .replace("Rs.", "")
                            .trim();

            double appointmentPrice = 0;

            try {

                appointmentPrice =
                        Double.parseDouble(priceText);

            } catch (NumberFormatException e) {

                appointmentPrice = 0;
            }

            String appointmentDate =
                    edtDate.getText()
                            .toString()
                            .trim();

            String appointmentTime =
                    edtTime.getText()
                            .toString()
                            .trim();

            // -----------------------------------------
            // SAVE TO DATABASE
            // -----------------------------------------

            SQLiteDatabase db =
                    databaseHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "productService",
                    productService
            );

            values.put(
                    "category",
                    appointmentCategory
            );

            values.put(
                    "price",
                    appointmentPrice
            );

            values.put(
                    "branch",
                    branch
            );

            values.put(
                    "appointmentDate",
                    appointmentDate
            );

            values.put(
                    "appointmentTime",
                    appointmentTime
            );

            long result =
                    db.insert(
                            "appointments",
                            null,
                            values
                    );

            // -----------------------------------------
            // RESULT
            // -----------------------------------------

            if (result != -1) {

                Toast.makeText(
                        AppointmentActivity.this,
                        "Appointment confirmed successfully",
                        Toast.LENGTH_LONG
                ).show();

                finish();

            } else {

                Toast.makeText(
                        AppointmentActivity.this,
                        "Failed to save appointment",
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        // ---------------------------------------------
        // INITIAL SETTINGS
        // ---------------------------------------------

        radioAutoDetect.setChecked(true);
        radioManual.setChecked(false);

        spinnerBranch.setEnabled(false);
        spinnerBranch.setSelection(0);

        imgProductPhoto.setVisibility(
                ImageView.GONE
        );
    }
}