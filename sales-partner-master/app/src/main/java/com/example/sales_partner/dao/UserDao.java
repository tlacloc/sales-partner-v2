package com.example.sales_partner.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.sales_partner.model.User;

import java.util.List;

@Dao
public interface UserDao {


    @Query("DELETE FROM user")
    public void nukeTable();

    @Query("SELECT * FROM user")
    public User GetAll();

    @Insert
    public void InsertarUsuario(User user);

}
