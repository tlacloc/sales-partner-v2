package com.example.sales_partner.reports;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sales_partner.AssemblyListAdapter;
import com.example.sales_partner.OrderRowAdapter;
import com.example.sales_partner.R;
import com.example.sales_partner.dao.AssemblyProductsDao;
import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.dao.OrderDao;
import com.example.sales_partner.dao.ProductDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.Assembly;
import com.example.sales_partner.model.Customer;
import com.example.sales_partner.model.Order;
import com.example.sales_partner.model.Product;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SimCustomerActivity extends AppCompatActivity {
    //TAG
    private static final String TAG = "SimCustomerActivity";
    String screenName;

    // DATA OBJECTS
    private OrderDao orderDao;
    private OrderAssembliesDao orderAssembliesDao;
    private AssemblyProductsDao assemblyProductsDao;
    private CustomerDao customerDao;
    private ProductDao productDao;

    // DATA
    private List<Product> products;
    private List<Order> orders;
    private List<Customer> customers;

    // GUI COMPONENTS
    Spinner customerSpinner;
    ListView ordersListView;
    Button processBtn;
    LinearLayout dateSelectorLayout;
    EditText dateStartEditTxt;
    EditText dateEndEditTxt;
    Button searchByDateBtn;
    LinearLayout priceSelectorLayout;
    Button searchByPriceBtn;
    EditText lowPriceTxt;
    EditText hiPriceTxt;

    // Adapters
    ArrayAdapter ordersAdapter;
    ArrayAdapter customersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_customer);

        screenName = getIntent().getStringExtra("tag");


        /// INIT DAOS
        customerDao = AppDatabase.getAppDatabase(getApplicationContext()).customerDao();
        orderDao = AppDatabase.getAppDatabase(getApplicationContext()).orderDao();
        orderAssembliesDao = AppDatabase.getAppDatabase(getApplicationContext()).orderAssembliesDao();
        assemblyProductsDao = AppDatabase.getAppDatabase(getApplicationContext()).assemblyProductsDao();
        productDao = AppDatabase.getAppDatabase(getApplicationContext()).productDao();

        // GET DATA
        customers = customerDao.getAll();
        products = productDao.getAll(); // Get Products
        orders = new ArrayList<Order>();
        //orders = orderDao.findByCustomerAndStatus(4,0); // Get Orders
        //Order o = orders.get(0);

        // Define Adapters
        customersAdapter = new ArrayAdapter(this, R.layout.text_list, customers);
        ordersAdapter = new OrderRowAdapter(this, R.layout.text_list, orders);

        // GET UI COMPONENTS
        customerSpinner = findViewById(R.id.sprCustomerSim);
        ordersListView = findViewById(R.id.lvOrdersSim);
        processBtn = findViewById(R.id.btnProcessSim);
        dateSelectorLayout = findViewById(R.id.dateSelectorLayout);
        priceSelectorLayout = findViewById(R.id.priceSelectorLayout);
        dateStartEditTxt = findViewById(R.id.editTextOrdersStartDateSim);
        dateEndEditTxt = findViewById(R.id.editTextOrdersEndDateSim);
        lowPriceTxt = findViewById(R.id.txtEditStartPrice);
        hiPriceTxt = findViewById(R.id.txtEditEndPrice);

        showComponentsByScreen();


        // Set Adapters
        customerSpinner.setAdapter(customersAdapter);
        ordersListView.setAdapter(ordersAdapter);

        // Set Listeners
        customerSpinner.setOnItemSelectedListener(spinnerListener);
        processBtn.setOnClickListener(processBtnClick);

    }

    private void showComponentsByScreen() {
        switch (screenName){
            case "BY_DATE":
                dateSelectorLayout.setVisibility(View.VISIBLE);
                searchByDateBtn = findViewById(R.id.search);
                searchByDateBtn.setOnClickListener(searchByDateBtnClick);
                break;
            case "BY_CUSTOMER":
                customerSpinner.setVisibility(View.VISIBLE);
                break;
            case "BY_PRICE":
                searchByPriceBtn = findViewById(R.id.search_price);
                priceSelectorLayout.setVisibility(View.VISIBLE);
                searchByPriceBtn.setOnClickListener(searchByPriceBtnClick);
                break;

        }
    }

    public void processOrder(Order order, OrderAssembliesDao orderAssembliesDao){
        int possible = 0 ;
        int impossible = 0;
        int count = 0;

        order.retrieveAssemblies(orderAssembliesDao);

        for (Assembly assembly : order.assemblies) {
            assembly.retrieveProducts(assemblyProductsDao);
            assembly.retrieveProductQuantity(assemblyProductsDao);
            assembly.retrieveQuantity(orderAssembliesDao, order.getId());

            int result = 0;

            int initQuantity = assembly.quantity;

            while(assembly.quantity > 0){
                 result = order.canDo(products,assembly.products, assembly.quantity);

                if (result == Assembly.STATUS_CAN_DO) {
                    System.out.println("caaaa doooo");
                    // Do it, get product from stock
                    order.doIt(products, assembly.products, assembly.quantity);
                } else {
                    break;
                }
                assembly.quantity--;
            }
            if(assembly.quantity==0)
                assembly.stockStatus = Assembly.STATUS_CAN_DO;
            else if(initQuantity > assembly.quantity)
                assembly.stockStatus = Assembly.STATUS_OUT_OF_STOCK;
            else if(initQuantity==assembly.quantity)
                assembly.stockStatus = Assembly.STATUS_CANT_DO;

        }

        // Iterate over assemblies and set order Status
        for (Assembly assembly : order.assemblies) {
            int status = Order.STATUS_CAN_DO;

            if(assembly.stockStatus==Assembly.STATUS_CAN_DO)
                count =+ 2;
            if(assembly.stockStatus==Assembly.STATUS_OUT_OF_STOCK)
                count++;
        }
        if (count == 2*order.assemblies.size()){
            order.stockStatus = Order.STATUS_CAN_DO;
        } else if (count == 0){
            order.stockStatus = Order.STATUS_CANT_DO;
        } else order.stockStatus = Order.STATUS_OUT_OF_STOCK;


        System.out.println(order);
    }

    public void processOrders(List<Order> orders){
        for (Order order : orders) {
            processOrder(order, orderAssembliesDao);
        }
    }

    public Boolean getProductFromStock(List<Product> products, int productId, int quantity){
        for (Product product : products) {
            if(product.getId() == productId) {
                if(!product.getProductOut(quantity)){
                    return false;
                }
            }
        }
        return true;
    }

    ///// LISTENERS
    public View.OnClickListener processBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            processOrders(orders);

            // Show message
            Toast.makeText(getApplicationContext(), "Se Procesaron las Órdenes", Toast.LENGTH_SHORT).show();

            ordersAdapter.notifyDataSetChanged();
        }
    };

    public View.OnClickListener searchByDateBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String startDate = dateStartEditTxt.getText().toString();
            String endDate = dateEndEditTxt.getText().toString();
            List<Order> o = orderDao.findByDatesAndStatus(startDate,endDate,0); // Get Orders
            orders.clear();
            orders.addAll(o);
            ordersAdapter.notifyDataSetChanged();
        }
    };

    public View.OnClickListener searchByPriceBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String lowPrice = lowPriceTxt.getText().toString();
            String hiPrice = hiPriceTxt.getText().toString();
            List<Order> o = orderDao.findByPriceAndStatus(Integer.parseInt(lowPrice), Integer.parseInt(hiPrice),0); // Get Orders
            orders.clear();
            orders.addAll(o);
            ordersAdapter.notifyDataSetChanged();
        }
    };

    public AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Customer selectedCustomer = (Customer) customerSpinner.getSelectedItem();

            List<Order> o = orderDao.findByCustomerAndStatus(selectedCustomer.getId(),0); // Get Orders
            orders.clear();
            orders.addAll(o);
            ordersAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    //////  BASURA DEL CALENDARIO


    private static final String CERO = "0";
    private static final String BARRA = "-";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    public void showDatePickerDialog(View v) {
        obtenerFecha();
    }
    public void showStartDatePickerDialog(View v) {
        obtenerFechaInicial();
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

}
