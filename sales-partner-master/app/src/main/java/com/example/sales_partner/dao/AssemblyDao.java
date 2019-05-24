package com.example.sales_partner.dao;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.sales_partner.model.Assembly;
import com.example.sales_partner.model.AssemblyExtended;
import com.example.sales_partner.model.OrderExtended;


import java.util.List;
@Dao
public interface AssemblyDao {

    @Query("SELECT * FROM assemblies")
    List<Assembly> getAll();

    @Query("DELETE FROM assemblies")
    public void nukeTable();

    @Query("SELECT \n" +
            "\tassemblies.id,\n" +
            "\tassemblies.description,\n" +
            "\tCOUNT(assemblies.id) as numProducts,\n" +
            "\tSUM(products.price) as price\n" +
            "FROM assemblies \n" +
            "INNER JOIN assembly_products ON assembly_products.id = assemblies.id\n" +
            "INNER JOIN products ON products.id = assembly_products.product_id\n" +
            "WHERE assemblies.description LIKE :desc\n" +
            "GROUP BY assemblies.id\n" +
            "ORDER BY assemblies.description ASC")
    List<AssemblyExtended> getExtendedByDescription(String desc);

    @Query("SELECT \n" +
            "id, \n" +
            "assembly_id as assemblyId,\n" +
            "qty as qty, \n" +
            "description assembly,\n" +
            "numProducts,\n" +
            "price as assemblyPrice\n" +
            "\tFROM order_assemblies\n" +
            "\tINNER JOIN (\n" +
            "\tSELECT \n" +
            "\tassemblies.id as assemblyId,\n" +
            "\tassemblies.description,\n" +
            "\tCOUNT(assemblies.id) as numProducts,\n" +
            "\tSUM(products.price) as price\n" +
            "\tFROM assemblies \n" +
            "\tINNER JOIN assembly_products ON assembly_products.id = assemblies.id\n" +
            "\tINNER JOIN products ON products.id = assembly_products.product_id\n" +
            "\tGROUP BY assemblies.id\n" +
            "\t\t\t) tab ON tab.assemblyId = order_assemblies.assembly_id\n" +
            "\tWHERE order_assemblies.id = :orderId")
    List<OrderExtended> findByOrderId(int orderId);

    @Insert
    void insertAll(Assembly assembly);

    @Update
    void update(Assembly... assembly);

    @Delete
    void delete(Assembly... assembly);
}
