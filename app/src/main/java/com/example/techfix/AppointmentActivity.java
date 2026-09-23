package com.example.techfix;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private Spinner spinnerTime;
    private TextView txtTimeAvailability;

    private Button btnAddPhoto;
    private Button btnCancel;
    private Button btnConfirm;

    private ImageView imgProductPhoto;

    private DatabaseHelper databaseHelper;

    private ArrayList<String> branchNames;
    private ArrayList<TimeSlot> timeSlots;
    private TimeSlotAdapter timeSlotAdapter;

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
    private boolean otherService;

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

        spinnerTime =
                findViewById(
                        R.id.spinnerTime
                );

        txtTimeAvailability =
                findViewById(
                        R.id.txtTimeAvailability
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

        timeSlots =
                new ArrayList<>();

        timeSlotAdapter =
                new TimeSlotAdapter(
                        this,
                        timeSlots
                );

        spinnerTime.setAdapter(
                timeSlotAdapter
        );

        spinnerTime.setEnabled(false);

        txtTimeAvailability.setText(
                "Select a date to view available slots."
        );

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

        otherService =
                getIntent().getBooleanExtra(
                        "otherService",
                        false
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

        if (!otherService && selectedPrice <= 0) {

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

        if (otherService) {

            edtPrice.setText(
                    "To be confirmed"
            );

        } else {

            edtPrice.setText(
                    "Rs. " +
                            String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    selectedPrice
                            )
            );
        }

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

                        refreshTimeSlots();
                    }
                }
        );

        // =====================================================
        // BRANCH CHANGE
        // =====================================================

        spinnerBranch.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        refreshTimeSlots();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
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
        // TIME SLOTS
        // Generated automatically after a date and branch
        // are selected.
        // =====================================================

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

            refreshTimeSlots();

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

        DatePickerDialog dialog =
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

                            refreshTimeSlots();
                        },
                        year,
                        month,
                        day
                );

        // Do not allow past dates.
        dialog.getDatePicker().setMinDate(
                System.currentTimeMillis()
        );

        dialog.show();
    }

    // =====================================================
    // REFRESH AVAILABLE TIME SLOTS
    // =====================================================

    private void refreshTimeSlots() {

        timeSlots.clear();

        String date =
                edtDate.getText()
                        .toString()
                        .trim();

        if (date.isEmpty()) {

            spinnerTime.setEnabled(false);

            timeSlotAdapter.notifyDataSetChanged();

            txtTimeAvailability.setText(
                    "Select a date to view available slots."
            );

            return;
        }

        if (branchNames.isEmpty()
                || spinnerBranch.getSelectedItem() == null) {

            spinnerTime.setEnabled(false);

            timeSlotAdapter.notifyDataSetChanged();

            txtTimeAvailability.setText(
                    "No branch is available."
            );

            return;
        }

        String branch =
                spinnerBranch
                        .getSelectedItem()
                        .toString()
                        .trim();

        if (branch.isEmpty()) {

            spinnerTime.setEnabled(false);

            timeSlotAdapter.notifyDataSetChanged();

            txtTimeAvailability.setText(
                    "Please select a valid branch."
            );

            return;
        }

        int technicianCapacity =
                getTechnicianCapacity(
                        branch
                );

        /*
         * 9:00 AM to 5:00 PM
         * 30-minute slots
         * Total = 16 slots per day.
         */
        Calendar start =
                Calendar.getInstance();

        start.set(
                Calendar.HOUR_OF_DAY,
                9
        );

        start.set(
                Calendar.MINUTE,
                0
        );

        start.set(
                Calendar.SECOND,
                0
        );

        start.set(
                Calendar.MILLISECOND,
                0
        );

        Calendar close =
                (Calendar) start.clone();

        close.set(
                Calendar.HOUR_OF_DAY,
                17
        );

        int availableCount = 0;

        while (start.before(close)) {

            Calendar end =
                    (Calendar) start.clone();

            end.add(
                    Calendar.MINUTE,
                    30
            );

            String storedStartTime =
                    String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            start.get(
                                    Calendar.HOUR_OF_DAY
                            ),
                            start.get(
                                    Calendar.MINUTE
                            )
                    );

            String storedEndTime =
                    String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            end.get(
                                    Calendar.HOUR_OF_DAY
                            ),
                            end.get(
                                    Calendar.MINUTE
                            )
                    );

            String displayTime =
                    formatDisplayTime(
                            start.get(
                                    Calendar.HOUR_OF_DAY
                            ),
                            start.get(
                                    Calendar.MINUTE
                            )
                    )
                            + " - " +
                            formatDisplayTime(
                                    end.get(
                                            Calendar.HOUR_OF_DAY
                                    ),
                                    end.get(
                                            Calendar.MINUTE
                                    )
                            );

            String storedTime =
                    storedStartTime +
                            " - " +
                            storedEndTime;

            int bookedCount =
                    getBookedCount(
                            branch,
                            date,
                            storedTime
                    );

            boolean pastSlot =
                    isPastSlot(
                            date,
                            start
                    );

            boolean available =
                    technicianCapacity > 0
                            && bookedCount < technicianCapacity
                            && !pastSlot;

            if (available) {
                availableCount++;
            }

            timeSlots.add(
                    new TimeSlot(
                            storedTime,
                            displayTime,
                            technicianCapacity,
                            bookedCount,
                            available
                    )
            );

            start.add(
                    Calendar.MINUTE,
                    30
            );
        }

        spinnerTime.setEnabled(
                technicianCapacity > 0
        );

        timeSlotAdapter.notifyDataSetChanged();

        if (technicianCapacity <= 0) {

            txtTimeAvailability.setText(
                    "No technicians are currently assigned to " +
                            branch +
                            "."
            );

        } else {

            txtTimeAvailability.setText(
                    availableCount +
                            " of " +
                            timeSlots.size() +
                            " slots are available at " +
                            branch +
                            "."
            );
        }

        // Make sure the first selectable item is shown.
        selectFirstAvailableSlot();
    }

    // =====================================================
    // TECHNICIAN CAPACITY
    // =====================================================

    private int getTechnicianCapacity(
            String branch) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT COUNT(*) " +
                                    "FROM technicians " +
                                    "WHERE LOWER(TRIM(COALESCE(branch, ''))) = " +
                                    "LOWER(TRIM(?))",
                            new String[]{
                                    branch
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getInt(0);
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    // =====================================================
    // BOOKED APPOINTMENT COUNT
    // =====================================================

    private int getBookedCount(
            String branch,
            String date,
            String time) {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT COUNT(*) " +
                                    "FROM appointments " +
                                    "WHERE LOWER(TRIM(COALESCE(branch, ''))) = " +
                                    "LOWER(TRIM(?)) " +
                                    "AND appointmentDate = ? " +
                                    "AND appointmentTime = ?",
                            new String[]{
                                    branch,
                                    date,
                                    time
                            }
                    );

            if (cursor.moveToFirst()) {

                return cursor.getInt(0);
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    // =====================================================
    // CHECK PAST SLOT
    // =====================================================

    private boolean isPastSlot(
            String date,
            Calendar slotStart) {

        String today =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        if (!date.equals(today)) {

            return false;
        }

        Calendar now =
                Calendar.getInstance();

        return !slotStart.after(now);
    }

    // =====================================================
    // DISPLAY TIME
    // =====================================================

    private String formatDisplayTime(
            int hour,
            int minute) {

        String amPm =
                hour >= 12
                        ? "PM"
                        : "AM";

        int displayHour =
                hour % 12;

        if (displayHour == 0) {
            displayHour = 12;
        }

        return String.format(
                Locale.getDefault(),
                "%d:%02d %s",
                displayHour,
                minute,
                amPm
        );
    }

    // =====================================================
    // SELECT FIRST AVAILABLE SLOT
    // =====================================================

    private void selectFirstAvailableSlot() {

        for (int i = 0; i < timeSlots.size(); i++) {

            TimeSlot slot =
                    timeSlots.get(i);

            if (slot.available) {

                spinnerTime.setSelection(
                        i
                );

                return;
            }
        }

        spinnerTime.setSelection(0);
    }

    // =====================================================
    // SAVE APPOINTMENT
    // =====================================================

    private void saveAppointment() {

        String date =
                edtDate
                        .getText()
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

        // Other Repair does not have an estimated price yet.
        // Its price is stored as 0 until the administrator
        // sets the final repair amount after the repair is finished.
        if (!otherService && selectedPrice <= 0) {

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
        // TIME SLOT VALIDATION
        // =====================================================

        if (spinnerTime.getSelectedItem() == null ||
                timeSlots.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select an available appointment slot.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        TimeSlot selectedSlot =
                (TimeSlot)
                        spinnerTime
                                .getSelectedItem();

        if (selectedSlot == null ||
                !selectedSlot.available) {

            Toast.makeText(
                    this,
                    "Please select an available appointment slot.",
                    Toast.LENGTH_LONG
            ).show();

            refreshTimeSlots();

            return;
        }

        // =====================================================
        // LAST AVAILABILITY CHECK
        // Prevent two customers from taking the same final slot.
        // =====================================================

        int capacity =
                getTechnicianCapacity(
                        branch
                );

        int booked =
                getBookedCount(
                        branch,
                        date,
                        selectedSlot.storedTime
                );

        if (capacity <= 0) {

            Toast.makeText(
                    this,
                    "No technicians are currently assigned to this branch.",
                    Toast.LENGTH_LONG
            ).show();

            refreshTimeSlots();

            return;
        }

        if (booked >= capacity) {

            Toast.makeText(
                    this,
                    "This slot has just become fully booked. Please choose another slot.",
                    Toast.LENGTH_LONG
            ).show();

            refreshTimeSlots();

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

        // Use original selected product values
        values.put(
                "productService",
                selectedProductName
        );

        values.put(
                "category",
                selectedCategory
        );

        // Other Repair = 0 until final amount is confirmed
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
                selectedSlot.storedTime
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
                    "Appointment Confirmed\n" +
                            selectedSlot.displayTime,
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
    // TIME SLOT MODEL
    // =====================================================

    private static class TimeSlot {

        String storedTime;
        String displayTime;

        int technicianCapacity;
        int bookedCount;

        boolean available;

        TimeSlot(
                String storedTime,
                String displayTime,
                int technicianCapacity,
                int bookedCount,
                boolean available) {

            this.storedTime = storedTime;
            this.displayTime = displayTime;
            this.technicianCapacity =
                    technicianCapacity;
            this.bookedCount =
                    bookedCount;
            this.available =
                    available;
        }

        @Override
        public String toString() {

            if (technicianCapacity <= 0) {

                return displayTime +
                        " • No technicians";
            }

            if (!available) {

                if (bookedCount >= technicianCapacity) {

                    return displayTime +
                            " • FULL";
                }

                return displayTime +
                        " • Not available";
            }

            return displayTime +
                    " • " +
                    (technicianCapacity - bookedCount) +
                    " available";
        }
    }

    // =====================================================
    // TIME SLOT ADAPTER
    // =====================================================

    private static class TimeSlotAdapter
            extends ArrayAdapter<TimeSlot> {

        public TimeSlotAdapter(
                android.content.Context context,
                List<TimeSlot> items) {

            super(
                    context,
                    android.R.layout.simple_spinner_item,
                    items
            );

            setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );
        }

        @Override
        public boolean isEnabled(
                int position) {

            TimeSlot item =
                    getItem(position);

            return item != null &&
                    item.available;
        }

        @Override
        public boolean areAllItemsEnabled() {

            return false;
        }

        @Override
        public View getView(
                int position,
                View convertView,
                android.view.ViewGroup parent) {

            TextView textView =
                    (TextView)
                            super.getView(
                                    position,
                                    convertView,
                                    parent
                            );

            TimeSlot item =
                    getItem(position);

            if (item != null) {

                textView.setText(
                        item.toString()
                );

                textView.setPadding(
                        15,
                        10,
                        15,
                        10
                );
            }

            return textView;
        }

        @Override
        public View getDropDownView(
                int position,
                View convertView,
                android.view.ViewGroup parent) {

            TextView textView =
                    (TextView)
                            super.getDropDownView(
                                    position,
                                    convertView,
                                    parent
                            );

            TimeSlot item =
                    getItem(position);

            if (item != null) {

                textView.setText(
                        item.toString()
                );

                textView.setPadding(
                        15,
                        12,
                        15,
                        12
                );

                textView.setTextColor(
                        item.available
                                ? android.graphics.Color.BLACK
                                : android.graphics.Color.GRAY
                );
            }

            return textView;
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
