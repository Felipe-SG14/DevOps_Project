package com.example.proyect_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MenuActivity extends AppCompatActivity {

    // Configuración botones

    public ImageButton musica;
    public ImageButton ubicacionPadre;
    public ImageButton luces;
    public TextView txt_music;
    public TextView txt_location;
    public TextView txt_lights;


    // Variables ara calcular distancia
    ///////// Variables ara calcular distancia
    // Ubicación Origen (USUARIO MÓVIL Ó HIJO Ó HIJA)
    private double latitudOrigen;
    private double longitudOrigen;
    // Ubicación Destino (CASA DEL USUARIO DE RASPBERRY o CASA DEL USUARIO DE ESP)
    private double latitudDestino;
    private double longitudDestino;
    // Auxiliar
    private double latitude;
    private double longitude;
    public boolean DistanciaMenor5m = false;
    public boolean DistanciaMenor10m = false;
    public boolean Detener_actualizacion=false;
    public boolean detener_musica=false;


    // Constantes para calcular la distancia a la casa
    // Radio Ecuatorial
    public static final float RadioTierraKm = 6378;

    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 10001;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    // Variables para asignar roles
    private String rol;

    public float distanciaCasa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Configuración botones
        luces = (ImageButton) findViewById(R.id.luces);
        musica = (ImageButton) findViewById(R.id.musica);
        ubicacionPadre = (ImageButton) findViewById(R.id.ubicacionPadre);


        txt_music = (TextView) findViewById(R.id.txt_music);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_lights = (TextView) findViewById(R.id.txt_lights);



        // Permisos de acceso a botones
        rol = getIntent().getStringExtra("dato");
        PermisosRol(rol);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(rol.equals("hijo"))
        {
           // Toast.makeText(this,String.valueOf(DistanciaMenor10m),Toast.LENGTH_SHORT).show();

            if(!detener_musica)
            {
                miUbicacion();
                //Toast.makeText(this,"MUSIC ON",Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(this,"Fuera de rango",Toast.LENGTH_SHORT).show();
            }
        }else if(rol.equals("hija"))
        {
            if(!Detener_actualizacion)
            {
                miUbicacion();
               // Toast.makeText(this,"Lights ON",Toast.LENGTH_SHORT).show();
            }else
            {
               // Toast.makeText(this,"Fuera de rango",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Función que calcula la distancia de la casa a la ubicación del usuario (hijo)
    public float DistanciaAcasa( double latitudOrigen, double longitudOrigen, double latitudDestino, double longitudDestino) {
        float[] results = new float[1];
        Location.distanceBetween(latitudOrigen, longitudOrigen,latitudDestino
                , longitudDestino,
                results);
        return results[0];
    }

    /////////  Para actualización de la ubicación
    private void miUbicacion(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ActualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,0,locListener);
    }

    public static Double round2Decimals(Double value, int i) {
        return new BigDecimal(value.toString()).setScale(6, RoundingMode.HALF_UP).doubleValue();
    }

    private void ActualizarUbicacion(Location location) {
        if (location != null) {
            latitude = round2Decimals(location.getLatitude(),6);
            longitude = round2Decimals(location.getLongitude(),6);

            Toast.makeText(this,String.valueOf(latitude),Toast.LENGTH_SHORT).show();
            Toast.makeText(this,String.valueOf(longitude),Toast.LENGTH_SHORT).show();

            if(rol.equals("hijo"))
            {
                // Destino RASP
                double d = DistanciaAcasa(latitude,longitude,19.451547, -99.089186);
                Toast.makeText(this,String.valueOf(d),Toast.LENGTH_SHORT).show();
                if(d <= 6 && !detener_musica)
                {
                    detener_musica = true;
                    iniciarMusica(detener_musica);
                    encenderFoco(true,8);
                }else
                {
                    DistanciaMenor5m = false;
                }

                if(d >6)
                {
                    encenderFoco(false,8);
                }

            }
            if(rol.equals("hija"))
            {
                // Destino ESP
                double d= DistanciaAcasa(latitude,longitude,19.45152,-99.08918);
                if(d <=6 && !Detener_actualizacion)
                {
                    Detener_actualizacion = true;
                    encenderFoco(true,4);
                }

                if(d > 6)
                {
                    encenderFoco(false,4);
                }
            }
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            ActualizarUbicacion(location);
        }
    };

    /////////  Función para iniciar Música
    public void iniciarMusica(boolean a) {
        if(a)
        {
            String url = "https://davinci999.xyz/solicitud_musica.php";            // Falta completar URL
            JSONObject jsonObject_musica_on = new JSONObject();
            try
            {
                jsonObject_musica_on.put("USUARIO_ID",1);
                jsonObject_musica_on.put("CLOSEHOME",1);
            }catch(JSONException e)
            {
                e.printStackTrace();
            }
            String message = "Music On";
            try
            {
                dataUsingVolley(jsonObject_musica_on,url,message);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    /////////  Función para encender focos automático
    public void encenderFoco(boolean b, int number_switch) {
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
                    //Toast response_toast = Toast.makeText(getApplicationContext(), "e " + response.toString(), Toast.LENGTH_LONG);
                    //response_toast.show();
                }, error -> {
        });
        requestQueue.add(jsonObjectRequest);
    }

    // Función que cambia de la ventana de menú de opciones a la ventana de luces
    // Para la madre, hijo e hija
    public void abrirMenuLuces(View view) {
        Detener_actualizacion=true;
        String rolFamilia = rol;
        Intent i1 = new Intent(this, MamaActivity.class);
        i1.putExtra("dato",rolFamilia);
        startActivity(i1);
    }

    // Función que cambia de la ventana de menú de opciones a la ventana para mandar ubicación GPS
    // Solo para el Padre
    public void abrirMenuUbicacion(View view) {
        Intent i2 = new Intent(this, PapaActivity.class);
        startActivity(i2);
    }

    public void PermisosRol(String input)
    {
        if(input.equals("mama"))
        {
            luces.setVisibility(View.VISIBLE);
            musica.setVisibility(View.INVISIBLE);
            ubicacionPadre.setVisibility(View.INVISIBLE);

            txt_music.setVisibility(View.INVISIBLE);
            txt_location.setVisibility(View.INVISIBLE);
        }
        if(input.equals("hijo"))
        {
            luces.setVisibility(View.VISIBLE);
            musica.setVisibility(View.VISIBLE);
            ubicacionPadre.setVisibility(View.INVISIBLE);

            txt_location.setVisibility(View.INVISIBLE);
        }
        if(input.equals("hija"))
        {
            luces.setVisibility(View.VISIBLE);
            musica.setVisibility(View.INVISIBLE);
            ubicacionPadre.setVisibility(View.INVISIBLE);

            txt_music.setVisibility(View.INVISIBLE);
            txt_location.setVisibility(View.INVISIBLE);
        }
        if(input.equals("papa"))
        {
            luces.setVisibility(View.INVISIBLE);
            musica.setVisibility(View.INVISIBLE);
            ubicacionPadre.setVisibility(View.VISIBLE);

            txt_music.setVisibility(View.INVISIBLE);
            txt_lights.setVisibility(View.INVISIBLE);
        }
    }

    // Listener para el control de la música, para parar la música
    public void controlMusica(View view) throws JSONException {
            detener_musica=true;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("USUARIO_ID",1);
            jsonObject.put("CLOSEHOME",0);
        dataUsingVolley(jsonObject,"https://davinci999.xyz/solicitud_musica.php","Music Off");
    }
}