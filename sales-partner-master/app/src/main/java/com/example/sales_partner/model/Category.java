package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName="categories")
public class Category {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "description")
    private String description;

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

    public String toString(){
        return this.description;
    }

}

