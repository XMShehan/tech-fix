package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ProductListActivity extends AppCompatActivity {

    private LinearLayout productListContainer;

    private Button btnAll;
    private Button btnMobile;
    private Button btnComputer;

    private EditText edtSearch;

    private DatabaseHelper databaseHelper;

    private ArrayList<Service> serviceList;

    private String selectedCategory = "All";

    private String customerId;
    private String customerName;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_product_list
        );

        productListContainer =
                findViewById(
                        R.id.productListContainer
                );

        btnAll =
                findViewById(R.id.btnAll);

        btnMobile =
                findViewById(R.id.btnMobile);

        btnComputer =
                findViewById(R.id.btnComputer);

        edtSearch =
                findViewById(R.id.edtSearch);

        databaseHelper =
                new DatabaseHelper(this);

        serviceList =
                new ArrayList<>();

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
        // LOAD SERVICES
        // =====================================================

        loadServices();

        // =====================================================
        // ALL
        // =====================================================

        btnAll.setOnClickListener(v -> {

            selectedCategory = "All";

            filterServices(
                    edtSearch
                            .getText()
                            .toString()
            );
        });

        // =====================================================
        // MOBILE
        // =====================================================

        btnMobile.setOnClickListener(v -> {

            selectedCategory = "Mobile";

            filterServices(
                    edtSearch
                            .getText()
                            .toString()
            );
        });

        // =====================================================
        // COMPUTER
        // =====================================================

        btnComputer.setOnClickListener(v -> {

            selectedCategory = "Computer";

            filterServices(
                    edtSearch
                            .getText()
                            .toString()
            );
        });

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterServices(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {

            loadServices();
        }
    }

    // =========================================================
    // LOAD ACTIVE SERVICES
    // =========================================================

    private void loadServices() {

        serviceList.clear();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor = null;

        try {

            cursor =
                    db.rawQuery(
                            "SELECT serviceId, serviceName, " +
                                    "category, description, price, duration " +
                                    "FROM services " +
                                    "WHERE status = ? " +
                                    "ORDER BY category, serviceName",

                            new String[]{
                                    "Active"
                            }
                    );

            while (cursor.moveToNext()) {

                serviceList.add(
                        new Service(
                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                "serviceId"
                                        )
                                ),

                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                "serviceName"
                                        )
                                ),

                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                "category"
                                        )
                                ),

                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                "description"
                                        )
                                ),

                                cursor.getDouble(
                                        cursor.getColumnIndexOrThrow(
                                                "price"
                                        )
                                ),

                                cursor.getString(
                                        cursor.getColumnIndexOrThrow(
                                                "duration"
                                        )
                                )
                        )
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        filterServices(
                edtSearch.getText().toString()
        );
    }

    // =========================================================
    // FILTER SERVICES
    // =========================================================

    private void filterServices(
            String searchText) {

        ArrayList<Service> filteredList =
                new ArrayList<>();

        String search =
                searchText
                        .toLowerCase(
                                Locale.getDefault()
                        )
                        .trim();

        for (Service service : serviceList) {

            boolean categoryMatches;

            if (selectedCategory.equalsIgnoreCase("All")) {

                categoryMatches = true;

            } else {

                categoryMatches =
                        service.category != null
                                && service.category
                                .equalsIgnoreCase(
                                        selectedCategory
                                );
            }

            boolean searchMatches =
                    service.serviceName
                            .toLowerCase(
                                    Locale.getDefault()
                            )
                            .contains(search)

                            ||

                            (service.description != null &&
                                    service.description
                                            .toLowerCase(
                                                    Locale.getDefault()
                                            )
                                            .contains(search))

                            ||

                            (service.category != null &&
                                    service.category
                                            .toLowerCase(
                                                    Locale.getDefault()
                                            )
                                            .contains(search));

            if (categoryMatches &&
                    searchMatches) {

                filteredList.add(service);
            }
        }

        displayServices(
                filteredList
        );
    }

    // =========================================================
    // DISPLAY SERVICES
    // =========================================================

    private void displayServices(
            ArrayList<Service> services) {

        productListContainer.removeAllViews();

        if (services.isEmpty() &&
                !selectedCategory.equalsIgnoreCase("All") &&
                !selectedCategory.equalsIgnoreCase("Mobile") &&
                !selectedCategory.equalsIgnoreCase("Computer")) {

            TextView emptyText =
                    createEmptyText(
                            "No repair services available"
                    );

            productListContainer.addView(
                    emptyText
            );

            return;
        }

        for (Service service : services) {

            createServiceCard(service);
        }

        // =====================================================
        // OTHER REPAIR OPTION
        // Available for ALL, Mobile and Computer
        // =====================================================

        if (selectedCategory.equalsIgnoreCase("All")) {

            createOtherCard(
                    "Other"
            );

        } else if (selectedCategory.equalsIgnoreCase("Mobile")
                || selectedCategory.equalsIgnoreCase("Computer")) {

            createOtherCard(
                    selectedCategory
            );
        }
    }

    // =========================================================
    // CREATE SERVICE CARD
    // =========================================================

    private void createServiceCard(
            Service service) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                22,
                22,
                22,
                22
        );

        card.setBackground(
                getDrawable(
                        R.drawable.bg_dashboard_card
                )
        );

        card.setElevation(2);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                15
        );

        card.setLayoutParams(
                cardParams
        );

        TextView nameText =
                new TextView(this);

        nameText.setText(
                service.serviceName
        );

        nameText.setTextSize(20);

        nameText.setTextColor(
                Color.rgb(
                        38,
                        50,
                        56
                )
        );

        nameText.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(nameText);

        TextView categoryText =
                new TextView(this);

        categoryText.setText(
                service.category
        );

        categoryText.setTextSize(14);

        categoryText.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        LinearLayout.LayoutParams categoryParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        categoryParams.setMargins(
                0,
                5,
                0,
                0
        );

        categoryText.setLayoutParams(
                categoryParams
        );

        card.addView(categoryText);

        if (service.description != null &&
                !service.description.trim().isEmpty()) {

            TextView descriptionText =
                    new TextView(this);

            descriptionText.setText(
                    service.description
            );

            descriptionText.setTextSize(14);

            descriptionText.setTextColor(
                    Color.rgb(
                            96,
                            125,
                            139
                    )
            );

            LinearLayout.LayoutParams descriptionParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            descriptionParams.setMargins(
                    0,
                    8,
                    0,
                    0
            );

            descriptionText.setLayoutParams(
                    descriptionParams
            );

            card.addView(
                    descriptionText
            );
        }

        TextView priceText =
                new TextView(this);

        priceText.setText(
                "Estimated Price: Rs. " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                service.price
                        )
        );

        priceText.setTextSize(21);

        priceText.setTextColor(
                Color.rgb(
                        25,
                        118,
                        210
                )
        );

        priceText.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams priceParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        priceParams.setMargins(
                0,
                12,
                0,
                3
        );

        priceText.setLayoutParams(
                priceParams
        );

        card.addView(priceText);

        TextView durationText =
                new TextView(this);

        durationText.setText(
                "Estimated Duration: " +
                        service.duration
        );

        durationText.setTextSize(14);

        durationText.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        card.addView(durationText);

        Button btnBook =
                new Button(this);

        btnBook.setText(
                "BOOK THIS SERVICE"
        );

        btnBook.setTextColor(
                Color.WHITE
        );

        btnBook.setTextSize(14);

        btnBook.setTypeface(
                null,
                Typeface.BOLD
        );

        btnBook.setBackground(
                getDrawable(
                        R.drawable.bg_login_button
                )
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        buttonParams.setMargins(
                0,
                15,
                0,
                0
        );

        btnBook.setLayoutParams(
                buttonParams
        );

        btnBook.setOnClickListener(v ->
                openAppointment(
                        service.serviceId,
                        service.serviceName,
                        service.category,
                        service.price,
                        service.duration,
                        false
                )
        );

        card.addView(btnBook);

        productListContainer.addView(card);
    }

    // =========================================================
    // OTHER CARD
    // =========================================================

    private void createOtherCard(
            String category) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                22,
                22,
                22,
                22
        );

        card.setBackground(
                getDrawable(
                        R.drawable.bg_dashboard_card
                )
        );

        card.setElevation(2);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                15
        );

        card.setLayoutParams(
                cardParams
        );

        TextView title =
                new TextView(this);

        title.setText(
                "Other Repair"
        );

        title.setTextSize(20);

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setTextColor(
                Color.rgb(
                        38,
                        50,
                        56
                )
        );

        card.addView(title);

        TextView description =
                new TextView(this);

        description.setText(
                "Can't find your repair service? " +
                        "Describe the problem and our team will review it."
        );

        description.setTextSize(14);

        description.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.setMargins(
                0,
                8,
                0,
                0
        );

        description.setLayoutParams(
                descriptionParams
        );

        card.addView(description);

        TextView price =
                new TextView(this);

        price.setText(
                "Estimated Price: To be confirmed"
        );

        price.setTextSize(15);

        price.setTextColor(
                Color.rgb(
                        25,
                        118,
                        210
                )
        );

        price.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams priceParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        priceParams.setMargins(
                0,
                12,
                0,
                0
        );

        price.setLayoutParams(priceParams);

        card.addView(price);

        Button btnBook =
                new Button(this);

        btnBook.setText(
                "BOOK OTHER REPAIR"
        );

        btnBook.setTextColor(
                Color.WHITE
        );

        btnBook.setTextSize(14);

        btnBook.setTypeface(
                null,
                Typeface.BOLD
        );

        btnBook.setBackground(
                getDrawable(
                        R.drawable.bg_login_button
                )
        );

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        buttonParams.setMargins(
                0,
                15,
                0,
                0
        );

        btnBook.setLayoutParams(
                buttonParams
        );

        btnBook.setOnClickListener(v ->
                openAppointment(
                        "OTHER",
                        "Other",
                        category,
                        0.0,
                        "To be confirmed",
                        true
                )
        );

        card.addView(btnBook);

        productListContainer.addView(card);
    }

    // =========================================================
    // OPEN APPOINTMENT
    // =========================================================

    private void openAppointment(
            String serviceId,
            String serviceName,
            String category,
            double price,
            String duration,
            boolean otherService) {

        Intent intent =
                new Intent(
                        ProductListActivity.this,
                        AppointmentActivity.class
                );

        intent.putExtra(
                "serviceId",
                serviceId
        );

        intent.putExtra(
                "productName",
                serviceName
        );

        intent.putExtra(
                "category",
                category
        );

        intent.putExtra(
                "price",
                price
        );

        intent.putExtra(
                "duration",
                duration
        );

        intent.putExtra(
                "otherService",
                otherService
        );

        intent.putExtra(
                "customerId",
                customerId
        );

        intent.putExtra(
                "customerName",
                customerName
        );

        intent.putExtra(
                "customerEmail",
                customerEmail
        );

        startActivity(intent);
    }

    // =========================================================
    // EMPTY TEXT
    // =========================================================

    private TextView createEmptyText(
            String message) {

        TextView text =
                new TextView(this);

        text.setText(message);

        text.setTextSize(17);

        text.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        text.setGravity(
                Gravity.CENTER
        );

        text.setPadding(
                0,
                50,
                0,
                50
        );

        return text;
    }

    // =========================================================
    // SERVICE CLASS
    // =========================================================

    private static class Service {

        String serviceId;
        String serviceName;
        String category;
        String description;
        double price;
        String duration;

        Service(
                String serviceId,
                String serviceName,
                String category,
                String description,
                double price,
                String duration) {

            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.category = category;
            this.description = description;
            this.price = price;
            this.duration = duration;
        }
    }
}
