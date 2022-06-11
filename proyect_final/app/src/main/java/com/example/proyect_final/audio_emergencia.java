package com.example.proyect_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.AsyncTask;
import android.Manifest;
import android.content.ContextWrapper;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class audio_emergencia extends AppCompatActivity {

    //------------------------------UBICACIÓN-------------------------------
    private static final String TAG = "MainActivity";
    public static final int DEFAULT_UPDATE_INTERVAL = 10;
    public static final int FAST_UPDATE_INTERVAL = 5;
    int LOCATION_REQUEST_CODE = 10001;
    private double latitude;
    private double longitude;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if(locationResult==null){
                return;
            }
            for(Location location: locationResult.getLocations()){
                Log.d(TAG,"onLocationResult: " + location.toString());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

        }
    };
    //------------------------------------------------------------------------
    
    //--------------GRABACIÓN AUDIO-------------------
    private static int MICROPHONE_PERMISSION_CODE=200;
    MediaRecorder mediaRecorder;
    //------------------------------------------------
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //--------------GRABACIÓN AUDIO--------------
        setContentView(R.layout.activity_audio_emergencia);
        if(isMicrophonePresent()){
            getMicrophonePermission();
        }
        //--------------------------------------------

        //--------------------------------UBICACIÓN-----------------------------------------------
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //----------------------------------------------------------------------------------------

        //----------------------------------------------ENVÍO SMS ------------------------------------------------------------------------------

        if(ActivityCompat.checkSelfPermission(audio_emergencia.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(audio_emergencia.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        //------------------------------------------------------------------------------------------------------------------------------------

    }

    public void btnRecordPress(View v){
        try {
            //--------------GRABACIÓN AUDIO------------------------------------------------------
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFile().getPath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(5000);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording is started",Toast.LENGTH_LONG).show();
            //-----------------------------------------------------------------------------------

            //----------------------------------------------GRABACIÓN AUDIO-------------------------------------------------------------------------
            mediaRecorder.setOnInfoListener((mr, what, extra) -> {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    Toast.makeText(this, "Recording has stopped",Toast.LENGTH_LONG).show();
                    mediaRecorder.release();

                    //-------------------ENVíO ARCHIVO A SERVIDOR FTP------------------------
                    new sendFiletFTP().execute();
                    //-----------------------------------------------------------------------
                }
            });

            //----------------------------------------------------------------ENVÍO SMS--------------------------------------------
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+52 5534532007",null, "Prueba 2",null, null);

            Toast.makeText(audio_emergencia.this, "MSJ Enviado", Toast.LENGTH_LONG).show();
            //----------------------------------------------------------------------------------------------------------------------

            //--------------------------------------------------UBICACIÓN--------------------------------------------------------------------------
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
            } else {
                askLocationPermission();
            }
            //--------------------------------------------------------------------------------------------------------------------------------------
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //----------------------------------------------GRABACIÓN AUDIO-------------------------------------------------------------------------
    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            ==PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        }

    }

    public File getRecordingFile(){
        ContextWrapper contextWrapper= new ContextWrapper(getApplicationContext());
        File musicDirectory =contextWrapper.getExternalFilesDir((Environment.DIRECTORY_MUSIC));
        File audio_e =new File(musicDirectory, "audio_emergencia.aac");
        return audio_e;
    }

    public String getDate() {
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("ddMMyyyy_HHmmss");
        Date now = new Date();
        String timestamp=simpleDateFormat.format(now);
        return  timestamp;

    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------------------------UBICACIÓN------------------------------------------------------------------------------
    
    public void onStopPress(View view) {
        stopLocationUpdates();
        Log.d(TAG,getRecordingFile().getPath());

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
                        apiException.startResolutionForResult(audio_emergencia.this, 1001);
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

    private void stopLocationUpdates(){
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

    public class sendFiletFTP extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String server="davinci999.xyz";
            String username="u447795502.pedro_tsm";
            String password="holasoypedro123TSM";
            String dirPath="/audio_dir";

            FTPClient ftpClient = new FTPClient();
            try
            {
                ftpClient.connect(server);
                ftpClient.login(username,password);
                ftpClient.enterLocalPassiveMode();

                Log.d(TAG, "CONNECTED");

                InputStream inputStream =new FileInputStream(getRecordingFile());
                ftpClient.storeFile(dirPath +"/audio_emergencia.aac",inputStream);
                inputStream.close();

                //boolean stored =ftpClient.storeFile(dirPath+"/audio_emergencia.mp3",inputStream);

                ftpClient.logout();
                ftpClient.disconnect();

                Log.d(TAG,"DISCONNECTED" );


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------
}