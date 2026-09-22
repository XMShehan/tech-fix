package com.example.techfix;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {

    private RadioGroup radioGroupLocation;
    private RadioButton radioAutoDetect;
    private RadioButton radioManual;

    private Spinner spinnerBranch;

    private EditText edtProductService;
    private EditText edtCategory;
    private EditText edtPrice;
    private EditText edtDate;
    private EditText edtTime;

    private Button btnAddPhoto;
    private Button btnCancel;
    private Button btnConfirm;

    private ImageView imgProductPhoto;

    private DatabaseHelper databaseHelper;

    private ArrayList<String> branchNames;

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final int CAMERA_PERMISSION_REQUEST = 200;

    private LocationManager locationManager;
    private LocationListener activeLocationListener;

    // =====================================================
    // LOGGED-IN CUSTOMER INFORMATION
    // =====================================================

    private String customerId;
    private String customerName;
    private String customerEmail;

    // =====================================================
    // SELECTED PRODUCT INFORMATION
    // These values come from ProductListActivity.
    // They are the source of truth for the appointment.
    // =====================================================

    private String selectedProductName;
    private String selectedCategory;
    private double selectedPrice;

    // =====================================================
    // CAMERA
    // =====================================================

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            Bundle extras =
                                    result.getData().getExtras();

                            if (extras != null) {

                                Bitmap photo =
                                        (Bitmap) extras.get("data");

                                if (photo != null) {

                                    imgProductPhoto.setImageBitmap(
                                            photo
                                    );

                                    Toast.makeText(
                                            this,
                                            "Photo captured successfully",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                        }
                    }
            );

    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_appointment
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        radioGroupLocation =
                findViewById(
                        R.id.radioGroupLocation
                );

        radioAutoDetect =
                findViewById(
                        R.id.radioAutoDetect
                );

        radioManual =
                findViewById(
                        R.id.radioManual
                );

        spinnerBranch =
                findViewById(
                        R.id.spinnerBranch
                );

        edtProductService =
                findViewById(
                        R.id.edtProductService
                );

        edtCategory =
                findViewById(
                        R.id.edtCategory
                );

        edtPrice =
                findViewById(
                        R.id.edtPrice
                );

        edtDate =
                findViewById(
                        R.id.edtDate
                );

        edtTime =
                findViewById(
                        R.id.edtTime
                );

        btnAddPhoto =
                findViewById(
                        R.id.btnAddPhoto
                );

        btnCancel =
                findViewById(
                        R.id.btnCancel
                );

        btnConfirm =
                findViewById(
                        R.id.btnConfirm
                );

        imgProductPhoto =
                findViewById(
                        R.id.imgProductPhoto
                );

        databaseHelper =
                new DatabaseHelper(this);

        branchNames =
                new ArrayList<>();

        // =====================================================
        // GET CUSTOMER INFORMATION
        // =====================================================

        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        customerName =
                getIntent().getStringExtra(
                        "customerName"
                );

        customerEmail =
                getIntent().getStringExtra(
                        "customerEmail"
                );

        // =====================================================
        // GET SELECTED PRODUCT INFORMATION
        // =====================================================

        selectedProductName =
                getIntent().getStringExtra(
                        "productName"
                );

        selectedCategory =
                getIntent().getStringExtra(
                        "category"
                );

        selectedPrice =
                getIntent().getDoubleExtra(
                        "price",
                        0
                );

        // =====================================================
        // VALIDATE PRODUCT INFORMATION
        // =====================================================

        if (selectedProductName == null ||
                selectedProductName.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Product information is missing. Please select a product again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        if (selectedCategory == null ||
                selectedCategory.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Product category is missing. Please select a product again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        if (selectedPrice <= 0) {

            Toast.makeText(
                    this,
                    "Invalid product price. Please select the product again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // =====================================================
        // DISPLAY SELECTED PRODUCT
        // =====================================================

        edtProductService.setText(
                selectedProductName
        );

        edtCategory.setText(
                selectedCategory
        );

        edtPrice.setText(
                "Rs. " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                selectedPrice
                        )
        );

        // =====================================================
        // MAKE PRODUCT INFORMATION READ-ONLY
        // =====================================================

        /*
         * The customer selected this information on the
         * Product List screen.
         *
         * It must not be editable here.
         */

        makeReadOnly(
                edtProductService
        );

        makeReadOnly(
                edtCategory
        );

        makeReadOnly(
                edtPrice
        );

        // =====================================================
        // LOAD BRANCHES
        // =====================================================

        loadBranches();

        // =====================================================
        // LOCATION
        // =====================================================

        radioGroupLocation.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId ==
                            R.id.radioAutoDetect) {

                        spinnerBranch.setEnabled(false);

                        detectCurrentLocation();

                    } else if (checkedId ==
                            R.id.radioManual) {

                        spinnerBranch.setEnabled(true);
                    }
                }
        );

        // =====================================================
        // ADD PHOTO
        // =====================================================

        btnAddPhoto.setOnClickListener(
                v -> openCamera()
        );

        // =====================================================
        // DATE
        // =====================================================

        edtDate.setOnClickListener(
                v -> showDatePicker()
        );

        // =====================================================
        // TIME
        // =====================================================

        edtTime.setOnClickListener(
                v -> showTimePicker()
        );

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(
                v -> finish()
        );

        // =====================================================
        // CONFIRM
        // =====================================================

        btnConfirm.setOnClickListener(
                v -> saveAppointment()
        );

        // =====================================================
        // INITIAL LOCATION STATE
        // =====================================================

        radioAutoDetect.setChecked(true);

        spinnerBranch.setEnabled(false);

        detectCurrentLocation();
    }

    // =====================================================
    // MAKE EDITTEXT READ-ONLY
    // =====================================================

    private void makeReadOnly(
            EditText editText) {

        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
        editText.setCursorVisible(false);
        editText.setLongClickable(false);
        editText.setTextIsSelectable(false);
    }

    // =====================================================
    // LOAD BRANCHES
    // =====================================================

    private void loadBranches() {

        branchNames.clear();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT branchName FROM branches",
                            null
                    );

            while (cursor.moveToNext()) {

                branchNames.add(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchName"
                                )
                        )
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load branches",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (branchNames.isEmpty()) {

            Toast.makeText(
                    this,
                    "No branches available yet",
                    Toast.LENGTH_SHORT
            ).show();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        branchNames
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerBranch.setAdapter(
                adapter
        );
    }

    // =====================================================
    // OPEN CAMERA
    // =====================================================

    private void openCamera() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_PERMISSION_REQUEST
            );

            return;
        }

        Intent cameraIntent =
                new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE
                );

        if (cameraIntent.resolveActivity(
                getPackageManager()
        ) != null) {

            cameraLauncher.launch(
                    cameraIntent
            );

        } else {

            Toast.makeText(
                    this,
                    "Camera is not available",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
    // DETECT CURRENT LOCATION
    // =====================================================

    private void detectCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );

            return;
        }

        locationManager =
                (LocationManager)
                        getSystemService(
                                LOCATION_SERVICE
                        );

        if (locationManager == null) {

            Toast.makeText(
                    this,
                    "Location service is unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // REMOVE OLD LISTENER
        // =====================================================

        if (activeLocationListener != null) {

            try {

                locationManager.removeUpdates(
                        activeLocationListener
                );

            } catch (SecurityException ignored) {
            }
        }

        try {

            Location lastLocation = null;

            // =================================================
            // GPS LOCATION
            // =================================================

            if (locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
            )) {

                lastLocation =
                        locationManager.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER
                        );
            }

            // =================================================
            // NETWORK LOCATION
            // =================================================

            if (lastLocation == null
                    && locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            )) {

                lastLocation =
                        locationManager.getLastKnownLocation(
                                LocationManager.NETWORK_PROVIDER
                        );
            }

            // =================================================
            // USE LAST LOCATION
            // =================================================

            if (lastLocation != null) {

                findNearestBranch(
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude()
                );

                return;
            }

            // =================================================
            // LOCATION LISTENER
            // =================================================

            activeLocationListener =
                    new LocationListener() {

                        @Override
                        public void onLocationChanged(
                                Location location) {

                            findNearestBranch(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );

                            locationManager.removeUpdates(
                                    this
                            );
                        }

                        @Override
                        public void onProviderEnabled(
                                String provider) {
                        }

                        @Override
                        public void onProviderDisabled(
                                String provider) {
                        }

                        @SuppressWarnings("deprecation")
                        @Override
                        public void onStatusChanged(
                                String provider,
                                int status,
                                Bundle extras) {
                        }
                    };

            // =================================================
            // GPS UPDATES
            // =================================================

            if (locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
            )) {

                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        1,
                        activeLocationListener
                );
            }

            // =================================================
            // NETWORK UPDATES
            // =================================================

            if (locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            )) {

                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000,
                        1,
                        activeLocationListener
                );
            }

        } catch (SecurityException e) {

            Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
    // FIND NEAREST BRANCH
    // =====================================================

    private void findNearestBranch(
            double userLatitude,
            double userLongitude) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        String nearestBranch = null;

        float shortestDistance =
                Float.MAX_VALUE;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT branchName, latitude, longitude " +
                                    "FROM branches",
                            null
                    );

            while (cursor.moveToNext()) {

                String branchName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "branchName"
                                )
                        );

                double branchLatitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "latitude"
                                )
                        );

                double branchLongitude =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "longitude"
                                )
                        );

                float[] distance =
                        new float[1];

                Location.distanceBetween(
                        userLatitude,
                        userLongitude,
                        branchLatitude,
                        branchLongitude,
                        distance
                );

                if (distance[0] <
                        shortestDistance) {

                    shortestDistance =
                            distance[0];

                    nearestBranch =
                            branchName;
                }
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to calculate nearest branch",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (nearestBranch != null) {

            for (
                    int i = 0;
                    i < branchNames.size();
                    i++
            ) {

                if (branchNames.get(i).equals(
                        nearestBranch
                )) {

                    spinnerBranch.setSelection(
                            i
                    );

                    break;
                }
            }

            float distanceKm =
                    shortestDistance / 1000;

            Toast.makeText(
                    this,
                    "Nearest Branch: " +
                            nearestBranch +
                            "\nDistance: " +
                            String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    distanceKm
                            ) +
                            " km",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "No branch data found",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
    // DATE PICKER
    // =====================================================

    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(
                        Calendar.YEAR
                );

        int month =
                calendar.get(
                        Calendar.MONTH
                );

        int day =
                calendar.get(
                        Calendar.DAY_OF_MONTH
                );

        new DatePickerDialog(
                this,
                (view,
                 selectedYear,
                 selectedMonth,
                 selectedDay) -> {

                    String date =
                            String.format(
                                    Locale.getDefault(),
                                    "%02d/%02d/%04d",
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear
                            );

                    edtDate.setText(
                            date
                    );

                },
                year,
                month,
                day
        ).show();
    }

    // =====================================================
    // TIME PICKER
    // =====================================================

    private void showTimePicker() {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(
                        Calendar.HOUR_OF_DAY
                );

        int minute =
                calendar.get(
                        Calendar.MINUTE
                );

        new TimePickerDialog(
                this,
                (view,
                 selectedHour,
                 selectedMinute) -> {

                    String time =
                            String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    selectedHour,
                                    selectedMinute
                            );

                    edtTime.setText(
                            time
                    );

                },
                hour,
                minute,
                false
        ).show();
    }

    // =====================================================
    // SAVE APPOINTMENT
    // =====================================================

    private void saveAppointment() {

        String date =
                edtDate.getText()
                        .toString()
                        .trim();

        String time =
                edtTime.getText()
                        .toString()
                        .trim();

        // =====================================================
        // CUSTOMER VALIDATION
        // =====================================================

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer information is missing. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // =====================================================
        // PRODUCT VALIDATION
        // =====================================================

        if (selectedProductName == null ||
                selectedProductName.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Product information is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (selectedCategory == null ||
                selectedCategory.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Product category is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (selectedPrice <= 0) {

            Toast.makeText(
                    this,
                    "Invalid product price.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // =====================================================
        // DATE VALIDATION
        // =====================================================

        if (date.isEmpty()) {

            edtDate.setError(
                    "Please select a date"
            );

            edtDate.requestFocus();

            return;
        }

        // =====================================================
        // TIME VALIDATION
        // =====================================================

        if (time.isEmpty()) {

            edtTime.setError(
                    "Please select a time"
            );

            edtTime.requestFocus();

            return;
        }

        // =====================================================
        // BRANCH VALIDATION
        // =====================================================

        if (spinnerBranch.getSelectedItem() == null ||
                branchNames.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select a branch",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String branch =
                spinnerBranch
                        .getSelectedItem()
                        .toString()
                        .trim();

        if (branch.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select a valid branch",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =====================================================
        // INSERT APPOINTMENT
        // =====================================================

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        // Logged-in customer
        values.put(
                "customerId",
                Integer.parseInt(customerId)
        );

        // IMPORTANT:
        // Use the original selected product values.
        // Do NOT read these from editable fields.

        values.put(
                "productService",
                selectedProductName
        );

        values.put(
                "category",
                selectedCategory
        );

        values.put(
                "price",
                selectedPrice
        );

        values.put(
                "branch",
                branch
        );

        values.put(
                "appointmentDate",
                date
        );

        values.put(
                "appointmentTime",
                time
        );

        long result =
                db.insert(
                        "appointments",
                        null,
                        values
                );

        // =====================================================
        // RESULT
        // =====================================================

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Appointment Confirmed",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to create appointment",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =====================================================
    // PERMISSION RESULT
    // =====================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                LOCATION_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                detectCurrentLocation();

            } else {

                Toast.makeText(
                        this,
                        "Location permission is required",
                        Toast.LENGTH_LONG
                ).show();
            }
        }

        if (requestCode ==
                CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                openCamera();

            } else {

                Toast.makeText(
                        this,
                        "Camera permission is required",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}