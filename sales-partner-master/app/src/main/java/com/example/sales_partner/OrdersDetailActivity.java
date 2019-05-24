package com.example.sales_partner;

import android.app.DatePickerDialog;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.example.sales_partner.dao.AssemblyDao;
import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.dao.OrderAssembliesDao;
import com.example.sales_partner.dao.OrderCustomerDao;
import com.example.sales_partner.dao.OrderDao;
import com.example.sales_partner.dao.OrderStatusDao;
import com.example.sales_partner.databinding.ActivityOrdersDetailBinding;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.Assembly;
import com.example.sales_partner.model.Customer;
import com.example.sales_partner.model.Order;
import com.example.sales_partner.model.OrderCustomer;
import com.example.sales_partner.model.OrderExtended;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class OrdersDetailActivity extends AppCompatActivity {
    public class Model extends BaseObservable {
        private String customerName;
        private String status;
        private String date;
        private String changelog;
        private List<OrderExtended> assemblies;

        @Bindable
        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        @Bindable
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Bindable
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Bindable
        public String getChangelog() {
            return changelog;
        }

        public void setChangelog(String changelog) {
            this.changelog = changelog;
        }

        public List<OrderExtended> getAssemblies() {
            return assemblies;
        }

        public void setAssemblies(List<OrderExtended> assemblies) {
            this.assemblies = assemblies;
        }
    }

    //TAG
    private static final String TAG = "OrdersDetailActivity";

    // DATA OBJECTS
    private OrderAssembliesDao orderAssemblyDao;
    private AssemblyDao assembliesDao;

    // VIEW COMPONENTS
    private Spinner statusSpnr;

    // View Models
    private Model viewModel = new Model();
    //private String customerName = "";
    private List<OrderCustomer> orders;

    // Properties
    private OrderCustomer order;

    // Adapters
    private ArrayAdapter orderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityOrdersDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_orders_detail);
        binding.setModel(this.viewModel);

        // LOAD DATABASE DAO
        orderAssemblyDao = AppDatabase.getAppDatabase(getApplicationContext()).orderAssembliesDao();
        assembliesDao = AppDatabase.getAppDatabase(getApplicationContext()).assemblyDao();

        // get selected order or return
        order = (OrderCustomer) getIntent().getSerializableExtra("order");
        if(order==null) return;

        viewModel.setCustomerName( order.customerName );
        viewModel.setStatus( order.status );
        viewModel.setDate(order.date);
        viewModel.setChangelog(order.changelog);

        // GET ASSEMBLIES FROM THIS ORDER
        viewModel.setAssemblies(assembliesDao.findByOrderId(order.id));

        // get List View
        ListView assembliesList = findViewById(R.id.assemblyList);
        ArrayAdapter assembliesAdapter = new ArrayAdapter(this, R.layout.text_list, viewModel.getAssemblies());
        assembliesList.setAdapter(assembliesAdapter);
    }



    private void updateOrders(List<OrderCustomer> newOrder) {

        //ADD ORDERS TO MODEL
        this.orders.clear();
        this.orders.addAll(newOrder);

        // notify adapter to update view
        orderAdapter.notifyDataSetChanged();

    }
}


