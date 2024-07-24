package com.volumebooster.volumeplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private SeekBar systemSound;
    private SeekBar volBoost;
    private AudioManager audioManager;
    private VolumeObserver volumeObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        systemSound = findViewById(R.id.system_sound);
        volBoost = findViewById(R.id.volume_boost);
        SeekBar bassBoost = findViewById(R.id.bass_boost);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Set max range for seekbar with the system sound
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        systemSound.setMax(maxVolume);

        // Set the current volume of the seekbar to the current system sound
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        systemSound.setProgress(currVolume);

        // Add a SeekBar.OnSeekBarChangeListener to the SeekBar
        systemSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do Nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do Nothing
            }
        });

        // Create and register the volume observer
        volumeObserver = new VolumeObserver(new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, volumeObserver);

        // Set max range for volBoost seekbar
        volBoost.setMax(100); // Arbitrary max value for boosting effect

        // Add a SeekBar.OnSeekBarChangeListener to the volBoost SeekBar
        volBoost.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 50) { // Arbitrary threshold for boosting
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do Nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do Nothing
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (volumeObserver != null) {
            getApplicationContext().getContentResolver().unregisterContentObserver(volumeObserver);
        }
    }

    private class VolumeObserver extends ContentObserver {

        VolumeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            systemSound.setProgress(currVolume);
        }
    }
}
