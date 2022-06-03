package com.example.proyect_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    // Configuración botones
    public Button luces;
    public Button musica;
    public Button ubicacionPadre;
    public Button stopEmergency;

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