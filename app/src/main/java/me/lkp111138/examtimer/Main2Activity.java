package me.lkp111138.examtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.security.acl.Group;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class Main2Activity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    private boolean focus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        playSound(this, getAlarmUri());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MobileAds.initialize(this, "ca-app-pub-4459787821471144~8472444057");
        AdView adv = findViewById(R.id.adView2);
        adv.loadAd(new AdRequest.Builder().build());
        new CountDownTimer(360000000, 1000) {
            boolean isWhite = true;
            @Override
            public void onTick(long millisUntilFinished) {
                if (isWhite) {
                    findViewById(R.id.activiy_main2).setBackgroundColor(Color.argb(150, 255, 0, 0));
                } else {
                    findViewById(R.id.activiy_main2).setBackgroundColor(Color.argb(255, 255, 255, 255));
                }
                isWhite = !isWhite;
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void onClick(View v) {
        mMediaPlayer.stop();
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
    }

    private void playSound(Context context, Uri alert) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0 && !pref.getBoolean("silent", false)) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (focus) {
            return;
        }
        focus = true;
        // progress wheel size
        final RingProgressBar vg = findViewById(R.id.progress2);
        final ViewGroup vg2 = findViewById(R.id.progress_layout2);
        vg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                vg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final float scale = getResources().getDisplayMetrics().density;
                // Here you can get the size :)
                ViewGroup.LayoutParams layoutParams = vg.getLayoutParams();
                layoutParams.height = layoutParams.width = (int) (Math.min(vg2.getHeight(), vg2.getWidth()) - (scale * 24));
                findViewById(R.id.progress2).setLayoutParams(layoutParams);
                // Toast.makeText(MainActivity.this, String.format(Locale.getDefault(), "bound: w=%d, h=%d\nprogress: w=%d, h=%d", vg2.getHeight(), vg2.getWidth(), layoutParams.width, layoutParams.height), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
