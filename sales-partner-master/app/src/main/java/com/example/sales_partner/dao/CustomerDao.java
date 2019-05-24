package com.example.sales_partner.dao;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.example.sales_partner.model.Customer;

import java.util.List;

@Dao
public interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY last_name ASC, first_name ASC")
    List<Customer> getAll();

    @Query("DELETE FROM customers")
    public void nukeTable();

    @Query("SELECT * FROM customers WHERE  id = :customerId ORDER BY last_name ASC, first_name ASC")
    Customer findById(int customerId);

    @RawQuery
    List<Customer> findByQuery(SimpleSQLiteQuery query);

    @Insert
    void insertAll(Customer... customer);

    @Update
    void update(Customer... customer);

    @Delete
    void delete(Customer... customer);
}