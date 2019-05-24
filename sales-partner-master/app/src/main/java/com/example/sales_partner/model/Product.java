package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(tableName="products")
public class Product {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private int price;

    @ColumnInfo(name = "qty")
    private int quantity;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // categoryID getter setter
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    // description getter setter
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // price getter setter
    public int getPrice() { return price; }
    public void setPrice(int price) {
        this.price= price;
    }

    // quantity getter setter
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity= quantity;
    }

    public String toString(){
        int descriptionLength =  20;
        return this.description.substring(0, descriptionLength) + " " + this.price + " q: " + this.quantity;
    }

    @Ignore
    public Boolean getProductOut(int qty){
        if((getQuantity()-qty) >= 0) {
            setQuantity(getQuantity() - qty);
            return true;
        }
        else {
            return false;
        }
    }

    @Ignore
    public int assemblyQty = 0;

    @Ignore public boolean notEnough = false;
}