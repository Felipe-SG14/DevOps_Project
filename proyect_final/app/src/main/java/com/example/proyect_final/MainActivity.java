package com.example.proyect_final;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Switch switch1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){

                if(b){
                    String url = "https://spiralweb.000webhostapp.com/solicitud.php"; //http://davinci999.xyz
                    JSONObject jsonObject_foco_on = new JSONObject();
                    try {
                        jsonObject_foco_on.put("dispositivo_id",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject_foco_on.put("Estado",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject_foco_on.put("Intensidad",100);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String message = "Light on";
                    try {
                        dataUsingVolley(jsonObject_foco_on, url, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    JSONObject jsonObject_foco_off = new JSONObject();
                    try {
                        jsonObject_foco_off.put("dispositivo_id",1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject_foco_off.put("Estado",0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject_foco_off.put("Intensidad",0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = "https://spiralweb.000webhostapp.com/solicitud.php"; //http://davinci999.xyz
                    String message = "Light off";
                    try {
                        dataUsingVolley(jsonObject_foco_off, url, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void dataUsingVolley(JSONObject jsonObject, String url, String message) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Toast response_toast = Toast.makeText(getApplicationContext(), "e " + response.toString(), Toast.LENGTH_LONG);
                    response_toast.show();
                }, error -> {
            Toast response_toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
            response_toast.show();
        });

        requestQueue.add(jsonObjectRequest);
    }

}