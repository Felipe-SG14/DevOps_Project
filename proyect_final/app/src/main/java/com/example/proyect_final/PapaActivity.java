package com.example.proyect_final;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

public class PapaActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 1001;

    public double latitude;
    public double longitude;
    static public String google_url;
    static public int contador=0;

    FusedLocationProviderClient fusedLocationProviderClient;
    SmsManager smsManager = SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_papa);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        if (ActivityCompat.checkSelfPermission(PapaActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PapaActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);

        }
    }

    public void btnLocation(View view)
    {
        contador=contador+1;
        getLastLocation();
        google_url = "https://www.google.com/maps/search/?api=1&query="+latitude+"%2C"+longitude;
        Log.d(TAG,"URL "+google_url);
        sendSMS();
    }
     public void sendSMS()
     {
         // MAMÁ
         smsManager.sendTextMessage("+52 5515045179", null, "SmartHome Ultima Ubicacion Papa: "+google_url, null, null);
         // HIJA
         smsManager.sendTextMessage("+52 5516286613", null, "SmartHome Ultima Ubicacion Papa: "+google_url, null, null);
         Log.d(TAG,"MAPA "+google_url);
         //HIJO
         smsManager.sendTextMessage("+52 5547691329", null, "SmartHome Última Ubicación Papá: "+google_url, null, null);
     }

    // Método para regresar a la ventana principal
    public void Regresar(View view)
    {
        Intent regresarMain = new Intent(this, MainActivity.class);
        startActivity(regresarMain);
    }

    /////////  Código de GPS
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

                    // Aqui se envia la ubicacion (Latitud  y longitud)
                    String url = "https://davinci999.xyz/solicitud_ubicacion.php"; //http://davinci999.xyz
                    JSONObject jsonObject_ubicacion  = new JSONObject();
                    try {
                        jsonObject_ubicacion.put("UBICACION_ID",3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject_ubicacion.put("LONGITUD",longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonObject_ubicacion.put("LATITUD",latitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(contador==1) {
                        String message = "Latitud: " + String.valueOf(latitude) +
                                "\n" + "Longitud: " + String.valueOf(longitude);
                        try {
                            dataUsingVolley(jsonObject_ubicacion, url, message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    

                } else {
                    Toast.makeText(PapaActivity.this,"Your location is not active",Toast.LENGTH_SHORT).show();
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

    public void dataUsingVolley(JSONObject jsonObject, String url, String message) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Toast response_toast = Toast.makeText(getApplicationContext(), "e " + response.toString(), Toast.LENGTH_LONG);
                    response_toast.show();
                }, error -> {
            Toast response_toast = Toast.makeText(PapaActivity.this, message, Toast.LENGTH_LONG);
            response_toast.show();
        });

        requestQueue.add(jsonObjectRequest);
    }


}