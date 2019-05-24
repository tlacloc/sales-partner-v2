package com.example.sales_partner;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sales_partner.dao.ProductDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.SalesData;

import java.util.List;

public class SalesViewHolder extends RecyclerView.ViewHolder {
    protected TextView month;
    protected TextView income;
    protected TextView sales;

    public SalesViewHolder(@NonNull View v) {
        super(v);
        month =  (TextView) v.findViewById(R.id.infoMesSales);
        income = (TextView)  v.findViewById(R.id.infoMoneySales);
        sales = (TextView) v.findViewById(R.id.infoSales);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = month.getText().toString();
                ProductDao productsDao = AppDatabase.getAppDatabase(v.getContext()).productDao();

                List<SalesData> income = productsDao.getIncome(m);

                String s = "";
                for (SalesData salesData : income) {
                    s += "Ensamble: " + salesData.assemblyDescription + "\n" +
                        "Ventas: " + salesData.sales + "\n" +
                        "Ingreso: " + salesData.income + "\n\n";
                }

                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                    .setTitle("Resumen")
                    .setMessage(s)
                    .show();
            }
        });
    }

}
