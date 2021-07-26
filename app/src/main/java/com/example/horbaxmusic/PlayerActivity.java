package com.example.horbaxmusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlayerActivity extends AppCompatActivity {

    Bundle songExtraData; // in order to get the values from previous activity
    ImageView prev,play, next,fav,loop;
    int position;
    SeekBar mSeekBarTime;
    static MediaPlayer mMediaPlayer; // if not static then two or more than two songs will be played at the same time
    TextView songName;
    ArrayList<File> musicList;
    private float acelVal;
    private float acelLast;
    private float shake;
    private SensorManager sm;
    private CircleLineVisualizer mVisualizer;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mVisualizer = findViewById(R.id.blob1);
        // mAudioPlayer = new AudioPlayer();
        mVisualizer.setDrawLine(true);
            sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            acelVal = SensorManager.GRAVITY_EARTH;
            acelLast = SensorManager.GRAVITY_EARTH;
            shake = 0.00f;
        // casting views

        prev = findViewById(R.id.previous);
        fav = findViewById(R.id.fav);
        play = findViewById(R.id.play);
        loop = findViewById(R.id.loop);
        next = findViewById(R.id.next);
        mSeekBarTime = findViewById(R.id.mSeekBarTime);
        songName  = findViewById(R.id.songName);


        // when the activity launches mediaplayer should be on stop

        if (mMediaPlayer!=null) {
            mMediaPlayer.stop();
        }

        // getting values from previous activity
        Intent intent = getIntent();
        songExtraData = intent.getExtras();

        musicList = (ArrayList)songExtraData.getParcelableArrayList("songsList");
        position = songExtraData.getInt("position", 0);

        // creating a new method that initializes the media player on launch of activity

        initializeMusicPlayer(position);



        // play button

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                looping();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position < musicList.size() -1) {
                    position++;
                } else {
                    position = 0;
                }
                initializeMusicPlayer(position);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position<=0) {
                    position = musicList.size();
                } else {
                    position++;
                }

                initializeMusicPlayer(position);
            }
        });




        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav.setImageResource(R.drawable.favon);
                Toast.makeText(PlayerActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show();
               /* startActivity(new Intent(PlayerActivity.this, Fav.class)

                        // we are storing all the songs in the key songlist

                        .putExtra("songsList", musicList.get(position).getName())
                         .putExtra("i",1));*/

                    SharedPreferences sharedPreferences = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("k", musicList.get(position).getName()).apply();


            }
        });




    }




    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acelLast = acelVal;
            acelVal = (float) Math.sqrt((double) (x*x+y*y+z*z));
            float delta = acelVal - acelLast;
            shake = shake*0.9f+delta;
            if(shake>12){
                {

                    if (position < musicList.size() -1) {
                        position++;
                    } else {
                        position = 0;
                    }
                    initializeMusicPlayer(position);
                }
                Toast.makeText(PlayerActivity.this, "Track changed by shaking", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };





    private void initializeMusicPlayer(int position) {

        // if mediaplayer is not null and playing reset it at the launch of activity

        if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        // getting out the song name
        String name = musicList.get(position).getName();
        songName.setText(name);

        // accessing the songs on storage

        Uri uri = Uri.parse(musicList.get(position).toString());
        //Uri uri = Uri.parse(songs.get(position).toString());
        mMediaPlayer =  MediaPlayer.create(PlayerActivity.this,uri);
        mVisualizer.setAudioSessionId(mMediaPlayer.getAudioSessionId());
        mMediaPlayer.start();

        // creating a mediaplayer
        // passing the uri

        //mMediaPlayer = MediaPlayer.create(this, uri);

        // SETTING ON PREPARED MEDIAPLAYER

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                // seekbar
                mSeekBarTime.setMax(mMediaPlayer.getDuration());

                // while mediaplayer is playing the play button should display pause
                play.setImageResource(R.drawable.pause);
                // start the mediaplayer
                mMediaPlayer.start();
            }
        });

        // setting the oncompletion listener
        // after song finishes what should happen // for now we will just set the pause button to play

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.play);
            }
        });
        
        
        // if you want the the mediaplayer to go to next song after its finished playing one song its optional 
        /*mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.play);

                int currentPosition = position;
                if (currentPosition < musicList.size() -1) {
                    currentPosition++;
                } else {
                    currentPosition = 0;
                }
                initializeMusicPlayer(currentPosition);

            }
        });*/


        // working on seekbar

        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // IF USER TOUCHES AND MESSES WITH SEEEKBAR
                if (fromUser) {
                    mSeekBarTime.setProgress(progress);
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // till here seekbar wont change on its own

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mMediaPlayer!=null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mSeekBarTime.setProgress(msg.what);
        }
    };

                // lastly create a method for play

            private void play() {
                // if mediaplayer is not null and playing and if play button is pressed pause it

                if (mMediaPlayer!=null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                  //  mMediaPlayer.setLooping(true);
                    // change the image of playpause button to play when we pause it
                    play.setImageResource(R.drawable.play);
                } else {
                    mMediaPlayer.start();
                    // if mediaplayer is playing // the image of play button should display pause
                    play.setImageResource(R.drawable.pause);

                }
            }

    private void looping() {
        loop.setImageResource(R.drawable.loopoff);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
           // mMediaPlayer.setLooping(false);
            //loop.setImageResource(R.drawable.loopoff);
    }

}
