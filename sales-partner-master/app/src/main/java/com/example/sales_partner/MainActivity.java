package com.example.sales_partner;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sales_partner.dao.AssemblyDao;
import com.example.sales_partner.dao.AssemblyProductsDao;
import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.dao.OrderDao;
import com.example.sales_partner.dao.OrderStatusDao;
import com.example.sales_partner.dao.ProductDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.dao.CategoryDao;
import com.example.sales_partner.model.Assembly;
import com.example.sales_partner.model.AssemblyProducts;
import com.example.sales_partner.model.Category;
import com.example.sales_partner.model.Customer;
import com.example.sales_partner.model.Order;
import com.example.sales_partner.model.OrderAssemblies;
import com.example.sales_partner.model.OrderStatus;
import com.example.sales_partner.model.Product;
import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //TAG
    private static final String TAG = "MainActivity";

    //VIEW COMPONENTS
    private ImageButton productsButton;
    private ImageButton assembliesButton;
    private ImageButton customersButton;
    private ImageButton ordersButton;
    private ImageButton reportsButton;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);

        requestQueue= Volley.newRequestQueue(this);

        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        // VIEW COMPONENTS INIT
        productsButton = findViewById(R.id.mainButtonProducts);
        assembliesButton = findViewById(R.id.mainButtonAssembly);
        customersButton = findViewById(R.id.mainButtonCustomers);
        ordersButton = findViewById(R.id.mainButtonOrders);
        reportsButton = findViewById(R.id.mainButtonReports);

        jsonParseAssembly();
        jsonParseAsProducts();
        jsonParseCategories();
        jsonParseClients();
        jsonParseOrdersAssemblies();
        jsonParseOrders();
        jsonParseOrder_Status();
        jsonParseProducts();


        //OnClick Events
        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: productsButton");
                Intent IntProducts = new Intent(getApplicationContext(),ProductsActivity.class);
                IntProducts.putExtra("tag","start");
                startActivity(IntProducts);

            }
        });
        assembliesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: assembliesButton");
                Intent IntAssemblies = new Intent(getApplicationContext(),AssembliesActivity.class);
                IntAssemblies.putExtra("tag","start");
                startActivity(IntAssemblies);

            }
        });
        customersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: customersButton");
                Intent IntCustomers = new Intent(getApplicationContext(),ClientsActivity.class);
                IntCustomers.putExtra("tag","start");
                startActivity(IntCustomers);

            }
        });
        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ordersButton");
                Intent IntOrders = new Intent(getApplicationContext(),OrdersActivity.class);
                IntOrders.putExtra("tag","start");
                startActivity(IntOrders);
            }
        });
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: reportsButton");
                Intent IntReports = new Intent(getApplicationContext(),ReportsActivity.class);
                IntReports.putExtra("tag","start");
                startActivity(IntReports);
            }
        });


        /*
        Category category = new Category();
        category.setDescription("holo");
        dgDao.insertAll(category);
        List<Category> productCategories2 = dgDao.getAll();
        System.out.println(productCategories1);
        System.out.println(productCategories2);
        */

        //List<ProductCategory> categories = db.productCategoryDao().getAll();
    }

    private void jsonParseAssembly(){

        String url = "http://148.209.151.63:3000/api/assemblies";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    AssemblyDao AsDao = db.assemblyDao();
                    AsDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        Assembly assembly = new Assembly();
                        int id = jsonObject.getInt("id");
                        String description = jsonObject.getString("description");
                        assembly.setDescription(description);
                        assembly.setId(id);
                        AsDao.insertAll(assembly);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseAsProducts(){

        String url = "http://148.209.151.63:3000/api/assembly_products";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    AssemblyProductsDao AsPrDao = db.assemblyProductsDao();
                    AsPrDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        AssemblyProducts assemblyProducts = new AssemblyProducts();
                        int key = i;
                        int id = jsonObject.getInt("id");
                        int product_id = jsonObject.getInt("product_id");
                        int qty = jsonObject.getInt("qty");
                        assemblyProducts.setId(id);
                        assemblyProducts.setKey(key);
                        assemblyProducts.setProductId(product_id);
                        assemblyProducts.setQty(qty);
                        AsPrDao.insertAll(assemblyProducts);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseCategories(){

        String url = "http://148.209.151.63:3000/api/categories";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    CategoryDao CaDao = db.categoryDao();
                    CaDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        Category category= new Category();
                        int id = jsonObject.getInt("id");
                        String description = jsonObject.getString("description");
                        category.setId(id);
                        category.setDescription(description);
                        CaDao.insertAll(category);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseClients(){

        String url = "http://148.209.151.63:3000/api/customers";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    CustomerDao CuDao = db.customerDao();
                    CuDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        Customer customer = new Customer();
                        int id = jsonObject.getInt("id");
                        String fname = jsonObject.getString("first_name");
                        String lname = jsonObject.getString("last_name");
                        String address = jsonObject.getString("address");
                        String phone1 = jsonObject.getString("phone1");
                        String phone2 = jsonObject.getString("phone2");
                        String phone3 = jsonObject.getString("phone3");
                        String email = jsonObject.getString("e_mail");
                        if(address.equals("null")){address = "";}
                        if(phone1.equals("null")){phone1 = "";}
                        if(phone2.equals("null")){phone2 = "";}
                        if(phone3.equals("null")){phone3 = "";}
                        if(email.equals("null")){email = "";}
                        customer.setAddress(address);
                        customer.setEmail(email);
                        customer.setFirstName(fname);
                        customer.setId(id);
                        customer.setLastName(lname);
                        customer.setPhone1(phone1);
                        customer.setPhone2(phone2);
                        customer.setPhone3(phone3);
                        CuDao.insertAll(customer);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseOrdersAssemblies(){

        String url = "http://148.209.151.63:3000/api/order_assemblies";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    OrderAssembliesDao OrAsDao = db.orderAssembliesDao();
                    OrAsDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        OrderAssemblies orderAssemblies = new OrderAssemblies();
                        int key = i;
                        int assembly_id = jsonObject.getInt("assembly_id");
                        int qty = jsonObject.getInt("qty");
                        int id = jsonObject.getInt("id");
                        orderAssemblies.setAssemblyId(assembly_id);
                        orderAssemblies.setKey(key);
                        orderAssemblies.setQty(qty);
                        orderAssemblies.setId(id);
                        OrAsDao.insertAll(orderAssemblies);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseOrders(){

        String url = "http://148.209.151.63:3000/api/orders";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    OrderDao OrDao = db.orderDao();
                    OrDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        Order order = new Order();
                        int id = jsonObject.getInt("id");
                        int status_id = jsonObject.getInt("status_id");
                        int customer_id = jsonObject.getInt("customer_id");
                        String date = jsonObject.getString("date");
                        String change_log = jsonObject.getString("change_log");
                        if(change_log.equals("null")){change_log = "";}
                        order.setChangeLog(change_log);
                        order.setCustomerId(customer_id);
                        order.setDate(date);
                        order.setId(id);
                        order.setStatusId(status_id);
                        OrDao.insertAll(order);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseOrder_Status(){

        String url = "http://148.209.151.63:3000/api/order_status";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    OrderStatusDao OrStaDao = db.orderStatusDao();
                    OrStaDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        OrderStatus orderStatus = new OrderStatus();
                        int id = jsonObject.getInt("id");
                        String description = jsonObject.getString("description");
                        int edit = jsonObject.getInt("editable");
                        boolean editable = false;
                        if(edit == 0){ editable = false;}
                        if(edit == 1){ editable = true;}
                        String previous = jsonObject.getString("previous");
                        String next = jsonObject.getString("next");
                        orderStatus.setDescription(description);
                        orderStatus.setEditable(editable);
                        orderStatus.setId(id);
                        orderStatus.setNext(next);
                        orderStatus.setPreviuos(previous);
                        OrStaDao.insertAll(orderStatus);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void jsonParseProducts(){

        String url = "http://148.209.151.63:3000/api/products";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    ProductDao PrDao = db.productDao();
                    PrDao.nukeTable();

                    for(int i = 0; i <= response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);

                        Product product = new Product();
                        int id = jsonObject.getInt("id");
                        int category_id = jsonObject.getInt("category_id");
                        String description = jsonObject.getString("description");
                        int price = jsonObject.getInt("price");
                        int qty = jsonObject.getInt("qty");
                        product.setCategoryId(category_id);
                        product.setDescription(description);
                        product.setId(id);
                        product.setPrice(price);
                        product.setQuantity(qty);
                        PrDao.insertAll(product);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }
}
