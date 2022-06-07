package com.example.proyect_final;


import android.Manifest;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 10001;

    private double latitude;
    private double longitude;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;


    ///////// Variables roles
    // public Button mama; NO NECESARIO POR EL MOMENTO


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // mama = (Button)findViewById(R.id.mama); NO NECESARIO POR EL MOMENTO

    }

    /////////  Código para hacer el cambio entre ventanas ///////////////////////////////////////////////////////////////////
    public void rol_mama(View view) {
        /*String rolMama = "mama";
        Intent i1 = new Intent(this, MamaActivity.class);
        i1.putExtra("dato",rolMama);
        startActivity(i1);*/

        Intent audio = new Intent(this, audio_emergencia.class);
        audio.putExtra("dato", "audio");
        startActivity(audio);
    }

    public void rol_papa(View view) {
        // String rolPapa = "papa"; NO ES NECESARIO POR AHORA
        Intent i2 = new Intent(this, PapaActivity.class);
        // i2.putExtra("dato",rolPapa);
        startActivity(i2);
    }

    public void rol_hija(View view) {
        String rolHija = "hija";
        Intent i3 = new Intent(this, MamaActivity.class);
        i3.putExtra("dato", rolHija);
        startActivity(i3);
    }

    public void rol_hijo(View view) {
        String rolHijo = "hijo";
        Intent i4 = new Intent(this, MamaActivity.class);
        i4.putExtra("dato", rolHijo);
        startActivity(i4);
    }

    /////////  Código de PHP-JSON ////////////////////////////////////////////////////////////////////////////////////////////
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
}