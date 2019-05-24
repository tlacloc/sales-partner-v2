package com.example.sales_partner.dao;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import com.example.sales_partner.model.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Query("SELECT * FROM orders")
    List<Order> getAll();

    @Query("DELETE FROM orders")
    public void nukeTable();

    @Query("SELECT * FROM orders WHERE status_id LIKE :statId")
    List<Order> findByStatus(int statId);

    @Query("SELECT * FROM orders WHERE customer_id LIKE :custId")
    List<Order> findByCustomer(int custId);

    @Query("SELECT * FROM orders WHERE customer_id LIKE :custId AND status_id LIKE :statId")
    List<Order> findByCustomerAndStatus(int custId, int statId);

    @Query("SELECT * FROM orders WHERE date LIKE :date")
    List<Order> findByDate(String date);

    @Query("SELECT * FROM orders WHERE date BETWEEN date(:startDate) AND date(:endDate) AND status_id=:statusId")
    List<Order> findByDatesAndStatus(String startDate, String endDate, int statusId);

    @Query("SELECT \n" +
            "id,\n" +
            "status_id,\n" +
            "customerId as customer_id,\n" +
            "change_log,\n" +
            "date\n" +
            "FROM \n" +
            "(SELECT \n" +
            "\t*,\n" +
            "\tSUM(subtotal)  as total\n" +
            "FROM \n" +
            "\t\t(SELECT \n" +
            "\t\torders.id AS id,\n" +
            "\t\torders.date,\n" +
            "\t\torders.change_log,\n" +
            "\t\tcustomers.id AS customerId,\n" +
            "\t\tcustomers.first_name as customerName,\n" +
            "\t\torder_status.description as status,\n" +
            "\t\torder_assemblies.assembly_id as assembly_id,\n" +
            "\t\tSUM(order_assemblies.qty) as assamblies_qty,\n" +
            "\t\tassembly_products.qty as assembly_products_qty,\n" +
            "\t\tproducts.id as product_id,\n" +
            "\t\tproducts.description as product,\n" +
            "\t\tproducts.price as product_price,\n" +
            "\t\tstatus_id,\n" +
            "\t\tSUM(assembly_products.qty*products.price*order_assemblies.qty) as subtotal\n" +
            "\t\tFROM orders \n" +
            "\t\tINNER JOIN customers ON customers.id = orders.customer_id\n" +
            "\t\tINNER JOIN order_status ON order_status.id = orders.status_id\n" +
            "\t\tINNER JOIN order_assemblies ON order_assemblies.id = orders.id\n" +
            "\t\tINNER JOIN assembly_products ON assembly_products.id = order_assemblies.assembly_id\n" +
            "\t\tINNER JOIN products ON products.id = assembly_products.product_id\n" +
            "\t\tWHERE orders.status_id = :statusId \n" +
            "\t\tGROUP BY orders.id, order_assemblies.assembly_id, products.id)\n" +
            "\tGROUP BY id) as InnerQuery\n" +
            "WHERE InnerQuery.total > :lowPrice AND InnerQuery.total < :hiPrice")
    List<Order> findByPriceAndStatus(int lowPrice, int hiPrice, int statusId);

    @Query("SELECT * FROM orders WHERE id LIKE :id")
    Order findById(int id);


    @RawQuery
    List<Order> findByQuery(SimpleSQLiteQuery query);

    @Insert
    long[] insertAll(Order... order);

    @Update
    void update(Order... orders);

    @Delete
    void delete(Order... orders);


}
