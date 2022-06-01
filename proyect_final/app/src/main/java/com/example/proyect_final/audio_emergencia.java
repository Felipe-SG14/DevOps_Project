package com.example.proyect_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class audio_emergencia extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE=200;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_emergencia);
        if(isMicrophonePresent()){
            getMicrophonePermission();
        }
    }

    public void btnRecordPress(View v){
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(30000);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording is started",Toast.LENGTH_LONG).show();

            mediaRecorder.setOnInfoListener((mr, what, extra) -> {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    Toast.makeText(this, "Recording has stopped",Toast.LENGTH_LONG).show();
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

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

    private void    getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            ==PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        }

    }

    private String getRecordingFilePath(){
        ContextWrapper contextWrapper= new ContextWrapper(getApplicationContext());
        File musicDirectory =contextWrapper.getExternalFilesDir((Environment.DIRECTORY_MUSIC));
        File audio_e =new File(musicDirectory,"testRecordingFile"+".mp3");
        return audio_e.getPath();
    }
}