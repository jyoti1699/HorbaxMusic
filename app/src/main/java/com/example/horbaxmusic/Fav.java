package com.example.horbaxmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Fav extends AppCompatActivity {
    ListView favlist;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    ArrayList<File> musicList;
    String songExtraData;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        i = getIntent().getIntExtra("i",0);

        if(i==1){

            songExtraData = getIntent().getStringExtra("songsList");

            // musicList = (ArrayList)songExtraData.getParcelableArrayList("musicList");

            favlist = findViewById(R.id.favlist);

            arrayList = new ArrayList<String>();

            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

            favlist.setAdapter(adapter);
            arrayList.add(songExtraData);

        }
        else
        {
            SharedPreferences sharedPreferences = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
            //String userName = sharedPreferences.getString("user_name", null);
           // int userAge = sharedPreferences.getInt("user_age", 0);

            favlist = findViewById(R.id.favlist);

            arrayList = new ArrayList<String>();

            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

            favlist.setAdapter(adapter);

            //SharedPreferences prefs= getSharedPreferences();

            arrayList.add(sharedPreferences.getString("k", null));

        }


    }
}