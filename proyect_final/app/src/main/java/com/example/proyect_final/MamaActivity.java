package com.example.proyect_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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

        sk_hija();
        sk_hijo();

        // Permisos de acceso a botones
        rol = getIntent().getStringExtra("dato");
        PermisosRol(rol);

        //Listeners para cada switch
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,1);
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,2);
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,3);
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,4);
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,5);
            }
        });

        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,6);
            }
        });

        switchHija.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,7);
            }
        });

        switchHijo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAccion(b,8);
            }
        });

    }


    //switchAccion(boolean b) ---> Adentro estan las acciones de Verdadero o Falso del Switch
    private void switchAccion(boolean b, int number_switch){
        if(b){
            String url = "https://davinci999.xyz/solicitud.php"; //http://davinci999.xyz
            JSONObject jsonObject_foco_on = new JSONObject();
            try {
                jsonObject_foco_on.put("dispositivo_id",number_switch);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonObject_foco_on.put("Estado",1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonObject_foco_on.put("Intensidad",255);
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
                jsonObject_foco_off.put("dispositivo_id",number_switch);
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
            String url = "https://davinci999.xyz/solicitud.php"; //http://davinci999.xyz
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

    /* To run Data update
                try {
                    dataUsingVolley_GET_FULL_DATA("https://felipedevopsp2.000webhostapp.com/prueba.php");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
     */

    public void dataUsingVolley_UPDATE_FULL_DATA(String url) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONArray jsonArray = new JSONArray();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, jsonArray,
                response -> {
                    try {
                        updateViews(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> { });
        requestQueue.add(jsonArrayRequest);
    }

    private void updateViews(JSONArray response) throws JSONException {
        String TAG = "TAG";
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonObject = response.getJSONObject(i);
            //Accesing each element of the Array
        }
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

    /////// Funciones de los sliders que controlan la intensidad de los focos de los hijos

    public void sk_hija(){ //Foco de la hija
        sk_hija = (SeekBar)findViewById(R.id.seekBar1);
        sk_hija.setMax(256); //Valor maximo de intensidad
        sk_hija.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int intensity;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                intensity=i;
                String url = "https://davinci999.xyz/solicitud_intensidad.php"; //http://davinci999.xyz
                JSONObject jsonObject_intensidad_girl = new JSONObject();
                try {
                    jsonObject_intensidad_girl.put("dispositivo_id",4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject_intensidad_girl.put("Intensidad",intensity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = "Intensity modified";
                try {
                    dataUsingVolley(jsonObject_intensidad_girl, url, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MamaActivity.this,String.valueOf(intensity),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sk_hijo(){ //Foco del hijo
        sk_hijo = (SeekBar)findViewById(R.id.seekBar2);
        sk_hijo.setMax(256); //Valor maximo de intensidad
        sk_hijo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int intensity;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                intensity=i;
                String url = "https://davinci999.xyz/solicitud_intensidad.php"; //http://davinci999.xyz
                JSONObject jsonObject_intensidad_boy = new JSONObject();
                try {
                    jsonObject_intensidad_boy.put("dispositivo_id",8);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObject_intensidad_boy.put("Intensidad",intensity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = "Intensity modified";
                try {
                    dataUsingVolley(jsonObject_intensidad_boy, url, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MamaActivity.this,String.valueOf(intensity),Toast.LENGTH_SHORT).show();
            }
        });
    }

}


