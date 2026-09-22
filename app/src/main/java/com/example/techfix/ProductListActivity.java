package com.example.techfix;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

    private ArrayList<Product> productList;

    private String selectedCategory = "All";

    // Logged-in customer information
    private String customerId;
    private String customerName;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_product_list
        );

        // =====================================================
        // CONNECT UI
        // =====================================================

        productListContainer =
                findViewById(R.id.productListContainer);

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

        productList =
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
        // LOAD PRODUCTS
        // =====================================================

        loadProducts();

        // =====================================================
        // CATEGORY - ALL
        // =====================================================

        btnAll.setOnClickListener(v -> {

            selectedCategory = "All";

            filterProducts(
                    edtSearch.getText().toString()
            );
        });

        // =====================================================
        // CATEGORY - MOBILE
        // =====================================================

        btnMobile.setOnClickListener(v -> {

            selectedCategory = "Mobile";

            filterProducts(
                    edtSearch.getText().toString()
            );
        });

        // =====================================================
        // CATEGORY - COMPUTER
        // =====================================================

        btnComputer.setOnClickListener(v -> {

            selectedCategory = "Computer";

            filterProducts(
                    edtSearch.getText().toString()
            );
        });

        // =====================================================
        // SEARCH
        // =====================================================

        edtSearch.addTextChangedListener(
                new android.text.TextWatcher() {

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

                        filterProducts(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {
                    }
                }
        );
    }

    // =========================================================
    // LOAD PRODUCTS
    // =========================================================

    private void loadProducts() {

        productList.clear();

        SQLiteDatabase db =
                databaseHelper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT productName, category, price, quantity " +
                                "FROM inventory " +
                                "WHERE quantity > 0",
                        null
                );

        while (cursor.moveToNext()) {

            String productName =
                    cursor.getString(0);

            String category =
                    cursor.getString(1);

            double price =
                    cursor.getDouble(2);

            int quantity =
                    cursor.getInt(3);

            Product product =
                    new Product(
                            productName,
                            category,
                            price,
                            quantity
                    );

            productList.add(product);
        }

        cursor.close();

        displayProducts(
                productList
        );
    }

    // =========================================================
    // FILTER PRODUCTS
    // =========================================================

    private void filterProducts(
            String searchText) {

        ArrayList<Product> filteredList =
                new ArrayList<>();

        searchText =
                searchText
                        .toLowerCase(
                                Locale.getDefault()
                        )
                        .trim();

        for (Product product :
                productList) {

            boolean categoryMatches;

            if (selectedCategory.equals("All")) {

                categoryMatches = true;

            } else {

                categoryMatches =
                        product.category.equalsIgnoreCase(
                                selectedCategory
                        );
            }

            boolean searchMatches =
                    product.productName
                            .toLowerCase(
                                    Locale.getDefault()
                            )
                            .contains(searchText)

                            ||

                            product.category
                                    .toLowerCase(
                                            Locale.getDefault()
                                    )
                                    .contains(searchText);

            if (categoryMatches &&
                    searchMatches) {

                filteredList.add(
                        product
                );
            }
        }

        displayProducts(
                filteredList
        );
    }

    // =========================================================
    // DISPLAY PRODUCTS
    // =========================================================

    private void displayProducts(
            ArrayList<Product> products) {

        productListContainer.removeAllViews();

        if (products.isEmpty()) {

            TextView noProducts =
                    new TextView(this);

            noProducts.setText(
                    "No products available"
            );

            noProducts.setTextSize(
                    18
            );

            noProducts.setTextColor(
                    Color.rgb(
                            96,
                            125,
                            139
                    )
            );

            noProducts.setGravity(
                    Gravity.CENTER
            );

            noProducts.setPadding(
                    0,
                    50,
                    0,
                    50
            );

            productListContainer.addView(
                    noProducts
            );

            return;
        }

        for (Product product :
                products) {

            createProductCard(
                    product.productName,
                    product.category,
                    product.price,
                    product.quantity
            );
        }
    }

    // =========================================================
    // CREATE PRODUCT CARD
    // =========================================================

    private void createProductCard(
            String productName,
            String category,
            double price,
            int quantity) {

        // =====================================================
        // MAIN CARD
        // =====================================================

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

        card.setElevation(
                2
        );

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

        // =====================================================
        // PRODUCT NAME
        // =====================================================

        TextView nameText =
                new TextView(this);

        nameText.setText(
                productName
        );

        nameText.setTextSize(
                20
        );

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

        card.addView(
                nameText
        );

        // =====================================================
        // CATEGORY
        // =====================================================

        TextView categoryText =
                new TextView(this);

        categoryText.setText(
                category
        );

        categoryText.setTextSize(
                14
        );

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

        card.addView(
                categoryText
        );

        // =====================================================
        // PRICE
        // =====================================================

        TextView priceText =
                new TextView(this);

        priceText.setText(
                "Rs. " +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                price
                        )
        );

        priceText.setTextSize(
                22
        );

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
                14,
                0,
                0
        );

        priceText.setLayoutParams(
                priceParams
        );

        card.addView(
                priceText
        );

        // =====================================================
        // AVAILABLE QUANTITY
        // =====================================================

        TextView quantityText =
                new TextView(this);

        quantityText.setText(
                "Available: " +
                        quantity
        );

        quantityText.setTextSize(
                14
        );

        quantityText.setTextColor(
                Color.rgb(
                        96,
                        125,
                        139
                )
        );

        LinearLayout.LayoutParams quantityParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        quantityParams.setMargins(
                0,
                5,
                0,
                15
        );

        quantityText.setLayoutParams(
                quantityParams
        );

        card.addView(
                quantityText
        );

        // =====================================================
        // SELECT PRODUCT
        // =====================================================

        Button btnSelect =
                new Button(this);

        btnSelect.setText(
                "SELECT PRODUCT"
        );

        btnSelect.setTextColor(
                Color.WHITE
        );

        btnSelect.setTextSize(
                14
        );

        btnSelect.setTypeface(
                null,
                Typeface.BOLD
        );

        btnSelect.setBackground(
                getDrawable(
                        R.drawable.bg_login_button
                )
        );

        btnSelect.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProductListActivity.this,
                            AppointmentActivity.class
                    );

            // =================================================
            // PRODUCT INFORMATION
            // =================================================

            intent.putExtra(
                    "productName",
                    productName
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
                    "quantity",
                    quantity
            );

            // =================================================
            // CUSTOMER INFORMATION
            // =================================================

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
        });

        card.addView(
                btnSelect
        );

        productListContainer.addView(
                card
        );
    }

    // =========================================================
    // PRODUCT CLASS
    // =========================================================

    private static class Product {

        String productName;
        String category;
        double price;
        int quantity;

        Product(
                String productName,
                String category,
                double price,
                int quantity) {

            this.productName =
                    productName;

            this.category =
                    category;

            this.price =
                    price;

            this.quantity =
                    quantity;
        }
    }
}