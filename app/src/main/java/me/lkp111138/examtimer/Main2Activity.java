package me.lkp111138.examtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.Locale;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.lkp111138.examtimer.objects.Paper;

public class Main2Activity extends AppCompatActivity {
    private static MediaPlayer mMediaPlayer;
    private boolean focus = false;
    private static Main2Activity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main2);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(instance);
        playSound(this, Uri.parse(pref.getString("ringtone", "")));
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

    static void playSound(Context context, Uri alert) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(instance);
        mMediaPlayer = new MediaPlayer();
        try {
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            Toast.makeText(instance, "Time is up", Toast.LENGTH_SHORT).show();
            if ((audioManager == null || audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) && !pref.getBoolean("silent", false)) {
                if (pref.getBoolean("enable_tts", false)) {
                    String _tts_lang = Locale.getDefault().getLanguage().split("[-_]+")[0];
                    switch (_tts_lang) {
                        case "en":
                        case "zh":
                            break;
                        default:
                            _tts_lang = "en";
                    }
                    final String tts_lang = _tts_lang;
                    final Paper _paper = MainActivity.getPaper();
                    String sentence = _paper.getParent().getParent().getTtsText().get(tts_lang)[2];
                    System.out.println("file:///android_asset/" + sentence);
                    MediaPlayer mp;
                    switch (tts_lang) {
                        case "en":
                            mp = MediaPlayer.create(Main2Activity.instance, R.raw.en_0mins);
                            mp.start();
                            break;
                        case "zh":
                            mp = MediaPlayer.create(Main2Activity.instance, R.raw.zh_0mins);
                            mp.start();
                            break;
                    }
                } else {
                    mMediaPlayer.setDataSource(context, alert);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                }
            }
        } catch (IOException e) {
            System.out.println("OOPS");
            e.printStackTrace();
        }
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
