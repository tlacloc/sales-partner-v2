package com.example.sales_partner.reports;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.sales_partner.R;

public class ReportsSimActivity extends AppCompatActivity {

    //LOG
    private static final String TAG = "ReportsSimActivity";

    //VIEW COMPONENTS
    Button byCustomer;
    Button byDate;
    Button byPrice;

    //Dao

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_sim);
        Log.d(TAG, "onCreate: ");

        //INIT VIEW COMPONENTS
        byCustomer = findViewById(R.id.btnCustomerSimulation);
        byDate = findViewById(R.id.btnDateSimulation);
        byPrice = findViewById(R.id.btnPriceSimulation);


        byCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: byCustomer");
                Intent intentCustomer = new Intent(getApplicationContext(),SimCustomerActivity.class);
                intentCustomer.putExtra("tag","BY_CUSTOMER");
                startActivity(intentCustomer);

            }
        });

        byDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: byDate");
                Intent intentDate = new Intent(getApplicationContext(),SimCustomerActivity.class);
                intentDate.putExtra("tag","BY_DATE");
                startActivity(intentDate);

            }
        });

        byPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: byPrice");
                Log.d(TAG, "onClick: byDate");
                Intent intentPrice = new Intent(getApplicationContext(),SimCustomerActivity.class);
                intentPrice.putExtra("tag","BY_PRICE");
                startActivity(intentPrice);

            }
        });
    }
}
