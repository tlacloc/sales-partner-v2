package com.example.sales_partner;

import android.app.DatePickerDialog;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.dao.OrderCustomerDao;
import com.example.sales_partner.dao.OrderDao;
import com.example.sales_partner.dao.OrderStatusDao;
import com.example.sales_partner.databinding.ActivityClientsAddBinding;
import com.example.sales_partner.databinding.ActivityOrdersBinding;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.Customer;
import com.example.sales_partner.model.Order;
import com.example.sales_partner.model.OrderAssemblies;
import com.example.sales_partner.model.OrderCustomer;
import com.example.sales_partner.model.OrderStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    private static final String CERO = "0";
    private static final String BARRA = "-";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //TAG
    private static final String TAG = "OrdersActivity";

    // DATA OBJECTS
    private OrderDao orderDao;
    private CustomerDao customerDao;
    private OrderStatusDao orderStatusDao;
    private OrderCustomerDao orderCustomerDao;

    private boolean enableStartDate = false;
    private boolean enableEndDate = false;

    /////// GETTERS AND SETTERS

    public boolean isEnableStartDate() {
        return enableStartDate;
    }

    public void setEnableStartDate(boolean enableStartDate) {
        this.enableStartDate = enableStartDate;
    }

    public boolean isEnableEndDate() {
        return enableEndDate;
    }

    public void setEnableEndDate(boolean enableEndDate) {
        this.enableEndDate = enableEndDate;
    }

    // VIEW COMPONENTS
    private Spinner statusSpnr;
    private Spinner customerSpnr;

    private CheckBox dateEndCheckBox;
    private CheckBox dateStartCheckBox;

    private EditText dateStartEditTxt;
    private EditText dateEndEditTxt;

    private Button dateEndBtn;
    private Button dateStartBtn;
    private ListView orderList;

    //List
    private List<OrderCustomer> orders;
    private ArrayList<StateVO> listVOs;

    // Adapters
    private ArrayAdapter orderAdapter;

    //Ints (?)
    private int custId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_orders);
        ActivityOrdersBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_orders);

        binding.setEnableStartDate(enableStartDate);
        binding.setEnableEndDate(enableEndDate);

        requestQueue= Volley.newRequestQueue(this);
        jsonParseClients();
        jsonParseOrders();
        jsonParseOrder_Status();
        jsonParseOrdersAssemblies();

        orderDao = AppDatabase.getAppDatabase(getApplicationContext()).orderDao();
        customerDao = AppDatabase.getAppDatabase(getApplicationContext()).customerDao();
        orderStatusDao = AppDatabase.getAppDatabase(getApplicationContext()).orderStatusDao();
        orderCustomerDao = AppDatabase.getAppDatabase(getApplicationContext()).orderCustomerDao();

        // VIEW COMPONENTS INIT
        statusSpnr = findViewById(R.id.statusOrdersSpnrSelect);
        customerSpnr = findViewById(R.id.customerOrdersSpnrSelect);

        dateEndCheckBox = findViewById(R.id.checkBoxOrdersEndDate);
        dateStartCheckBox = findViewById(R.id.checkBoxOrdersStartDate);

        dateStartEditTxt = findViewById(R.id.editTextOrdersStartDate);
        dateEndEditTxt = (EditText) findViewById(R.id.editTextOrdersEndDate);

        dateEndBtn = findViewById(R.id.btnOrdersEndDate);
        dateStartBtn = findViewById(R.id.btnOrdersStartDate);

        orderList = findViewById(R.id.orderList);

        // init customer data model
        orders = new ArrayList<OrderCustomer>();

        // ADAPTERS
        orderAdapter = new ArrayAdapter(this, R.layout.text_list, orders);
        orderList.setAdapter(orderAdapter);

        // field selection options
        String[] customerFields = {"Select field", "Pendiente", "Cancelado", "Confirmado", "En transito", "Finalizado"};
        listVOs = new ArrayList<>();

        for (int i = 0; i < customerFields.length; i++) {
            StateVO stateVO = new StateVO();
            stateVO.setTitle(customerFields[i]);
            stateVO.setSelected(false);
            listVOs.add(stateVO);
        }
        SpinnerCheckboxAdapter spinnerCheckboxAdapter = new SpinnerCheckboxAdapter(getApplicationContext(), 0,
                listVOs);
        statusSpnr.setAdapter(spinnerCheckboxAdapter);


        //ADDING TODOS AS A OPTION
        Customer todos = new Customer();
        todos.setFirstName("Seleccionar");
        todos.setLastName("todos");
        todos.setId(-1);

        //LISTA DE CLIENTES
        List<Customer> customers = new ArrayList<Customer>();
        customers.add(todos);
        customers.addAll(customerDao.getAll());

        ArrayAdapter customerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, customers);
        customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        customerSpnr.setAdapter(customerAdapter);

        // ADDING ORDERS TO LISTVIEW
        List<OrderCustomer> o = orderCustomerDao.getAll();
        this.orders.clear();
        this.orders.addAll(o);

        // notify adapter to update view
        orderAdapter.notifyDataSetChanged();

        // REGISTRAR CONTEXT MENU
        registerForContextMenu(orderList);

        //CUSTOMER SPINNER
        customerSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Customer selectedCustomer = (Customer) parent.getItemAtPosition(position);

                custId = selectedCustomer.getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void obtenerFechaInicial(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                dateStartEditTxt.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }
    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                dateEndEditTxt.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
            }
            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }

    public void showDatePickerDialog(View v) {
        obtenerFecha();
    }
    public void showStartDatePickerDialog(View v) {
        obtenerFechaInicial();
    }

    //GENERATE TOOLBAR MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clients_menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // SEARCH BUTTON ACTION
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_menu_item:

                onSearchMenuClicked();

                return true;

            case R.id.add_menu_item:
                Intent intent = new Intent(getApplicationContext(),OrdersAddActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int id= v.getId();
        MenuInflater inflater= getMenuInflater();

        switch (id){
            case R.id.orderList:
                // GET SELECTED ITEM
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                OrderCustomer selectedOrder = (OrderCustomer) orderAdapter.getItem(info.position);

                // INFLATE VIEW
                inflater.inflate(R.menu.menu_contextual_ordenes , menu);

                // DISABLE NEXT STATUS IF NECESSARY
                MenuItem menuItem;
                if(getStatusIntArrayFromString(selectedOrder.nextStatus).size()<=0){
                    menuItem = menu.findItem(R.id.action_oravan);
                    menuItem.setVisible(false);
                }

                menuItem = menu.findItem(R.id.action_orregre);
                menuItem.setVisible(false);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final OrderCustomer selectedOrder = (OrderCustomer) orderAdapter.getItem(info.position);

        switch (item.getItemId()) {
            //Elementos del menu contextual ordenes
            case R.id.action_ordetails:
                //////////// DETALLES ORDEN
                Intent IntOrders = new Intent(getApplicationContext(),OrdersDetailActivity.class);
                IntOrders.putExtra("order",selectedOrder);
                startActivity(IntOrders);

                Toast.makeText(OrdersActivity.this, "Detalles de orden", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_oredit:
                Intent IntCustomers = new Intent(getApplicationContext(),OrdersAddActivity.class);
                IntCustomers.putExtra("order",selectedOrder);
                startActivity(IntCustomers);
                return true;
            case R.id.action_oravan:
                ///// AVANZAR STATUS ORDEN

                View v = this.getLayoutInflater().inflate(R.layout.dialog_order_status_change, null);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Cerrando ventana")
                        .setCancelable(false)
                        .setView(v)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AlertDialog d = (AlertDialog)dialog;
                                Spinner s = d.findViewById(R.id.spinnerStatus);
                                TextView t = d.findViewById(R.id.changelog);
                                OrderStatus status = (OrderStatus) s.getSelectedItem();

                                int newStatusId = status.getId();
                                String newChangelog = t.getText().toString();

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                String now = dateFormat.format(date);
                                newChangelog = now + ": " + newChangelog;
                                Order order = orderDao.findById(selectedOrder.id);
                                order.setChangeLog(order.getChangeLog() + "\n" + newChangelog);
                                order.setStatusId(newStatusId);
                                orderDao.update(order);
                                updateOrders(orderCustomerDao.getAll());
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();

                // Get Dialog Spinner
                Spinner s = v.findViewById(R.id.spinnerStatus);

                // PARSE string to List<Integer>
                List<Integer> n = getStatusIntArrayFromString(selectedOrder.nextStatus);
                List<OrderStatus> status = orderStatusDao.findNextStatus(n);

                ArrayAdapter statusAdapter = new ArrayAdapter(this, R.layout.text_list, status);
                s.setAdapter(statusAdapter);


                Toast.makeText(OrdersActivity.this, "Avanzar estado", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_orregre:
                Toast.makeText(OrdersActivity.this, "Regresar estado", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private List<Integer> getStatusIntArrayFromString(String nextStatus) {
        String foo = nextStatus;
        String[] split = foo.split(",");
        List<Integer> n = new ArrayList<Integer>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            try{
                Integer j = Integer.parseInt(split[i]);
                n.add(j);
            }catch (Exception e){}
        }
        return n;
    }

    private void onSearchMenuClicked() {

        // get init date
        Date initDate = null;
        if(dateStartCheckBox.isChecked()){
            ;
            //dateStartEditTxt
        }


        List<Order> o = new ArrayList<Order>();

        String ultimate_query = "SELECT \n" +
                "id,\n" +
                "customerId AS customerId,\n " +
                "statusI" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "" +
                "d AS statusId,\n" +
                "customerName,\n" +
                "status,\n" +
                "date,\n" +
                "assemblies_qty as assemblies,\n" +
                "SUM(subtotal) as price\n" +
                "FROM\n" +
                "(\n" +
                "SELECT \n" +
                "orders.id AS id,\n" +
                "order_status.id AS statusId,\n"+
                "customers.id AS customerId,\n" +
                "customers.first_name as customerName,\n" +
                "orders.date as date,\n" +
                "order_status.description as status,\n" +
                "order_assemblies.assembly_id as assembly_id,\n" +
                "SUM(order_assemblies.qty) as assemblies_qty,\n" +
                "assembly_products.qty as assembly_products_qty,\n" +
                "products.id as product_id,\n" +
                "products.description as product,\n" +
                "products.price as product_price,\n" +
                "SUM(assembly_products.qty*products.price*order_assemblies.qty) as subtotal\n" +
                "FROM orders \n" +
                "INNER JOIN customers ON customers.id = orders.customer_id\n" +
                "INNER JOIN order_status ON order_status.id = orders.status_id\n" +
                "INNER JOIN order_assemblies ON order_assemblies.id = orders.id\n" +
                "INNER JOIN assembly_products ON assembly_products.id = order_assemblies.assembly_id\n" +
                "INNER JOIN products ON products.id = assembly_products.product_id\n" +
                "GROUP BY orders.id, order_assemblies.assembly_id, products.id\n" +
                ")";

        String query = "SELECT * FROM orders";
        String statusQuery = statusStringQueryMaker();
        String customerQuery = customerAndStatusQueryMaker(custId, statusQuery,
                dateStartEditTxt.getText().toString(), dateEndEditTxt.getText().toString() );
        ultimate_query = ultimate_query + customerQuery +  " GROUP BY id";


        ArrayList<String> starray = new ArrayList<String>();

        //Object[] tmp = starray.toArray();
        String dateStartStr = dateStartEditTxt.getText().toString();
        String dateEndStr = dateEndEditTxt.getText().toString();
        ArrayList<Object> arreglos = new ArrayList<Object>();

        if (!dateStartStr.isEmpty()){
            arreglos.add(dateStartStr);
        }
        if (!dateEndStr.isEmpty()){
            arreglos.add(dateEndStr);
        }

        SimpleSQLiteQuery q = new SimpleSQLiteQuery(query, arreglos.toArray());
        //List<Order> ord = orderDao.findByQuery(q);

        SimpleSQLiteQuery ultimate_q = new SimpleSQLiteQuery(ultimate_query, arreglos.toArray());
        List<OrderCustomer> ord = orderCustomerDao.findByQuery(ultimate_q);
        updateOrders(ord);


    }


    private String customerAndStatusQueryMaker(int customerId, String statusString, String startDate, String endDate) {
        boolean unaClausula = false;

        String query = "";
        if (customerId == -1) { //ALL CUSTOMERS SELECTED
            //FIND JUST BY STATUS
            if (statusString.isEmpty()) { //NO STATUS SELECTED
                //DO NOTHING
            } else { //STATUS SELECTED
                query = statusString;
            }
        } else {

            if (statusString.isEmpty()) {//FIND BY CUSTOMER

                query = " customerId = " + customerId;

            } else {//FIND BY CUSTOMER AND STATUS

                query = " customerId like " + customerId + statusString;

            }
        }

        if(!startDate.isEmpty() && !endDate.isEmpty()){
            if(!query.isEmpty()) query += " AND";
            query += " date BETWEEN date(?) AND date(?)";
        } else if(!startDate.isEmpty()){
            if(!query.isEmpty()) query += " AND";
            query += " date(date) > date(?)";
        } else if(!endDate.isEmpty()){
            if(!query.isEmpty()) query += " AND";
            query += " date(date) < date(?)";
        }

        if(!query.isEmpty()){
            query = " WHERE" + query;
        }

        return query;
    }

    private String statusStringQueryMaker() {

        String selectedStatus = "";

        boolean previousConditions = false;

        for (StateVO i : listVOs) {
            if (i.isSelected()) {
                if (previousConditions == false) {
                    if (i.getTitle().equals("Pendiente")) {
                        selectedStatus = " statusId like 0";
                    }
                    if (i.getTitle().equals("Cancelado")) {
                        selectedStatus = " statusId like 1";
                    }
                    if (i.getTitle().equals("Confirmado")) {
                        selectedStatus = " statusId like 2";
                    }
                    if (i.getTitle().equals("En transito")) {
                        selectedStatus = " statusId like 3";
                    }
                    if (i.getTitle().equals("Finalizado")) {
                        selectedStatus = " statusId like 4";
                    }
                    previousConditions = true;
                } else {
                    if (i.getTitle().equals("Pendiente")) {
                        selectedStatus = selectedStatus + " OR statusId like 0";
                    }
                    if (i.getTitle().equals("Cancelado")) {
                        selectedStatus = selectedStatus + " OR statusId like 1";
                    }
                    if (i.getTitle().equals("Confirmado")) {
                        selectedStatus = selectedStatus + " OR statusId like 2";
                    }
                    if (i.getTitle().equals("En transito")) {
                        selectedStatus = selectedStatus + " OR statusId like 3";
                    }
                    if (i.getTitle().equals("Finalizado")) {
                        selectedStatus = selectedStatus + " OR statusId like 4";
                    }
                }
            }
        }

        if (selectedStatus != "") {
            if (custId == -1) {
                selectedStatus = " " + selectedStatus;
            } else {
                selectedStatus = " AND ("+ selectedStatus + ")";
            }

        }

        return selectedStatus;
    }


    private void updateOrders(List<OrderCustomer> newOrder) {

        //ADD ORDERS TO MODEL
        this.orders.clear();
        this.orders.addAll(newOrder);

        // notify adapter to update view
        orderAdapter.notifyDataSetChanged();

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
}


