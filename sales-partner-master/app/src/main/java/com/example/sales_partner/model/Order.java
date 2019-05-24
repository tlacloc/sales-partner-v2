package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.db.AppDatabase;

import java.util.Date;
import java.util.List;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "status_id")
    private int statusId;

    @ColumnInfo(name = "customer_id")
    private int customerId;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "change_log")
    private String changeLog;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChangeLog() {
        if(changeLog==null)
            return "";
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public String toString(){
        String s = this.date+ "\nStatus: " + this.statusId + "\nCustomer: " + this.customerId;
        if(assemblies!=null){
            for (Assembly assembly : assemblies) {
                s += "\n" + assembly.toString() + "\n";
            }
        }
        return s;
    }

    @Ignore
    public List<Assembly> assemblies;
    @Ignore
    public  int stockStatus;
    public static final int STATUS_OUT_OF_STOCK = 1;
    public static final int STATUS_CAN_DO = 2;
    public static final int STATUS_CANT_DO = 3;

    public List<Assembly> retrieveAssemblies(OrderAssembliesDao orderAssembliesDao){
        assemblies = orderAssembliesDao.findByOrderId(this.id);
        return assemblies;
    }

    public static String getOrderText(int stockStatus){
        String r = "";
        switch (stockStatus){
            case STATUS_CAN_DO:
                r = "Confirmado";
                break;
            case STATUS_CANT_DO:
                r = "No existe productos para confirmarlo";
                break;
            case STATUS_OUT_OF_STOCK:
                r = "Faltan algunos productos para confirmarlo";
                break;
        }
        return r;
    }

    public int canDo(List<Product> products, List<Product> productsToGetOut, int assemblyQty){
        // Mr Meeseeks
        for (Product pout : productsToGetOut) {
            for (Product product : products) {
                if(product.getId() == pout.getId()){
                    // product found!
                    // pout.assemblyQty: num prod necesarios por assembly
                    // assemblyQty: num assamblies pedidos en la orden
                    int dif = product.getQuantity() - pout.assemblyQty*assemblyQty;
                    if(dif<0) {
                        pout.notEnough = true;
                        return Assembly.STATUS_OUT_OF_STOCK;
                    }
                }
            }
        }
        return Assembly.STATUS_CAN_DO;
    }

    public int doIt(List<Product> products, List<Product> productsToGetOut, int assemblyQty){
        for (Product pout : productsToGetOut) {
            for (Product product : products) {
                if(product.getId() == pout.getId()){
                    // product found!
                    product.getProductOut(pout.assemblyQty*assemblyQty);
                    System.out.println(product.getQuantity()); //  Must be positive
                    //int dif = product.getQuantity() - pout.assemblyQty;
                    //if(dif<0) return Assembly.STATUS_OUT_OF_STOCK;
                }
            }
        }
        return Assembly.STATUS_CAN_DO;
    }



}
