package com.example.proyect_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public EditText email;
    public EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.editTextTextPersonName);
        password = (EditText) findViewById(R.id.editTextTextPassword);
    }

    public void loginButton (View view) throws JSONException {
        datosusandoVolley();
    }

    private void datosusandoVolley() throws JSONException {
        String url = "https://davinci999.xyz/log.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CORREO",email.getText());
        jsonObject.put("PASSWORD",password.getText());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("ID");
                            if(id!=0)
                            {
                                openUserActivity(id);
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Vuelva a intertarlo",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast response_toast = Toast.makeText(getApplicationContext(), "e " + error.toString(), Toast.LENGTH_LONG);
                response_toast.show();
            }
        }

        );
        requestQueue.add(jsonObjectRequest);
    }

    public void openUserActivity(int id){
        switch (id){
            case 1:
                String rolHijo = "hijo";
                Intent i4 = new Intent(this, MenuActivity.class);
                i4.putExtra("dato",rolHijo);
                startActivity(i4);
                Toast.makeText(LoginActivity.this,"Hola Gabriel",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String rolHija = "hija";
                Intent i3 = new Intent(this, MenuActivity.class);
                i3.putExtra("dato",rolHija);
                startActivity(i3);
                Toast.makeText(LoginActivity.this,"Hola Diana",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                String rolPapa = "papa";
                Intent i2 = new Intent(this, MenuActivity.class);
                i2.putExtra("dato", rolPapa);
                startActivity(i2);
                Toast.makeText(LoginActivity.this,"Hola Juan",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                String rolMama = "mama";
                Intent i1 = new Intent(this, MenuActivity.class);
                i1.putExtra("dato",rolMama);
                startActivity(i1);
                Toast.makeText(LoginActivity.this,"Hola Carmen",Toast.LENGTH_SHORT).show();
                break;


        }
    }
}