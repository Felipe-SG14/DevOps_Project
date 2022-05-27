package com.example.proyect_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MamaActivity extends AppCompatActivity {

    // Configuración Botones

    public Switch switch1;
    public Switch switch2;
    public Switch switch3;
    public Switch switch4;
    public Switch switch5;
    public Switch switch6;
    public Switch switchHija;    // Switch 7 es para la hija
    public Switch switchHijo;    // Switch 8 es para el hijo
    public SeekBar sk_hija;
    public SeekBar sk_hijo;



    // Variables para asignar roles
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mama);

        // Configuración switch y seekbar
        switch1 = (Switch)findViewById(R.id.switch1);
        switch2 = (Switch)findViewById(R.id.switch2);
        switch3 = (Switch)findViewById(R.id.switch3);
        switch4 = (Switch)findViewById(R.id.switch4);
        switch5 = (Switch)findViewById(R.id.switch5);
        switch6 = (Switch)findViewById(R.id.switch6);
        switchHija = (Switch)findViewById(R.id.switch7);
        switchHijo = (Switch)findViewById(R.id.switch8);

        sk_hija = (SeekBar)findViewById(R.id.seekBar1);
        sk_hijo = (SeekBar)findViewById(R.id.seekBar2);

        // Permisos de acceso a botones
        rol = getIntent().getStringExtra("dato");
        PermisosRol(rol);

        //Listeners para cada switch
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switchHija.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

        switchHijo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b);
            }
        });

    }


    //switchAccion(boolean b) ---> Adentro estan las acciones de Verdadero o Falso del Switch
    private void switchAccion(boolean b){
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

    public void dataUsingVolley(JSONObject jsonObject, String url, String message) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Toast response_toast = Toast.makeText(getApplicationContext(), "e " + response.toString(), Toast.LENGTH_LONG);
                    response_toast.show();
                }, error -> {
            Toast response_toast = Toast.makeText(MamaActivity.this, message, Toast.LENGTH_LONG);
            response_toast.show();
        });

        requestQueue.add(jsonObjectRequest);
    }


    /////////  Función para dar permisos de acceso a los botones //////////////////////////////////////////////////////////////
    // Método para regresar a la ventana principal
    public void Regresar(View view)
    {
        Intent regresarMain = new Intent(this, MainActivity.class);
        startActivity(regresarMain);
    }

    public void PermisosRol(String input)
    {
        if(input.equals("mama"))
        {
            switch1.setVisibility(View.VISIBLE);
            switch2.setVisibility(View.VISIBLE);
            switch3.setVisibility(View.VISIBLE);
            switch4.setVisibility(View.VISIBLE);
            switch5.setVisibility(View.VISIBLE);
            switch6.setVisibility(View.VISIBLE);
            switchHija.setVisibility(View.VISIBLE);
            switchHijo.setVisibility(View.VISIBLE);
            sk_hija.setVisibility(View.VISIBLE);
            sk_hijo.setVisibility(View.VISIBLE);
        }
        if(input.equals("hijo"))
        {
            switch1.setVisibility(View.VISIBLE);
            switch2.setVisibility(View.VISIBLE);
            switch3.setVisibility(View.VISIBLE);
            switch4.setVisibility(View.VISIBLE);
            switch5.setVisibility(View.VISIBLE);
            switch6.setVisibility(View.VISIBLE);
            switchHija.setVisibility(View.INVISIBLE);
            switchHijo.setVisibility(View.VISIBLE);
            sk_hija.setVisibility(View.INVISIBLE);
            sk_hijo.setVisibility(View.VISIBLE);
        }
        if(input.equals("hija"))
        {
            switch1.setVisibility(View.VISIBLE);
            switch2.setVisibility(View.VISIBLE);
            switch3.setVisibility(View.VISIBLE);
            switch4.setVisibility(View.VISIBLE);
            switch5.setVisibility(View.VISIBLE);
            switch6.setVisibility(View.VISIBLE);
            switchHija.setVisibility(View.VISIBLE);
            switchHijo.setVisibility(View.INVISIBLE);
            sk_hija.setVisibility(View.VISIBLE);
            sk_hijo.setVisibility(View.INVISIBLE);
        }
    }
}


