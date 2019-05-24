package com.example.sales_partner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.sales_partner.reports.ReportsProductsActivity;
import com.example.sales_partner.reports.ReportsSalesActivity;
import com.example.sales_partner.reports.ReportsSimActivity;

public class ReportsActivity extends AppCompatActivity {
    
    //TAG
    private static final String TAG = "ReportsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Log.d(TAG, "onCreate: ");

        ImageButton neededProducts = (ImageButton)findViewById(R.id.ProductsReportsBtn);
        neededProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportsProductsActivity.class);
                //intent.putExtra("customer",selectedCustomer);
                startActivity(intent);
            }
        });

        ImageButton simulation = findViewById(R.id.SimulationReportsBtn);
        ImageButton ReviewReportsBtn = findViewById(R.id.ReviewReportsBtn);

        simulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportsSimActivity.class);
                //intent.putExtra("customer",selectedCustomer);
                startActivity(intent);
            }
        });

        ReviewReportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportsSalesActivity.class);
                //intent.putExtra("customer",selectedCustomer);
                startActivity(intent);
            }
        });
    }
}
