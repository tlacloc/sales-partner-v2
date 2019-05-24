package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.sales_partner.dao.AssemblyProductsDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.dao.ProductDao;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "assemblies")
public class Assembly implements Serializable {
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


    public String toString() {
        int descriptionLength =  20;
        String result = description;

        if(description.length()>descriptionLength)
           result = description.substring(0, descriptionLength);
        if(stockStatus!=0 && stockStatus==STATUS_OUT_OF_STOCK)
            result += "\n NO HAY SUFICIENTES EN BODEGA";

        return result;
    }

    @Ignore
    public int quantity;

    @Ignore
    public List<Product> products;

    @Ignore
    public  int stockStatus;
    public static final int STATUS_OUT_OF_STOCK = 1;
    public static final int STATUS_CAN_DO = 2;
    public static final int STATUS_CANT_DO = 3;

    public void retrieveProducts(AssemblyProductsDao assemblyProductsDao){
        products = assemblyProductsDao.findByAssemblyId(id);
    }
    public void retrieveProductQuantity(AssemblyProductsDao assemblyProductsDao){
        for (Product product : products) {
            AssemblyProducts assprod = assemblyProductsDao.findByAssemblyIdAndProductId(id, product.getId());
            int qty = assprod.getQty();
            product.assemblyQty = qty;
        }
    }

    public void retrieveQuantity(OrderAssembliesDao orderAssembliesDao, int orderId){
        OrderAssemblies orderAssemblies = orderAssembliesDao.findByOrderIdAndAssembleId(orderId, getId());
        this.quantity = orderAssemblies.getQty();
    }

}

