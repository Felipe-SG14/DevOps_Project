package com.example.proyect_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.Nullable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import android.nfc.Tag;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 1001;

    private double latitude;
    private double longitude;

    FusedLocationProviderClient fusedLocationProviderClient;

    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

                    Log.d(TAG, "onSuccess" + location.toString());
                    Log.d(TAG, "onSuccess" + latitude);
                    Log.d(TAG, "onSuccess" + longitude);

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