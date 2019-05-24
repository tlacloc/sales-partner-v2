package com.example.sales_partner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.sales_partner.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRowAdapter extends ArrayAdapter<Order> {
    private Context mContext;
    private ArrayList<Order> orders;
    private OrderRowAdapter myAdapter;
    private boolean isFromView = false;

    public OrderRowAdapter(Context context, int resource, List<Order> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.orders = (ArrayList<Order>) objects;
        this.myAdapter = this;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.order_row, null);
            
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView
                    .findViewById(R.id.txtDate);
            holder.txtState = (TextView) convertView
                    .findViewById(R.id.lblStatusValue);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Order order = orders.get(position);
        String status = Order.getOrderText(order.stockStatus);

        holder.txtDate.setText(order.getDate());
        holder.txtState.setText(status);

        return convertView;
    }

    private class ViewHolder {
        private TextView txtDate;
        private TextView txtState;
    }
}
