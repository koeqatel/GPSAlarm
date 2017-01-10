package xyz.snsstudio.gpsalarm;

import android.app.Activity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by sande on 2016-12-21.
 */

public class Alarm extends Activity {
    MediaPlayer mediaPlayer = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        try {
            mediaPlayer.setDataSource(getIntent().getStringExtra("Tone"));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void snoozeButton(View view) {
        mediaPlayer.stop();
        //TODO add snooze delay here
    }

    public void stopButton(View view) {
        mediaPlayer.stop();
        finish();
    }
}
