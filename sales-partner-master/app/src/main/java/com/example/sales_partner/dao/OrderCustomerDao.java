package com.example.sales_partner.dao;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import com.example.sales_partner.model.Order;
import com.example.sales_partner.model.OrderCustomer;


import java.util.List;

@Dao
public interface OrderCustomerDao {

    @Query("SELECT \n" +
            "            id,\n" +
            "            customerId AS customerId, \n" +
            "            statusId AS statusId,\n" +
            "            nextStatus AS nextStatus, \n" +
            "            customerName,\n" +
            "            status,\n" +
            "            date,\n" +
            "            SUM(subtotal) as price,\n" +
            "            SUM(assemblies_qty) as assemblies,\n" +
            "            changelog as changelog \n" +
            "            FROM\n" +
            "   (\n" +
            "   SELECT \n" +
            "            orders.id AS id,\n" +
            "            order_status.id AS statusId,\n" +
            "            order_status.next AS nextStatus,\n" +
            "            customers.id AS customerId,\n" +
            "            customers.first_name as customerName,\n" +
            "            orders.date as date,\n" +
            "            order_status.description as status,\n" +
            "            order_assemblies.assembly_id as assembly_id,\n" +
            "            orders.change_log as changelog, \n" +
            "            order_assemblies.qty as assemblies_qty,\n" +
            "            assembly_products.qty as assembly_products_qty,\n" +
            "            products.id as product_id,\n" +
            "            products.description as product,\n" +
            "            products.price as product_price,\n" +
            "            SUM(assembly_products.qty*products.price*order_assemblies.qty) as subtotal\n" +
            "            FROM orders \n" +
            "            INNER JOIN customers ON customers.id = orders.customer_id\n" +
            "            INNER JOIN order_status ON order_status.id = orders.status_id\n" +
            "            LEFT OUTER JOIN order_assemblies ON order_assemblies.id = orders.id\n" +
            "            LEFT OUTER JOIN assembly_products ON assembly_products.id = order_assemblies.assembly_id\n" +
            "            LEFT OUTER JOIN products ON products.id = assembly_products.product_id\n" +
            "            GROUP BY orders.id, order_assemblies.assembly_id " +
            "   ) \n" +
            " GROUP BY id")
    List<OrderCustomer> getAll();

    @RawQuery
    List<OrderCustomer> findByQuery(SimpleSQLiteQuery query);

}
