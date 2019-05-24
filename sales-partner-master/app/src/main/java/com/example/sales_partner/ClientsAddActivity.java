package com.example.sales_partner;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sales_partner.dao.CustomerDao;
import com.example.sales_partner.databinding.ActivityClientsAddBinding;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.Customer;

import java.util.List;

public class ClientsAddActivity extends AppCompatActivity {
    private boolean validationOn = false;

    //LOG
    private static final String TAG = "ClientsActivity";

    // DATA OBJECTS
    private CustomerDao customerDao;

    // Models
    private Customer customer;

    private boolean phone2Checked = false;
    private boolean phone3Checked = false;
    private boolean emailChecked = false;


    //////////////////////
    // GETTERS AND SETTERS
    /////////////////////

    public boolean isPhone2Checked() {
        return phone2Checked;
    }

    public void setPhone2Checked(boolean phone2Checked) {
        this.phone2Checked = phone2Checked;
    }

    public boolean isPhone3Checked() { return phone3Checked; }

    public void setPhone3Checked(boolean phone3Checked) { this.phone3Checked = phone3Checked; }


    public boolean isEmailChecked() { return emailChecked; }

    public void setEmailChecked(boolean emailChecked) { this.emailChecked = emailChecked; }


    // VIEW COMPONENTS
    EditText txtEditFirstName;
    EditText txtEditLastName;
    EditText txtEditPhone1;
    EditText txtEditPhone2;
    EditText txtEditPhone3;
    EditText tatEditAddress;
    EditText txtEditEmail;

//    Button btnCustomerSave;

    CheckBox checkBoxPhone2;
    CheckBox checkBoxPhone3;
    CheckBox checkBoxEmail;

    private boolean saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_clients_add);

        ActivityClientsAddBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_clients_add);

        customer = (Customer)getIntent().getSerializableExtra("customer");
        if(customer==null) customer = new Customer();

        binding.setCustomer(customer);
        binding.setPhone2Checked(phone2Checked);

        customerDao = AppDatabase.getAppDatabase(getApplicationContext()).customerDao();

        Log.d(TAG, "onCreate: ");

        saved = false;


        // VIEW COMPONENTS INIT
        txtEditFirstName = findViewById(R.id.txtEditFirstName);
        txtEditLastName = findViewById(R.id.txtEditLastName);
        txtEditPhone1 = findViewById(R.id.txtEditPhone1);
        txtEditPhone2 = findViewById(R.id.txtEditPhone2);
        txtEditPhone3 = findViewById(R.id.txtEditPhone3);
        tatEditAddress = findViewById(R.id.txtEditAdress);
        txtEditEmail = findViewById(R.id.txtEditEmail);

//        btnCustomerSave = findViewById(R.id.btnCustomerSave);

        checkBoxPhone2 = findViewById(R.id.checkboxPhone2);
        checkBoxPhone3 = findViewById(R.id.checkboxPhone3);
        checkBoxEmail = findViewById(R.id.checkboxEmail);

    }
    //GENERATE TOOLBAR MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // SEARCH BUTTON ACTION
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu_item:
                /// SEARCH

                boolean enableSave;
                enableSave = true;

                //SAVING DATA
                String firstName = txtEditFirstName.getText().toString();
                String lastName = txtEditLastName.getText().toString();
                String phone1 = txtEditPhone1.getText().toString();
                String phone2 = txtEditPhone2.getText().toString();
                String phone3 = txtEditPhone3.getText().toString();
                String address = tatEditAddress.getText().toString();
                String email = txtEditEmail.getText().toString();

                String dialogString = "";

                if(validationOn){
                    if (firstName.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Nombre";
                    }
                    if (lastName.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Apellido";
                    }
                    if (phone1.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Telefono";
                    }
                    if (address.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Direccion";
                    }

                    if (checkBoxEmail.isChecked() && email.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Email";
                    }
                    if (checkBoxPhone2.isChecked() && phone2.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Telefono 2";
                    }
                    if (checkBoxPhone3.isChecked() && phone3.isEmpty()){
                        //MISSING DATA
                        enableSave = false;
                        dialogString += " Telefono 3";
                    }
                } else { enableSave = true; }



                if (enableSave){

                    //All data
                    customer.setFirstName(firstName);
                    customer.setLastName(lastName);
                    customer.setPhone1(phone1);
                    customer.setPhone2(phone2);
                    customer.setPhone3(phone3);
                    customer.setAddress(address);
                    customer.setEmail(email);
                    //customerDao.insertAll(customer);

                    if(customer.getId() > 0)
                        customerDao.update(customer);
                    else
                        customerDao.insertAll(customer);

                    Toast.makeText(getApplicationContext(), "Datos guardados", Toast.LENGTH_SHORT).show();
                    Intent IntCustomers = new Intent(getApplicationContext(),ClientsActivity.class);
                    IntCustomers.putExtra("tag","start");
                    startActivity(IntCustomers);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(), "Falto llenar" + dialogString, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //CONFIRM DELETE UNSAVED DATA
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");

        if (!saved) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cerrando ventana")
                    .setCancelable(false)
                    .setMessage("¿Está seguro que desea salir sin guardar")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent IntCustomers = new Intent(getApplicationContext(),ClientsActivity.class);
                            IntCustomers.putExtra("tag","start");
                            startActivity(IntCustomers);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}