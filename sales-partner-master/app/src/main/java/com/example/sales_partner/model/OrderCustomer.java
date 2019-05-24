package com.example.sales_partner.model;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.RawQuery;

import java.io.Serializable;
import java.util.List;

public class OrderCustomer implements Serializable {
   public int id;
   public String customerName;

   public String status;
   public int statusId;
   public String nextStatus;


   public int customerId;
   public String date;
   public int assemblies;
   public int price;
   public String changelog;


   public String toString() {
      return this.customerName + " " + this.status + " " + this.date + " " + this.assemblies + " $" + this.price;
   }


}
