package com.example.sales_partner.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.sales_partner.model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Query("DELETE FROM categories")
    public void nukeTable();

    @Insert
    void insertAll(Category category);

    @Update
    void update(Category... category);

    @Delete
    void delete(Category... category);
}