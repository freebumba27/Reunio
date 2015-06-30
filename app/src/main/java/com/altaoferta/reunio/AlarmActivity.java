package com.altaoferta.reunio;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class AlarmActivity extends Activity {

    TextView textViewShowMessage;
    Vibrator v;
    MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        textViewShowMessage = (TextView)findViewById(R.id.textViewShowMessage);
        textViewShowMessage.setText("You Have an Appointment at "+getIntent().getStringExtra("time"));

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000, 1000, 2000, 3000, 4000};
        v.vibrate(pattern, -1);

        Uri alert = Uri.parse("android.resource://" + this.getPackageName() + "/raw/ring_tone");
        mMediaPlayer = new MediaPlayer();
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        try {
            mMediaPlayer.setDataSource(this, alert);
            mMediaPlayer.setVolume(Float.parseFloat(Double.toString(am.getStreamVolume(AudioManager.STREAM_ALARM) / 7.0)),
                    Float.parseFloat(Double.toString(am.getStreamVolume(AudioManager.STREAM_RING) / 7.0)));

            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayer!=null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                }
            }, 1000 * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        Intent i = new Intent(this, DashBoardActivity.class);
        finish();
//        startActivity(i);
        v.cancel();
        if(mMediaPlayer!=null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
