package com.example.horbaxmusic;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView listSongs, allMusicList;
    String[] items;
    private int PERM_REQ_CODE;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Menu menu;
    TextView textView;
    TextView txt_song_name;
    SeekBar songSeekBar;
    Button btn_play_pause, btn_next, btn_prev;
    private CircleLineVisualizer mVisualizer;
    int position;
    String song;
    Bundle songExtraData;
    ImageView prev,play, next;
    ArrayAdapter<String> musicArrayAdapter; // Adapter for music list
    String songs[]; // to storage song names;
    ArrayList<File> musics;



    static MediaPlayer mediaPlayer;
   // ArrayList<File> songs;
    Thread updateSeekBar;
    static MediaPlayer mediaPlayer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);

        toolbar=findViewById(R.id.toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new
                ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_allsongs);

        allMusicList = findViewById(R.id.musiclist);

        ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERM_REQ_CODE);

        // working on dexter permission
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                // display music files on storage

                musics = findMusicFiles(Environment.getExternalStorageDirectory());
                songs = new String[musics.size()];
                for (int i = 0; i <musics.size(); i++) {
                    songs[i] = musics.get(i).getName();
                }
                // here you are passing songs in the adapter;
                musicArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, songs);
                //setting the adapter on listview

                allMusicList.setAdapter(musicArrayAdapter);

                allMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // passing the intent to the playeractivity

                        startActivity(new Intent(MainActivity.this, PlayerActivity.class)

                                // we are storing all the songs in the key songlist
                                // we are storing the position of songs in key position
                                .putExtra("songsList", musics)
                                .putExtra("position", position));
                    }
                });

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                // asking for permission

                permissionToken.continuePermissionRequest();

            }
        }).check();




    }


    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_allsongs: break;
            case R.id.nav_fav:
                Intent intent = new Intent(MainActivity.this, Fav.class);
                startActivity(intent);
                break;
            case R.id.nav_aboutus:
                Intent intent1 = new Intent(MainActivity.this, AboutUs.class);
                startActivity(intent1);
                break;
            case R.id.nav_settings:
                Intent intent2 = new Intent(MainActivity.this, Settings.class);
                startActivity(intent2);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START); return true;
    }



    public ArrayList<File> findMusicFiles(File file) {
        ArrayList<File> musicfileobject = new ArrayList<>();
        File[] files = file.listFiles();

        // This line of code throws NullPointerException
        // because ptr is null
        for (File currentFiles : files) {

            if (currentFiles.isDirectory() && !currentFiles.isHidden()) {
                musicfileobject.addAll(findMusicFiles(currentFiles));
            } else {
                if (currentFiles.getName().endsWith(".mp3") || currentFiles.getName().endsWith(".mp4a") || currentFiles.getName().endsWith(".wav")) {
                    musicfileobject.add(currentFiles);
                }
            }
        }

        return musicfileobject;
    }



}
