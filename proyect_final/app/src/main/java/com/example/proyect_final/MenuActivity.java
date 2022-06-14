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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
              //  Toast.makeText(this,"Fuera de rango",Toast.LENGTH_SHORT).show();
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
    public double DistanciaAcasa( double latitudOrigen, double longitudOrigen, double latitudDestino, double longitudDestino) {
        // Diferencia de latitudes y longitudes (En radianes)
        double DifLatitud = (latitudDestino-latitudOrigen)*(Math.PI/180);
        double DifLongitud = (longitudDestino-longitudOrigen)*(Math.PI/180);

        //a = sin²(Δlat/2) + cos(lat1) · cos(lat2) · sin²(Δlong/2)
        double a = Math.pow(Math.sin(DifLatitud/2),2) + (Math.cos(latitudOrigen))*(Math.cos(latitudDestino))*Math.pow(Math.sin(DifLongitud/2),2);

        //c = 2 · atan2(√a, √(1−a))
        double c = 2*(Math.atan2(Math.sqrt(a),Math.sqrt(1-a)));

        // d = R · c
        double distancia = (RadioTierraKm*c)*1000;      // Distancia en metros



        return distancia;

    }

    /////////  Para actualización de la ubicación
    private void miUbicacion(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ActualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locListener);
    }

    private void ActualizarUbicacion(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if(rol.equals("hijo"))
            {
                // Destino RASP
                double d = DistanciaAcasa(latitude,longitude,37.4219958,-122.0840005);
                if(d <= 5)
                {
                    detener_musica = true;
                    iniciarMusica(DistanciaMenor5m);
                }else
                {
                    DistanciaMenor5m = false;

                }
                if(d <= 10 && !Detener_actualizacion)
                {
                    Detener_actualizacion=true;
                    encenderFoco(true,8);

                }


            }
            if(rol.equals("hija"))
            {
                // Destino ESP
                double d= DistanciaAcasa(latitude,longitude,37.4219958,-122.0840005);
                if(d <= 10 && !Detener_actualizacion)
                {
                    Detener_actualizacion = true;

                    encenderFoco(true,4);

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

