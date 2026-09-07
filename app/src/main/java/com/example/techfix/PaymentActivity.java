package com.example.techfix;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView txtAppointment;
    private TextView txtService;
    private TextView txtAmount;

    private RadioGroup paymentMethodGroup;
    private RadioButton radioCash;
    private RadioButton radioCard;
    private RadioButton radioBankTransfer;

    private Button btnConfirmPayment;
    private Button btnCancel;

    private DatabaseHelper databaseHelper;

    private int appointmentId;
    private String customerId;
    private double amount;
    private String productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);

        // =====================================================
        // CONNECT UI
        // =====================================================

        txtAppointment =
                findViewById(R.id.txtAppointment);

        txtService =
                findViewById(R.id.txtService);

        txtAmount =
                findViewById(R.id.txtAmount);

        paymentMethodGroup =
                findViewById(R.id.paymentMethodGroup);

        radioCash =
                findViewById(R.id.radioCash);

        radioCard =
                findViewById(R.id.radioCard);

        radioBankTransfer =
                findViewById(R.id.radioBankTransfer);

        btnConfirmPayment =
                findViewById(R.id.btnConfirmPayment);

        btnCancel =
                findViewById(R.id.btnCancel);

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET DATA FROM MY APPOINTMENTS
        // =====================================================

        appointmentId =
                getIntent().getIntExtra(
                        "appointmentId",
                        -1
                );

        customerId =
                getIntent().getStringExtra(
                        "customerId"
                );

        amount =
                getIntent().getDoubleExtra(
                        "amount",
                        0
                );

        productService =
                getIntent().getStringExtra(
                        "productService"
                );

        // =====================================================
        // VALIDATE INFORMATION
        // =====================================================

        if (appointmentId == -1 ||
                customerId == null ||
                customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Payment information is missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // =====================================================
        // DISPLAY PAYMENT INFORMATION
        // =====================================================

        txtAppointment.setText(
                "Appointment #" + appointmentId
        );

        if (productService == null ||
                productService.trim().isEmpty()) {

            productService =
                    "Repair Service";
        }

        txtService.setText(
                "Service: " + productService
        );

        txtAmount.setText(
                "Amount: Rs. " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                amount
                        )
        );

        // Default payment method
        radioCash.setChecked(true);

        // =====================================================
        // CONFIRM PAYMENT
        // =====================================================

        btnConfirmPayment.setOnClickListener(v -> {

            processPayment();
        });

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(v -> {

            finish();
        });
    }

    // =====================================================
    // PROCESS PAYMENT
    // =====================================================

    private void processPayment() {

        int selectedMethodId =
                paymentMethodGroup.getCheckedRadioButtonId();

        if (selectedMethodId == -1) {

            Toast.makeText(
                    this,
                    "Please select a payment method",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String paymentMethod;

        // =====================================================
        // CASH
        // =====================================================

        if (selectedMethodId == R.id.radioCash) {

            paymentMethod = "Cash";

            createPendingPayment(
                    paymentMethod
            );

            return;
        }

        // =====================================================
        // BANK TRANSFER
        // =====================================================

        if (selectedMethodId == R.id.radioBankTransfer) {

            paymentMethod = "Bank Transfer";

            createPendingPayment(
                    paymentMethod
            );

            return;
        }

        // =====================================================
        // CARD
        // =====================================================

        if (selectedMethodId == R.id.radioCard) {

            openCardPaymentGateway();

            return;
        }

        Toast.makeText(
                this,
                "Invalid payment method",
                Toast.LENGTH_SHORT
        ).show();
    }

    // =====================================================
    // CASH / BANK TRANSFER
    // =====================================================

    private void createPendingPayment(
            String paymentMethod) {

        if (paymentAlreadyExists()) {

            Toast.makeText(
                    this,
                    "Payment has already been recorded",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        String paymentDate =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

        ContentValues values =
                new ContentValues();

        values.put(
                "appointmentId",
                appointmentId
        );

        values.put(
                "customerId",
                Integer.parseInt(customerId)
        );

        values.put(
                "amount",
                amount
        );

        values.put(
                "paymentMethod",
                paymentMethod
        );

        values.put(
                "paymentStatus",
                "PENDING"
        );

        values.put(
                "paymentDate",
                paymentDate
        );

        long result =
                db.insert(
                        "payments",
                        null,
                        values
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    paymentMethod +
                            " payment recorded as PENDING",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Payment failed. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =====================================================
    // OPEN DUMMY CARD PAYMENT GATEWAY
    // =====================================================

    private void openCardPaymentGateway() {

        if (paymentAlreadyExists()) {

            Toast.makeText(
                    this,
                    "Payment has already been recorded",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent =
                new Intent(
                        PaymentActivity.this,
                        CardPaymentActivity.class
                );

        intent.putExtra(
                "appointmentId",
                appointmentId
        );

        intent.putExtra(
                "customerId",
                customerId
        );

        intent.putExtra(
                "amount",
                amount
        );

        intent.putExtra(
                "productService",
                productService
        );

        startActivity(intent);
    }

    // =====================================================
    // CHECK EXISTING PAYMENT
    // =====================================================

    private boolean paymentAlreadyExists() {

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor = db.rawQuery(
                    "SELECT paymentId " +
                            "FROM payments " +
                            "WHERE appointmentId = ? " +
                            "AND customerId = ? " +
                            "LIMIT 1",

                    new String[]{
                            String.valueOf(appointmentId),
                            customerId
                    }
            );

            return cursor.moveToFirst();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }
}