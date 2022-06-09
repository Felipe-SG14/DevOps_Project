package com.example.proyect_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class MenuActivity extends AppCompatActivity {

    // Configuración botones
    public Button luces;
    public Button musica;
    public Button ubicacionPadre;
    public Button stopEmergency;

    // Para calcular las distancias
    // Ubicación origen es la ubicación del celular/hijo/usuario
    private double latitudOrigen;
    private double longitudOrigen;

    // Ubicación destino es la ubicación de la casa/destino
    private double latitudDestino;
    private double longitudDestino;

    private boolean DistanciaMenor5m;

    // Constantes para calcular la distancia a la casa
    // Radio Ecuatorial
    public static final float RadioTierraKm = 6378;

    // Variables para asignar roles
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Configuración botones
        luces = (Button)findViewById(R.id.luces);
        musica=(Button)findViewById(R.id.musica);
        ubicacionPadre=(Button)findViewById(R.id.ubicacionPadre);
        stopEmergency=(Button)findViewById(R.id.stopEmergency);

        // Permisos de acceso a botones
        rol = getIntent().getStringExtra("dato");
        PermisosRol(rol);

        if (rol.equals("hijo"))
        {
            DistanciaAcasa();
        }


    }

    // Función que calcula la distancia de la casa a la ubicación del usuario (hijo)
    public void DistanciaAcasa()
    {
        // Ubicación Destino (en Km)
        // CASA DEL USUARIO DE RASPBERRY
        // ACTUALMENTE UBICACIÓN DE LA FACULTAD DE INGENIERÍA
        latitudDestino = 19.33144;
        longitudDestino = -99.18408;

        // Ubicación Origen (en Km)
        // CELULAR DEL USUARIO
        // ACTUALMENTE UBICACIÓN DE LAS BICIS CERCA DE LA FACULTAD DE INGENIERÍA
        latitudOrigen = 19.33211;
        longitudOrigen = -99.18438;

        // Diferencia de latitudes y longitudes (En radianes)
        double DifLatitud = (latitudDestino-latitudOrigen)*(Math.PI/180);
        double DifLongitud = (longitudDestino-longitudOrigen)*(Math.PI/180);

        //a = sin²(Δlat/2) + cos(lat1) · cos(lat2) · sin²(Δlong/2)
        double a = Math.pow(Math.sin(DifLatitud/2),2) + (Math.cos(latitudOrigen))*(Math.cos(latitudDestino))*Math.pow(Math.sin(DifLongitud/2),2);

        //c = 2 · atan2(√a, √(1−a))
        double c = 2*(Math.atan2(Math.sqrt(a),Math.sqrt(1-a)));

        // d = R · c
        // En metros
        double distancia = (RadioTierraKm*c)*1000;
        String mensajeD = String.valueOf(distancia);
        Toast.makeText(this, mensajeD, Toast.LENGTH_SHORT).show();
    }

    /////////  Código de GPS y permisos //////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //We have a location
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.d(TAG, "onSuccess " + location.toString());
                    Log.d(TAG, "onSuccess " + latitude);
                    Log.d(TAG, "onSuccess " + longitude);

                } else {
                    Log.d(TAG, "onSuccess: Location was null...");
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFaiure: " + e.getLocalizedMessage());
            }
        });

    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                getLastLocation();
            } else {
                //Permission not granted
            }
        }
    }

    // Función que cambia de la ventana de menú de opciones a la ventana de luces
    // Para la madre, hijo e hija
    public void abrirMenuLuces(View view) {
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
            stopEmergency.setVisibility(View.VISIBLE);

        }
        if(input.equals("hijo"))
        {
            luces.setVisibility(View.VISIBLE);
            musica.setVisibility(View.VISIBLE);
            ubicacionPadre.setVisibility(View.INVISIBLE);
            stopEmergency.setVisibility(View.VISIBLE);
        }
        if(input.equals("hija"))
        {
            luces.setVisibility(View.VISIBLE);
            musica.setVisibility(View.INVISIBLE);
            ubicacionPadre.setVisibility(View.INVISIBLE);
            stopEmergency.setVisibility(View.VISIBLE);
        }
        if(input.equals("papa"))
        {
            luces.setVisibility(View.INVISIBLE);
            musica.setVisibility(View.INVISIBLE);
            ubicacionPadre.setVisibility(View.VISIBLE);
            stopEmergency.setVisibility(View.VISIBLE);
        }
    }

    // Listener para el control de la música, para parar la música
    public void controlMusica(View view) {

    }


}