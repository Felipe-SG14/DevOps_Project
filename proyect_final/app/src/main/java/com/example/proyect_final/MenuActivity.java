package com.example.proyect_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    // Función que cambia de la ventana de menú de opciones a la ventana de luces
    // Para la madre, hijo e hija
    public void abrirMenuLuces(View view) {
        String rolFamilia = rol;
        Intent i1 = new Intent(this, MamaActivity.class);
        i1.putExtra("dato",rolFamilia);
        startActivity(i1);
    }

    // Función que cambia de la ventana de menú de opciones a la ventana para mandar ubicación GPS
    // Solo para el Padres
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