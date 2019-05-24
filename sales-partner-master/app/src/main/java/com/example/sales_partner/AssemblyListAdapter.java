package com.example.sales_partner;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.sales_partner.model.AssemblyExtended;
import com.example.sales_partner.model.OrderExtended;

import java.util.List;

public class AssemblyListAdapter extends ArrayAdapter {
    private Activity context;
    private List <OrderExtended> orders;

    public AssemblyListAdapter(Activity context, int textViewResourceId, List objects){
        super(context, R.layout.txt_box_item, textViewResourceId, objects);
        this.context = context;
        this.orders = objects;
    }

    @Override
    public int getCount() {
        return this.orders.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        OrderExtended assembly = orders.get(position);

            LayoutInflater inflater = context.getLayoutInflater();
            rowView=inflater.inflate(R.layout.txt_box_item, null,true);

            TextView assemblyText = (TextView) rowView.findViewById(R.id.tvAssemblyText);
            NumberPicker qtyNumberPicker = (NumberPicker) rowView.findViewById(R.id.npAssemblyQty);
            qtyNumberPicker.setMinValue(1);
            qtyNumberPicker.setMaxValue(7);

            assemblyText.setText(assembly.getAssembly());
            qtyNumberPicker.setValue(assembly.getQty());
            qtyNumberPicker.setTag(position);

            qtyNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    // update data model if value changed
                    int pos = (Integer) picker.getTag();
                    OrderExtended selectedOrder = (OrderExtended) orders.get(pos);
                    selectedOrder.setQty(newVal);
                }
            });



        return rowView;
    }

}
