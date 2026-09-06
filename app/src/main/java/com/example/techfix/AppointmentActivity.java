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

public class AppointmentActivity extends AppCompatActivity {

    RadioGroup radioGroupLocation;
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

    ArrayList<String> branchNames;

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final int CAMERA_PERMISSION_REQUEST = 200;

    private LocationManager locationManager;
    private LocationListener activeLocationListener;

    // Logged-in customer information
    String customerId;
    String customerName;
    String customerEmail;

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

                                    imgProductPhoto.setImageBitmap(photo);

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

        setContentView(R.layout.activity_appointment);

        radioGroupLocation =
                findViewById(R.id.radioGroupLocation);

        radioAutoDetect =
                findViewById(R.id.radioAutoDetect);

        radioManual =
                findViewById(R.id.radioManual);

        spinnerBranch =
                findViewById(R.id.spinnerBranch);

        edtProductService =
                findViewById(R.id.edtProductService);

        edtCategory =
                findViewById(R.id.edtCategory);

        edtPrice =
                findViewById(R.id.edtPrice);

        edtDate =
                findViewById(R.id.edtDate);

        edtTime =
                findViewById(R.id.edtTime);

        btnAddPhoto =
                findViewById(R.id.btnAddPhoto);

        btnCancel =
                findViewById(R.id.btnCancel);

        btnConfirm =
                findViewById(R.id.btnConfirm);

        imgProductPhoto =
                findViewById(R.id.imgProductPhoto);

        databaseHelper =
                new DatabaseHelper(this);

        branchNames =
                new ArrayList<>();

        // =================================================
        // GET CUSTOMER INFORMATION
        // =================================================

        customerId =
                getIntent().getStringExtra("customerId");

        customerName =
                getIntent().getStringExtra("customerName");

        customerEmail =
                getIntent().getStringExtra("customerEmail");

        // Product details passed in from previous screen
        String productName =
                getIntent().getStringExtra("productName");

        String category =
                getIntent().getStringExtra("category");

        double price =
                getIntent().getDoubleExtra("price", 0);

        if (productName != null) {

            edtProductService.setText(productName);
        }

        if (category != null) {

            edtCategory.setText(category);
        }

        if (price > 0) {

            edtPrice.setText(
                    String.format("%.2f", price)
            );
        }

        loadBranches();

        // =================================================
        // LOCATION
        // =================================================

        radioGroupLocation.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.radioAutoDetect) {

                        spinnerBranch.setEnabled(false);

                        detectCurrentLocation();

                    } else if (checkedId == R.id.radioManual) {

                        spinnerBranch.setEnabled(true);
                    }
                }
        );

        // =================================================
        // BUTTONS
        // =================================================

        btnAddPhoto.setOnClickListener(
                v -> openCamera()
        );

        edtDate.setOnClickListener(
                v -> showDatePicker()
        );

        edtTime.setOnClickListener(
                v -> showTimePicker()
        );

        btnCancel.setOnClickListener(
                v -> finish()
        );

        btnConfirm.setOnClickListener(
                v -> saveAppointment()
        );

        // =================================================
        // INITIAL STATE
        // =================================================

        radioAutoDetect.setChecked(true);

        spinnerBranch.setEnabled(false);

        detectCurrentLocation();
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

        spinnerBranch.setAdapter(adapter);
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
                        getSystemService(LOCATION_SERVICE);

        if (locationManager == null) {

            Toast.makeText(
                    this,
                    "Location service is unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Clean up previous listener
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

            if (locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
            )) {

                lastLocation =
                        locationManager.getLastKnownLocation(
                                LocationManager.GPS_PROVIDER
                        );
            }

            if (lastLocation == null
                    && locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            )) {

                lastLocation =
                        locationManager.getLastKnownLocation(
                                LocationManager.NETWORK_PROVIDER
                        );
            }

            if (lastLocation != null) {

                findNearestBranch(
                        lastLocation.getLatitude(),
                        lastLocation.getLongitude()
                );

                return;
            }

            activeLocationListener =
                    new LocationListener() {

                        @Override
                        public void onLocationChanged(
                                Location location
                        ) {

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
                                String provider
                        ) {
                        }

                        @Override
                        public void onProviderDisabled(
                                String provider
                        ) {
                        }

                        @SuppressWarnings("deprecation")
                        @Override
                        public void onStatusChanged(
                                String provider,
                                int status,
                                Bundle extras
                        ) {
                        }
                    };

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
            double userLongitude
    ) {

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

                if (distance[0] < shortestDistance) {

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

                    spinnerBranch.setSelection(i);

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
    // DATE / TIME PICKERS
    // =====================================================

    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(Calendar.YEAR);

        int month =
                calendar.get(Calendar.MONTH);

        int day =
                calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(
                this,
                (view,
                 selectedYear,
                 selectedMonth,
                 selectedDay) -> {

                    String date =
                            String.format(
                                    "%02d/%02d/%04d",
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear
                            );

                    edtDate.setText(date);

                },
                year,
                month,
                day
        ).show();
    }

    private void showTimePicker() {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(Calendar.HOUR_OF_DAY);

        int minute =
                calendar.get(Calendar.MINUTE);

        new TimePickerDialog(
                this,
                (view,
                 selectedHour,
                 selectedMinute) -> {

                    String time =
                            String.format(
                                    "%02d:%02d",
                                    selectedHour,
                                    selectedMinute
                            );

                    edtTime.setText(time);

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

        String productService =
                edtProductService
                        .getText()
                        .toString()
                        .trim();

        String category =
                edtCategory
                        .getText()
                        .toString()
                        .trim();

        String priceText =
                edtPrice
                        .getText()
                        .toString()
                        .replace("Rs.", "")
                        .trim();

        String date =
                edtDate
                        .getText()
                        .toString()
                        .trim();

        String time =
                edtTime
                        .getText()
                        .toString()
                        .trim();

        // =================================================
        // VALIDATION
        // =================================================

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer information is missing. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (productService.isEmpty()) {

            edtProductService.setError(
                    "Product / Service is required"
            );

            return;
        }

        if (date.isEmpty()) {

            edtDate.setError(
                    "Please select a date"
            );

            return;
        }

        if (time.isEmpty()) {

            edtTime.setError(
                    "Please select a time"
            );

            return;
        }

        if (spinnerBranch.getSelectedItem() == null) {

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
                        .toString();

        double price;

        try {

            price =
                    priceText.isEmpty()
                            ? 0
                            : Double.parseDouble(
                            priceText
                    );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Invalid price",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // =================================================
        // INSERT APPOINTMENT
        // =================================================

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        // NEW: Save logged-in customer ID
        values.put(
                "customerId",
                Integer.parseInt(customerId)
        );

        values.put(
                "productService",
                productService
        );

        values.put(
                "category",
                category
        );

        values.put(
                "price",
                price
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
            int[] grantResults
    ) {

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