package com.example.horbaxmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private String PREFS_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch sw = (Switch) findViewById(R.id.switch1);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean silent = settings.getBoolean("switchkey", false);
        sw.setChecked(silent);


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(Settings.this, "Shake feature ON", Toast.LENGTH_SHORT).show();

                } else {
                    // The toggle is disabled
                    Toast.makeText(Settings.this, "Shake feature OFF", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
            }
        });


    }
}