package com.example.proyect_final;

import android.Manifest;
import android.content.Intent;
import android.content.ContextWrapper;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginActivity extends AppCompatActivity {
    public EditText email;
    public EditText password;
    //------------------------------UBICACI??N-------------------------------
    private static final String TAG = "MainActivity";
    public static final int DEFAULT_UPDATE_INTERVAL = 10;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int MAX_DURATION_MS = 5000;
    int LOCATION_REQUEST_CODE = 10001;
    public double latitude;
    public double longitude;
    static public String nameAudioFile;
    public int contador = 0;
    static public String google_url;
    static public String deviceId;
    static public int user;


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    SmsManager smsManager = SmsManager.getDefault();
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }

            for (Location location : locationResult.getLocations()) {
                Log.d(TAG, "onLocationResult: " + location.toString());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                contador = contador + 1;
                if (contador == 1) {
                    google_url = "https://www.google.com/maps/?q=" + latitude + "," + longitude;
                    getUserID();
                    sendMessage();

                    /*SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+52 5534532007",null, "Audio de emergencia: https://davinci999.xyz/audio_dir/"+nameAudioFile,null, null);
                    smsManager.sendTextMessage( "+52 5534532007",null, "??ltima ubicaci??n "+google_url,null, null);
                    Log.d(TAG, "onSuccess " + google_url);*/
                }
            }


        }
    };

    //------------------------------------PHP REQUEST--------------------------------------
    public void getUserIDRequest(String url) { // Lee los n??meros de telefono
        RequestQueue myQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user = response.getInt("user_id");
                            Log.d(TAG, "UserTRY " + user);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        myQueue.add(request);
        myQueue.start();
        Log.d(TAG, "UserDefinite " + user);
    }
    //------------------------------------------------------------------------------------


    public void getUserID() {
        String url = "https://davinci999.xyz/deviceID.php?dispositivo_id=" + deviceId;
        getUserIDRequest(url);
    }

    public void sendMessage() {
        Log.d(TAG, "Dispositivo= " + deviceId);
        Log.d(TAG, "User " + user);

        if (user == 3) {
            smsManager.sendTextMessage("+52 5534532007", null, "Audio de emergencia: https://davinci999.xyz/audio_dir/" + nameAudioFile, null, null);
            smsManager.sendTextMessage("+52 5534532007", null, "??ltima ubicaci??n " + google_url, null, null);
            Log.d(TAG, "onSuccess " + google_url + " " + user);
        } else if (user == 1) {
            smsManager.sendTextMessage("+52 5550685663", null, "Audio de emergencia: https://davinci999.xyz/audio_dir/" + nameAudioFile, null, null);
            smsManager.sendTextMessage("+52 5550685663", null, "??ltima ubicaci??n " + google_url, null, null);
            Log.d(TAG, "onSuccess " + google_url);

        }
    }
    //------------------------------------------------------------------------

    //--------------GRABACI??N AUDIO-------------------
    private static int MICROPHONE_PERMISSION_CODE = 200;
    MediaRecorder mediaRecorder;
    //------------------------------------------------




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.editTextTextPersonName);
        password = (EditText) findViewById(R.id.editTextTextPassword);

        //--------------GRABACI??N AUDIO--------------

        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }
        //--------------------------------------------

        //--------------------------------UBICACI??N-----------------------------------------------
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //----------------------------------------------------------------------------------------

        //----------------------------------------------ENV??O SMS ------------------------------------------------------------------------------

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);

        }

        //------------------------------------------------------------------------------------------------------------------------------------

    }
    public void loginButton (View view) throws JSONException {
        datosusandoVolley();
    }

    private void datosusandoVolley() throws JSONException {
        String url = "https://davinci999.xyz/log.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CORREO",email.getText());
        jsonObject.put("PASSWORD",password.getText());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int id = response.getInt("ID");
                            if(id!=0)
                            {
                                openUserActivity(id);
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Vuelva a intertarlo",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast response_toast = Toast.makeText(getApplicationContext(), "e " + error.toString(), Toast.LENGTH_LONG);
                response_toast.show();
            }
        }

        );
        requestQueue.add(jsonObjectRequest);
    }
    public void openUserActivity(int id){
        switch (id){
            case 1:
                String rolHijo = "hijo";
                Intent i4 = new Intent(this, MenuActivity.class);
                i4.putExtra("dato",rolHijo);
                startActivity(i4);
                Toast.makeText(LoginActivity.this,"Hola Gabriel",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String rolHija = "hija";
                Intent i3 = new Intent(this, MenuActivity.class);
                i3.putExtra("dato",rolHija);
                startActivity(i3);
                Toast.makeText(LoginActivity.this,"Hola Diana",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                String rolPapa = "papa";
                Intent i2 = new Intent(this, MenuActivity.class);
                i2.putExtra("dato", rolPapa);
                startActivity(i2);
                Toast.makeText(LoginActivity.this,"Hola Juan",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                String rolMama = "mama";
                Intent i1 = new Intent(this, MenuActivity.class);
                i1.putExtra("dato",rolMama);
                startActivity(i1);
                Toast.makeText(LoginActivity.this,"Hola Carmen",Toast.LENGTH_SHORT).show();
                break;



        }
    }
    public void btnRecordPress(View v) {
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "ID del tel??fono: " + deviceId);
        try {
            //--------------GRABACI??N AUDIO------------------------------------------------------
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFile().getPath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(MAX_DURATION_MS);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording is started", Toast.LENGTH_LONG).show();
            //-----------------------------------------------------------------------------------

            //----------------------------------------------------------------ENV??O SMS-------------------------------------------

            //ArrayList<String> supplierNames = new ArrayList<String>();
            /*supplierNames.add("Audio de emergencia: https://davinci999.xyz/audio_dir/"+nameAudioFile);
            supplierNames.add(" ??ltima ubicaci??n "+google_url);*/
            /*String message="Audio de emergencia: https://davinci999.xyz/audio_dir/"+nameAudioFile+" ??ltima ubicaci??n "+google_url;
            //ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage("+52 5550685663",null,parts ,null, null);*/


            //----------------------------------------------------------------------------------------------------------------------

            //----------------------------------------------GRABACI??N AUDIO-------------------------------------------------------------------------
            mediaRecorder.setOnInfoListener((mr, what, extra) -> {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Toast.makeText(this, "Recording has stopped", Toast.LENGTH_LONG).show();
                    mediaRecorder.release();
                    //-------------------ENV??O ARCHIVO A SERVIDOR FTP------------------------
                    new sendFileFTP().execute();
                    //-----------------------------------------------------------------------
                    //sendMessage();
                    Toast.makeText(LoginActivity.this, "MSJ Enviado", Toast.LENGTH_LONG).show();
                }
            });


            //--------------------------------------------------UBICACI??N--------------------------------------------------------------------------
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
            } else {
                askLocationPermission();
            }
            //--------------------------------------------------------------------------------------------------------------------------------------
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------GRABACI??N AUDIO-------------------------------------------------------------------------
    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }

    }

    public File getRecordingFile() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir((Environment.DIRECTORY_MUSIC));

        File audio_e = new File(musicDirectory, getFileName());
        return audio_e;
    }

    public String getFileName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
        Date now = new Date();
        String timestamp = simpleDateFormat.format(now);
        nameAudioFile = "audio_emergencia_" + timestamp + ".wav";
        return nameAudioFile;
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------UBICACI??N------------------------------------------------------------------------------

    public void onStopPress(View view) {
        stopLocationUpdates();
        Log.d(TAG, nameAudioFile);

    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //setting of devices satisfied and start location updates
                startLocationUpdate();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(LoginActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
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
                checkSettingsAndStartLocationUpdates();
            } else {
                //Permission not granted
            }
        }
    }

    public class sendFileFTP extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String server = "davinci999.xyz";
            String username = "u447795502.pedro_tsm";
            String password = "holasoypedro123TSM";
            String dirPath = "/audio_dir";

            FTPClient ftpClient = new FTPClient();
            try {
                ftpClient.connect(server);
                ftpClient.login(username, password);
                ftpClient.enterLocalPassiveMode();

                Log.d(TAG, "CONNECTED");

                InputStream inputStream = new FileInputStream(getRecordingFile());
                ftpClient.storeFile(dirPath + "/" + nameAudioFile, inputStream);
                inputStream.close();

                //boolean stored =ftpClient.storeFile(dirPath+"/audio_emergencia.mp3",inputStream);

                ftpClient.logout();
                ftpClient.disconnect();

                Log.d(TAG, "DISCONNECTED");


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;


    }}





    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
