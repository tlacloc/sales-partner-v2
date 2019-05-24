package com.example.sales_partner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.sales_partner.model.SalesData;

import java.util.ArrayList;
import java.util.List;

public class SalesCardListAdapter extends RecyclerView.Adapter<SalesViewHolder> {

    private List<SalesData> salesDataList;

    public SalesCardListAdapter(List<SalesData> list){
        this.salesDataList = list;
    }

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.sales_card, viewGroup, false);

        return new SalesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder salesViewHolder, int i) {
        SalesData ci = salesDataList.get(i);
        salesViewHolder.month.setText(ci.monthYear);
        salesViewHolder.income.setText("" + ci.income);
        salesViewHolder.sales.setText("" + ci.sales);
        //salesViewHolder.month.setText(ci.surname);
    }

    @Override
    public int getItemCount() {
        return salesDataList.size();
    }
}
