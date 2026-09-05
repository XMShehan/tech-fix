package com.example.techfix;

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

public class ProductListActivity extends AppCompatActivity {

    LinearLayout productListContainer;
    Button btnBack;
    EditText edtSearch;

    DatabaseHelper databaseHelper;

    ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_list);

        productListContainer = findViewById(R.id.productListContainer);
        btnBack = findViewById(R.id.btnBack);
        edtSearch = findViewById(R.id.edtSearch);

        databaseHelper = new DatabaseHelper(this);

        productList = new ArrayList<>();

        loadProducts();

        btnBack.setOnClickListener(v -> {
            finish();
        });

        edtSearch.addTextChangedListener(new android.text.TextWatcher() {

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

                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(
                    android.text.Editable s) {
            }
        });
    }

    private void loadProducts() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT productName, category, price, quantity " +
                        "FROM inventory " +
                        "WHERE quantity > 0",
                null
        );

        while (cursor.moveToNext()) {

            String productName = cursor.getString(0);
            String category = cursor.getString(1);
            double price = cursor.getDouble(2);
            int quantity = cursor.getInt(3);

            Product product = new Product(
                    productName,
                    category,
                    price,
                    quantity
            );

            productList.add(product);
        }

        cursor.close();

        displayProducts(productList);
    }

    private void filterProducts(String searchText) {

        ArrayList<Product> filteredList = new ArrayList<>();

        searchText = searchText.toLowerCase().trim();

        for (Product product : productList) {

            if (product.productName.toLowerCase().contains(searchText)
                    || product.category.toLowerCase().contains(searchText)) {

                filteredList.add(product);
            }
        }

        displayProducts(filteredList);
    }

    private void displayProducts(ArrayList<Product> products) {

        productListContainer.removeAllViews();

        if (products.isEmpty()) {

            TextView noProducts = new TextView(this);

            noProducts.setText("No products found");
            noProducts.setTextSize(18);
            noProducts.setGravity(Gravity.CENTER);
            noProducts.setPadding(0, 40, 0, 40);

            productListContainer.addView(noProducts);

            return;
        }

        for (Product product : products) {

            createProductCard(
                    product.productName,
                    product.category,
                    product.price,
                    product.quantity
            );
        }
    }

    private void createProductCard(
            String productName,
            String category,
            double price,
            int quantity) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);

        card.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 20);

        card.setLayoutParams(cardParams);

        // Product name
        TextView nameText = new TextView(this);

        nameText.setText(productName);
        nameText.setTextSize(21);
        nameText.setTypeface(null, Typeface.BOLD);

        card.addView(nameText);

        // Category
        TextView categoryText = new TextView(this);

        categoryText.setText("Category: " + category);
        categoryText.setTextSize(15);

        LinearLayout.LayoutParams categoryParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        categoryParams.setMargins(0, 10, 0, 0);

        categoryText.setLayoutParams(categoryParams);

        card.addView(categoryText);

        // Price
        TextView priceText = new TextView(this);

        priceText.setText(
                "Rs. " + String.format("%.2f", price)
        );

        priceText.setTextSize(19);
        priceText.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams priceParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        priceParams.setMargins(0, 15, 0, 0);

        priceText.setLayoutParams(priceParams);

        card.addView(priceText);

        // Quantity
        TextView quantityText = new TextView(this);

        quantityText.setText("Available: " + quantity);
        quantityText.setTextSize(15);

        LinearLayout.LayoutParams quantityParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        quantityParams.setMargins(0, 5, 0, 10);

        quantityText.setLayoutParams(quantityParams);

        card.addView(quantityText);

        // Select button
        Button btnSelect = new Button(this);

        btnSelect.setText("Select Product");
        btnSelect.setGravity(Gravity.CENTER);

        card.addView(btnSelect);

        productListContainer.addView(card);
    }

    // Product class
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

            this.productName = productName;
            this.category = category;
            this.price = price;
            this.quantity = quantity;
        }
    }
}