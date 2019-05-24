package com.example.sales_partner.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sales_partner.R;
import com.example.sales_partner.dao.ProductDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.NeededProducts;

import java.util.ArrayList;
import java.util.List;


public class ReportsProductsActivity extends AppCompatActivity {
    
    //LOG
    private static final String TAG = "ReportsProductsActivity";

    //VIEW COMPONENTS
    ListView productsList;

    //Lista de productis
    private List<NeededProducts> products;
    //ADAPTERS
    private ArrayAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_products);
        Log.d(TAG, "onCreate: ");

        ProductDao productsDao = AppDatabase.getAppDatabase(getApplicationContext()).productDao();

        //INIT VIEW COMPONENTS
        productsList = findViewById(R.id.listReportsProducts);

        //populate products
        products = new ArrayList<>();
        products = productsDao.findNeededProducts();

        //sending products to the adapter
        productsAdapter = new ArrayAdapter(this, R.layout.text_list, products);
        productsList.setAdapter(productsAdapter);
    }
}
