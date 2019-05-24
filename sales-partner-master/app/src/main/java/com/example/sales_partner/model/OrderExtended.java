package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

public class OrderExtended {

    private int id;
    private int assemblyId;
    private int qty;
    private String assembly;
    private int numProducts;
    private int assemblyPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssemblyId() {
        return assemblyId;
    }

    public void setAssemblyId(int assemblyId) {
        this.assemblyId = assemblyId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(int numProducts) {
        this.numProducts = numProducts;
    }

    public int getAssemblyPrice() {
        return assemblyPrice;
    }

    public void setAssemblyPrice(int assemblyPrice) {
        this.assemblyPrice = assemblyPrice;
    }

    public String toString(){
        return assembly + "\nCantidad: " + qty + "\nPrecio: " + assemblyPrice;
    }


}
