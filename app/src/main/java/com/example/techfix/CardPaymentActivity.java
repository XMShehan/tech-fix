package com.example.techfix;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CardPaymentActivity extends AppCompatActivity {

    private TextView txtAppointment;
    private TextView txtService;
    private TextView txtAmount;

    private EditText edtCardHolder;
    private EditText edtCardNumber;
    private EditText edtExpiry;
    private EditText edtCvv;

    private Button btnPay;
    private Button btnCancel;

    private DatabaseHelper databaseHelper;

    private int appointmentId;
    private String customerId;
    private double amount;
    private String productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_card_payment
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        txtAppointment =
                findViewById(R.id.txtAppointment);

        txtService =
                findViewById(R.id.txtService);

        txtAmount =
                findViewById(R.id.txtAmount);

        edtCardHolder =
                findViewById(R.id.edtCardHolder);

        edtCardNumber =
                findViewById(R.id.edtCardNumber);

        edtExpiry =
                findViewById(R.id.edtExpiry);

        edtCvv =
                findViewById(R.id.edtCvv);

        btnPay =
                findViewById(R.id.btnPay);

        btnCancel =
                findViewById(R.id.btnCancel);

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // GET PAYMENT INFORMATION
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
        // DISPLAY INFORMATION
        // =====================================================

        txtAppointment.setText(
                "Appointment #" +
                        appointmentId
        );

        if (productService == null ||
                productService.trim().isEmpty()) {

            productService =
                    "Repair Service";
        }

        txtService.setText(
                "Service: " +
                        productService
        );

        txtAmount.setText(
                "Amount: Rs. " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                amount
                        )
        );

        // =====================================================
        // CARD NUMBER
        // =====================================================

        edtCardNumber.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        // =====================================================
        // CVV
        // =====================================================

        edtCvv.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
        );

        // =====================================================
        // PAY
        // =====================================================

        btnPay.setOnClickListener(v -> {

            processCardPayment();
        });

        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener(v -> {

            finish();
        });
    }

    // =====================================================
    // PROCESS CARD PAYMENT
    // =====================================================

    private void processCardPayment() {

        String cardHolder =
                edtCardHolder.getText()
                        .toString()
                        .trim();

        String cardNumber =
                edtCardNumber.getText()
                        .toString()
                        .trim()
                        .replace(
                                " ",
                                ""
                        );

        String expiry =
                edtExpiry.getText()
                        .toString()
                        .trim();

        String cvv =
                edtCvv.getText()
                        .toString()
                        .trim();

        // =====================================================
        // VALIDATE CARD HOLDER
        // =====================================================

        if (TextUtils.isEmpty(cardHolder)) {

            edtCardHolder.setError(
                    "Enter card holder name"
            );

            edtCardHolder.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE CARD NUMBER
        // =====================================================

        if (!cardNumber.equals(
                "4242424242424242"
        )) {

            edtCardNumber.setError(
                    "Use the dummy card number: 4242 4242 4242 4242"
            );

            edtCardNumber.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE EXPIRY
        // =====================================================

        if (!expiry.equals("12/30")) {

            edtExpiry.setError(
                    "Use dummy expiry: 12/30"
            );

            edtExpiry.requestFocus();

            return;
        }

        // =====================================================
        // VALIDATE CVV
        // =====================================================

        if (!cvv.equals("123")) {

            edtCvv.setError(
                    "Use dummy CVV: 123"
            );

            edtCvv.requestFocus();

            return;
        }

        // =====================================================
        // CHECK EXISTING PAYMENT
        // =====================================================

        if (paymentAlreadyExists()) {

            Toast.makeText(
                    this,
                    "Payment has already been recorded",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // =====================================================
        // SAVE SUCCESSFUL PAYMENT
        // =====================================================

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
                "Card"
        );

        values.put(
                "paymentStatus",
                "PAID"
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
                    "Card payment successful!",
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