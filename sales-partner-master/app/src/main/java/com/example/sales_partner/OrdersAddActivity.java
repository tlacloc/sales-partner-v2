package com.example.sales_partner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sales_partner.dao.AssemblyDao;
import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.dao.OrderDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.Assembly;
import com.example.sales_partner.model.AssemblyExtended;
import com.example.sales_partner.model.Customer;
import com.example.sales_partner.model.Order;
import com.example.sales_partner.model.OrderAssemblies;
import com.example.sales_partner.model.OrderCustomer;
import com.example.sales_partner.model.OrderExtended;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersAddActivity extends AppCompatActivity {

    // DATA OBJECTS
    private CustomerDao customerDao;
    private AssemblyDao assemblyDao;
    private OrderAssembliesDao orderAssembliesDao;
    private OrderDao ordersDao;

    // Models
    private List<Customer> customers;
    public static List<OrderExtended> assemblies;
    private ViewModel model;

    // Filled if sent order to edit
    private OrderCustomer orderEdit = null;

    class ViewModel{
        public List<Customer> customers;
    }


    //////////////////////
    // GETTERS AND SETTERS
    /////////////////////


    // VIEW COMPONENTS
    Spinner customerSpinner;
    ListView assemblyListView;
    Button saveButton;
    TextView searchEditText;

    //
    private ArrayAdapter adapter;


    private boolean saved;
    private String okString = "Agregar Orden";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_add);
        //ActivityOrdersAddBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_orders_add);

        // IF WE GET ORDER WE ARE EDITING
        Object o = getIntent().getSerializableExtra("order");
        if(o != null) orderEdit = (OrderCustomer)o;

        // Get User Dao
        customerDao = AppDatabase.getAppDatabase(getApplicationContext()).customerDao();
        assemblyDao = AppDatabase.getAppDatabase(getApplicationContext()).assemblyDao();
        ordersDao = AppDatabase.getAppDatabase(getApplicationContext()).orderDao();
        orderAssembliesDao = AppDatabase.getAppDatabase(getApplicationContext()).orderAssembliesDao();

        // VIEW COMPONENTS INIT
        customerSpinner = findViewById(R.id.sprOrderAddCustomer);
        assemblyListView = findViewById(R.id.lvOrderAddAssemblies);
        saveButton = findViewById(R.id.btnAddOrder);
        searchEditText = findViewById(R.id.clientSearchTxt);

        // GET DATA FROM DB
        model = new ViewModel();
        customers = customerDao.getAll();
        model.customers = customers;
        //assemblies = assemblyDao.getExtendedByDescription("%%");

        if(orderEdit!=null) {
            assemblies = assemblyDao.findByOrderId(orderEdit.id);
            okString = "Actualizar Orden";
        }else assemblies = new ArrayList<OrderExtended>();

        // Define Adapters
        ArrayAdapter customersAdapter = new ArrayAdapter(this, R.layout.text_list, customers);
        adapter = new AssemblyListAdapter(this, android.R.id.text1, assemblies);

        // SAVE Button listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OrdersAddActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmacion")
                        .setCancelable(false)
                        .setMessage("Revisa tu pedido, esta acción no podrá deshacerse")
                        .setPositiveButton(okString, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ////// ADD ORDER TO DATABASE, SAVE DATE AND STATUS TO PENDIENTE
                                try {
                                    saveAll();
                                    Toast.makeText(OrdersAddActivity.this,"Orden Guardada", Toast.LENGTH_LONG).show();
                                } catch (Exception e){
                                    Toast.makeText(OrdersAddActivity.this,"Error al guardar", Toast.LENGTH_LONG).show();
                                };

                                Intent intent = new Intent(getApplicationContext(),OrdersActivity.class);
                                intent.putExtra("tag","start");
                                startActivity(intent);
                                finish();

                            }
                        })
                        .setNegativeButton("Regresar", null)
                        .show();

            }
        });

        // SET ADAPTERS
        customerSpinner.setAdapter(customersAdapter);
        assemblyListView.setAdapter(adapter);

        if(orderEdit != null){
            int customerId = orderEdit.customerId;
            Customer selectedCustomer = customerDao.findById(customerId);

            ////// UNCOMMENT IF USE SAME SPINNER BUT DISABLED
            /*
            int position = customersAdapter.getPosition(selectedCustomer);
            customerSpinner.setSelection(position);
            customerSpinner.setEnabled(false);
            */

            // show text edit
            TextView textViewCustomer = findViewById(R.id.txtOrderAddCustomer);
            textViewCustomer.setVisibility(View.VISIBLE);
            textViewCustomer.setText(selectedCustomer.toString());
            // hide Spinner
            customerSpinner.setVisibility(View.GONE);
        }


        registerForContextMenu(assemblyListView);


        saved = false;




    }

    private void saveAll() {
        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
        db.beginTransaction();
        try{
            long orderId = 0;
            //do multiple database operations here
            if(orderEdit==null){
                // we are adding a new order
                orderId = saveOrder(); // gets data and saves order
            }else{
                // we are editting an order
                orderId = orderEdit.id;
                orderAssembliesDao.deleteByOrderId((int) orderId);
            }

            long[] ids = saveOrderAssemblies((int) orderId);

            //which throws exceptions on error
            db.setTransactionSuccessful();
            //do not any more database operations between
            //setTransactionSuccessful and endTransaction
        }catch(Exception e){
            //end the transaction on error too when doing exception handling
            db.endTransaction();
            throw e;
        }
        //end the transaction on no error
        db.endTransaction();
    }

    private long[] saveOrderAssemblies(int id) {
        List<OrderAssemblies> assembliesToSave = new ArrayList<OrderAssemblies>();
        for (OrderExtended assembly : assemblies) {
            int assemblyId = assembly.getAssemblyId();
            int qty = assembly.getQty();
            if(qty==0)
                continue;

            OrderAssemblies newOrderAssembly = new OrderAssemblies();
            newOrderAssembly.setQty(qty);
            newOrderAssembly.setId(id);
            newOrderAssembly.setAssemblyId(assemblyId);
            assembliesToSave.add(newOrderAssembly);
        }
        OrderAssemblies[] arr = assembliesToSave.toArray(new OrderAssemblies[0]);

        // SAVE TO DATABASE
        long[] ids = orderAssembliesDao.insertAll(arr);

        return ids;
    }

    private long saveOrder() {
        Customer selectedCustomer = (Customer) customerSpinner.getSelectedItem();
        // Get current date
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();

        // Vars to save
        // Get customer id
        int customerId = selectedCustomer.getId();

        int statusId = 0; ///// 0 = Pendiente
        String date = dateFormat.format(d);

        Order newOrder = new Order();
        newOrder.setCustomerId(customerId);
        newOrder.setStatusId(statusId);
        newOrder.setDate(date);

        // SAVE TO DATABASE
        long[] ids  = ordersDao.insertAll(newOrder);

        // get the id of the saved order
        long id = -1;

        if(ids.length>0) // validate
            id = ids[0];

        return id;
    }

    //GENERATE TOOLBAR MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.clients_menu_toolbar, menu);
       MenuItem o = menu.findItem(R.id.search_menu_item);
       o.setVisible(false);
       return super.onCreateOptionsMenu(menu);
    }

    // SEARCH BUTTON ACTION
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_menu_item:

                Intent IntCustomers = new Intent(getApplicationContext(),AssembliesActivity.class);
                IntCustomers.putExtra("orderAddEdit",true);
                startActivityForResult(IntCustomers,1);
                //startActivity(IntCustomers);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == RESULT_CANCELED) {
            // Si es así mostramos mensaje de cancelado por pantalla.
            Toast.makeText(this, "Resultado cancelado", Toast.LENGTH_SHORT)
                    .show();
        } else {
            // De lo contrario, recogemos el resultado de la segunda actividad.
            AssemblyExtended resultado = (AssemblyExtended) data.getExtras().getSerializable("assembly");
            OrderExtended already = null;
            for (OrderExtended assembly : assemblies) {
                if(assembly.getAssemblyId()==resultado.getId()){
                    already = assembly;
                }
            }
            if(already != null)
                already.setQty(already.getQty()+1);
            else
                assemblies.add(resultado.toOrderExtended(0,1));

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual_aux, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.action_auxdelete:
                final OrderExtended selectedAssembly = (OrderExtended) adapter.getItem(info.position);
                adapter.remove(selectedAssembly);
                /*Intent i = getIntent();
                i.putExtra("assembly",selectedAssembly);
                //OrdersAddActivity.assemblies.add(selectedAssembly.toOrderExtended(0,1));
                setResult(RESULT_OK, i);
                finish();*/
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    //CONFIRM DELETE UNSAVED DATA
    @Override
    public void onBackPressed() {
        if (!saved) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cerrando ventana")
                    .setCancelable(false)
                    .setMessage("¿Está seguro que desea salir sin guardar")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}