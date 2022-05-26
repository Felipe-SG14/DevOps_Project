package com.example.proyect_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class MamaActivity extends AppCompatActivity {

    // Configuración Botones

    public Switch sw_1;
    public Switch sw_2;
    public Switch sw_3;
    public Switch sw_4;
    public Switch sw_5;
    public Switch sw_6;
    public Switch sw_hija;    // Switch 7 es para la hija
    public Switch sw_hijo;    // Switch 8 es para el hijo
    public SeekBar sk_hija;
    public SeekBar sk_hijo;



    // Variables para asignar roles
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mama);

        // Configuración switch y seekbar
        sw_1 = (Switch)findViewById(R.id.switch1);
        sw_2 = (Switch)findViewById(R.id.switch2);
        sw_3 = (Switch)findViewById(R.id.switch3);
        sw_4 = (Switch)findViewById(R.id.switch4);
        sw_5 = (Switch)findViewById(R.id.switch5);
        sw_6 = (Switch)findViewById(R.id.switch6);
        sw_hija = (Switch)findViewById(R.id.switch7);
        sw_hijo = (Switch)findViewById(R.id.switch8);

        sk_hija = (SeekBar)findViewById(R.id.seekBar1);
        sk_hijo = (SeekBar)findViewById(R.id.seekBar2);

        // Permisos de acceso a botones
        rol = getIntent().getStringExtra("dato");
        PermisosRol(rol);
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
            sw_1.setVisibility(View.VISIBLE);
            sw_2.setVisibility(View.VISIBLE);
            sw_3.setVisibility(View.VISIBLE);
            sw_4.setVisibility(View.VISIBLE);
            sw_5.setVisibility(View.VISIBLE);
            sw_6.setVisibility(View.VISIBLE);
            sw_hija.setVisibility(View.VISIBLE);
            sw_hijo.setVisibility(View.VISIBLE);
            sk_hija.setVisibility(View.VISIBLE);
            sk_hijo.setVisibility(View.VISIBLE);
        }
        if(input.equals("hijo"))
        {
            sw_1.setVisibility(View.VISIBLE);
            sw_2.setVisibility(View.VISIBLE);
            sw_3.setVisibility(View.VISIBLE);
            sw_4.setVisibility(View.VISIBLE);
            sw_5.setVisibility(View.VISIBLE);
            sw_6.setVisibility(View.VISIBLE);
            sw_hija.setVisibility(View.INVISIBLE);
            sw_hijo.setVisibility(View.VISIBLE);
            sk_hija.setVisibility(View.INVISIBLE);
            sk_hijo.setVisibility(View.VISIBLE);
        }
        if(input.equals("hija"))
        {
            sw_1.setVisibility(View.VISIBLE);
            sw_2.setVisibility(View.VISIBLE);
            sw_3.setVisibility(View.VISIBLE);
            sw_4.setVisibility(View.VISIBLE);
            sw_5.setVisibility(View.VISIBLE);
            sw_6.setVisibility(View.VISIBLE);
            sw_hija.setVisibility(View.VISIBLE);
            sw_hijo.setVisibility(View.INVISIBLE);
            sk_hija.setVisibility(View.VISIBLE);
            sk_hijo.setVisibility(View.INVISIBLE);
        }
    }
}


