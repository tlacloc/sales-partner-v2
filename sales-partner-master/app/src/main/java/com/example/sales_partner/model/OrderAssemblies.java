package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity (tableName = "order_assemblies")
public class OrderAssemblies {
    @PrimaryKey
    @ColumnInfo(name = "key")
    private int key;
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "assembly_id")
    private int assemblyId;
    @ColumnInfo(name = "qty")
    private int qty;

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

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
