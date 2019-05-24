package com.example.sales_partner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sales_partner.dao.UserDao;
import com.example.sales_partner.db.AppDatabase;
import com.example.sales_partner.model.User;
import com.facebook.stetho.Stetho;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button SignUpBtn;
    Button LoginBtn;

    EditText Username;
    EditText Password;

    private RequestQueue requestQueue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue= Volley.newRequestQueue(this);


        SignUpBtn = findViewById(R.id.sign);
        LoginBtn = findViewById(R.id.login);

        Username = findViewById(R.id.Usuario);
        Password = findViewById(R.id.Contraseña);


        Stetho.initializeWithDefaults(this);

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Usuario = Username.getText().toString();
                String Passwo = Password.getText().toString();


                jsonParse(Usuario);

                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                UserDao UsDao = db.userDao();
                User usuario = UsDao.GetAll();


                if (usuario == null){
                    Toast t=Toast.makeText(getApplicationContext(),"Usuario no encontrado", Toast.LENGTH_SHORT);
                    t.show();
                }else {
                    String key = usuario.getPassword();
                    if (key.equals(Passwo)){
                        Toast t=Toast.makeText(getApplicationContext(),"Bienvenido " + usuario.getName(), Toast.LENGTH_SHORT);
                        t.show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Toast t=Toast.makeText(getApplicationContext(),"Contraseña incorrecta", Toast.LENGTH_SHORT);
                        t.show();
                    }
                }

            }
        });

    }

    private void jsonParse(String user){

        String url = "http://148.209.151.63:3000/api/users/" + user;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);

                    User user1 = new User(0,"","","");
                    if(!jsonObject.getString("user").equals("")){
                    String Usuario = jsonObject.getString("user");
                    String Password = jsonObject.getString("password");
                    String Name = jsonObject.getString("name");
                    user1.setName(Name);
                    user1.setPassword(Password);
                    user1.setUser(Usuario);}

                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    UserDao UsDao = db.userDao();
                    UsDao.nukeTable();
                    UsDao.InsertarUsuario(user1);

                } catch (JSONException e) {
                    AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                    UserDao UsDao = db.userDao();
                    UsDao.nukeTable();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                UserDao UsDao = db.userDao();
                UsDao.nukeTable();
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

}

