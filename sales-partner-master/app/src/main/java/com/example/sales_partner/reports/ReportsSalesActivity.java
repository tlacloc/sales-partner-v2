package com.example.sales_partner.reports;

import android.databinding.adapters.CardViewBindingAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.sales_partner.R;
import com.example.sales_partner.SalesCardListAdapter;
import com.example.sales_partner.dao.ProductDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.SalesData;

import java.util.List;

public class ReportsSalesActivity extends AppCompatActivity {

    SalesCardListAdapter listAdapter;

    public List<SalesData> sales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_sales);

        // get DAO
        ProductDao productsDao = AppDatabase.getAppDatabase(getApplicationContext()).productDao();

        RecyclerView recList = findViewById(R.id.rvSales);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        sales = productsDao.getIncomeByMonth();

        listAdapter = new SalesCardListAdapter(sales);
        recList.setAdapter(listAdapter);

    }
}
