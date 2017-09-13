package xyz.snsstudio.gpsalarm;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sande on 2016-12-21.
 */

public class Alarm extends Activity {
    MediaPlayer mediaPlayer = new MediaPlayer();


    public void onAttachedToWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        runAlarm();

    }

    public void runAlarm() {
        try {
            float volume;
            volume = getIntent().getIntExtra("Volume", 75) / 100.0f;
            if (getIntent().getBooleanExtra("Vibrate", false)) {
                Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
                long[] pattern = {0, 100, 1000, 100, 100, 100};
                v.vibrate(pattern, 0);

            }
            //TODO: Write Vibration.
            mediaPlayer.setDataSource(this, Uri.parse(getIntent().getStringExtra("Tone")));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(volume, volume);
            //TODO: I disabled this for testing purposes.
//            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void snoozeButton_click(View view) {
        Data.Snooze = true;
        int SnoozeTimes = Data.SnoozeTimes;
        SnoozeTimes = SnoozeTimes + 1;
        Data.SnoozeTimes = SnoozeTimes;
        mediaPlayer.stop();
        finish();
    }

    public void stopButton_click(View view) {
        mediaPlayer.stop();
        Data.Snooze = false;
        Data.SnoozeTimes = 0;
        finish();
    }
}
