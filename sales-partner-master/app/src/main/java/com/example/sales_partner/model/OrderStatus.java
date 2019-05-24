package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "order_status")
public class OrderStatus {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "editable")
    private boolean editable;
    @ColumnInfo(name = "previous")
    private String previuos;
    @ColumnInfo(name = "next")
    private String next;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getPreviuos() {
        return previuos;
    }

    public void setPreviuos(String previuos) {
        this.previuos = previuos;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String toString() {
        return this.description;
    }
}

