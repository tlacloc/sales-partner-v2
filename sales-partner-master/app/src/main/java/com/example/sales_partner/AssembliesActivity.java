package com.example.sales_partner;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sales_partner.dao.AssemblyDao;
import com.example.sales_partner.dao.AssemblyProductsDao;
import com.example.sales_partner.dao.UserDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.Assembly;
import com.example.sales_partner.model.AssemblyExtended;
import com.example.sales_partner.model.AssemblyProducts;
import com.example.sales_partner.model.Product;
import com.example.sales_partner.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssembliesActivity extends AppCompatActivity {
    
    //TAG
    private static final String TAG = "AssembliesActivity";

    //DATA OBJECTS
    private AssemblyDao assemblyDao;
    private AssemblyProductsDao assemblyProductsDao;

    // VIEW COMPONENTS
    private EditText searchEditText;

    // Lista de todos los ensambles
    private List<AssemblyExtended> assemblies;

    // Adapters
    private ArrayAdapter assembliesAdapter;

    private Boolean addEdit = false;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assemblies);

        requestQueue= Volley.newRequestQueue(this);
        jsonParse();
        jsonParseAsProducts();

        addEdit = getIntent().getBooleanExtra("orderAddEdit",false);

        Log.d(TAG, "onCreate: ");

        //DB INIT
        assemblyDao = AppDatabase.getAppDatabase(getApplicationContext()).assemblyDao();
        assemblyProductsDao = AppDatabase.getAppDatabase(getApplicationContext()).assemblyProductsDao();

        // VIEW COMPONENTS INIT
        searchEditText = findViewById(R.id.assembliesTxt);
        assemblies = new ArrayList<AssemblyExtended>();
        List<AssemblyExtended> tmp = assemblyDao.getExtendedByDescription("%%");
        assemblies.addAll(tmp);

        // ADAPTERS
        assembliesAdapter = new ArrayAdapter(this, R.layout.text_list, assemblies);

        ListView assembliesList = (ListView) findViewById(R.id.assembliesList);
        assembliesList.setAdapter(assembliesAdapter);

        // REGISTRAR CONTEXT MENU
        if(addEdit)
            registerForContextMenu(assembliesList);

        assembliesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AssemblyExtended selectedAssembly = (AssemblyExtended) assembliesAdapter.getItem(position);
                List<Product> products = assemblyProductsDao.findByAssemblyId(selectedAssembly.getId());
                String listOfProductsString = "";

                for (Product product : products) {
                    listOfProductsString += product.getDescription() + "\n" +
                            " Q: " + product.getQuantity() + " P: $" + product.getPrice() + "\n\n";
                }

                AlertDialog alertDialog = new AlertDialog.Builder(parent.getContext())
                        .setTitle("Ensamble")
                        .setMessage(
                                "Producto: " + selectedAssembly.getDescription() + " " + "\n\n" +
                                "Precio Total: $" + selectedAssembly.getPrice() + " " + "\n\n" +
                                "Productos: \n\n" + listOfProductsString
                        )
                        .show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.products_menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // SEARCH BUTTON ACTION
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu_item:
                // text to search
                String query = searchEditText.getText().toString();
                query = "%" + query + "%";

                //
                List<AssemblyExtended> a = new ArrayList<AssemblyExtended>();
                a = assemblyDao.getExtendedByDescription(query);

                // add products to model
                this.assemblies.clear();
                this.assemblies.addAll(a);

                // notify adapter to update view
                assembliesAdapter.notifyDataSetChanged();


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual_auxadden, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final AssemblyExtended selectedAssembly = (AssemblyExtended) assembliesAdapter.getItem(info.position);

        switch (item.getItemId()) {

            case R.id.action_auxadd:
                Intent i = getIntent();
                i.putExtra("assembly",selectedAssembly);
                //OrdersAddActivity.assemblies.add(selectedAssembly.toOrderExtended(0,1));
                setResult(RESULT_OK, i);
                finish();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void jsonParse(){

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

}
