package com.example.android.whatstheweather;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.VideoView;

public class DisplayMessageActivity extends AppCompatActivity {

    /* used to connect to videos stored in raw folder */
    private Uri uri;
    /* video view in this activity */
    private VideoView videoView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        videoView = findViewById(R.id.videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        Intent intent = getIntent();
        String weatherType = intent.getStringExtra("weatherType");
        uri = this.getUriGivenWeatherType(weatherType);
        if (uri != null)
        {
            videoView.setVideoURI(uri);
            videoView.start();
        }
        String message = intent.getStringExtra("weatherInfo");
        TextView textView = findViewById(R.id.display);
        textView.setText(message);
    }


    private Uri getUriGivenWeatherType(String weatherType) {
        switch (weatherType) {
            case "Snow":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.snowflakes);
                break;
            case "Rain":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.raindrops);
                break;
            case "Clear":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.clearsky);
                break;
            case "Clouds":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.clouds);
                break;
            case "Mist":
                System.out.println("Misty bitches");
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.haze);
                break;
            case "Fog":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.fog);
                break;
            case "Smoke":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.smoke);
                break;
            case "Haze":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.haze);
                break;
            case "Thunderstorm":
                uri = Uri.parse("android.resource://"+getPackageName()+"/" + R.raw.thunderclouds);
                break;
            default:
                uri = null;
        }
        return uri;
    }

    /* This function is run when the app is resumed */
    @Override
    protected void onResume() {
        super.onResume();
        if (uri != null)
        {
            videoView.setVideoURI(uri);
            videoView.start();
        }
    }
}

