package me.lkp111138.examtimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.lkp111138.examtimer.objects.DataLoader;

//ca-app-pub-4459787821471144~8472444057
//ca-app-pub-4459787821471144/5052073799
public class MainActivity extends AppCompatActivity {
    long init_msToCount = 0;
    long msToCount = init_msToCount;
    CountDownTimer timer = null;
    boolean started = false;
    long msStarted;
    TextToSpeech tts;
    boolean focus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MobileAds.initialize(this, "ca-app-pub-4459787821471144~8472444057");
        AdView adv = findViewById(R.id.adView);
        adv.loadAd(new AdRequest.Builder().build());
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == 0) {
                    Locale l = Locale.getDefault();
                    if (tts.isLanguageAvailable(l) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                        tts.setLanguage(l);
                        //Toast.makeText(MainActivity.this, String.format("tts locale: %s", l.toString()), Toast.LENGTH_SHORT).show();
                    } else {
                        tts.setLanguage(Locale.US);
                        //Toast.makeText(MainActivity.this, String.format("tts locale: %s", Locale.US.toString()), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> all = pref.getAll();
        String s = "";
        for (String key : all.keySet()) {
            try {
                s += String.format("%s=%s\n", key, pref.getBoolean(key, false));
            } catch (Exception e) {
                //
            }
            try {
                s += String.format("%s=%s\n", key, pref.getFloat(key, 0));
            } catch (Exception e) {
                //
            }
            try {
                s += String.format("%s=%s\n", key, pref.getInt(key, 0));
            } catch (Exception e) {
                //
            }
            try {
                s += String.format("%s=%s\n", key, pref.getLong(key, 0));
            } catch (Exception e) {
                //
            }
            try {
                s += String.format("%s=%s\n", key, pref.getString(key, null));
            } catch (Exception e) {
                //
            }
            try {
                s += String.format("%s=%s\n", key, pref.getStringSet(key, null));
            } catch (Exception e) {
                //
            }
        }
        if (s.length() > 2) {
            s = s.substring(0, s.length() - 2);
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }*/
        try {
            final InputStream data = getAssets().open(getResources().getString(R.string.json_file));
            DataLoader.load(data, this);
        } catch (IOException|JSONException e) {
            Toast.makeText(this, "Sorry, we are unable to read the necessary data. " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (focus) {
            return;
        }
        focus = true;
        // progress wheel size
        final RingProgressBar vg = findViewById(R.id.progress);
        final ViewGroup vg2 = findViewById(R.id.progress_layout);
        vg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                vg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                final float scale = getResources().getDisplayMetrics().density;
                // Here you can get the size :)
                ViewGroup.LayoutParams layoutParams = vg.getLayoutParams();
                layoutParams.height = layoutParams.width = (int) (Math.min(vg2.getHeight(), vg2.getWidth()) - (scale * 24));
                findViewById(R.id.progress).setLayoutParams(layoutParams);
                // Toast.makeText(MainActivity.this, String.format(Locale.getDefault(), "bound: w=%d, h=%d\nprogress: w=%d, h=%d", vg2.getHeight(), vg2.getWidth(), layoutParams.width, layoutParams.height), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    public void onClick(final View v) {
        int[] positions = DataLoader.getPositions();
        if (positions[0] == 0 || positions[1] == 0 || positions[2] == 0) {
            Toast.makeText(this, R.string.text_select_first, Toast.LENGTH_SHORT).show();
            return;
        }
        final TextView tv = findViewById(R.id.remaining_time);
        final RingProgressBar rbar = findViewById(R.id.progress);
        final int ms = (int) msToCount;
        final int init_ms = (int) init_msToCount;
        final Button control = findViewById(R.id.start_button);
        final Button reset = findViewById(R.id.button_reset);
        final Spinner exam_dropdown = findViewById(R.id.exam_dropdown);
        final Spinner subj_dropdown = findViewById(R.id.subj_dropdown);
        final Spinner paper_dropdown = findViewById(R.id.paper_dropdown);
        exam_dropdown.setEnabled(false);
        subj_dropdown.setEnabled(false);
        paper_dropdown.setEnabled(false);
        reset.setEnabled(false);
        rbar.setMax(1000);
        if (!started) {
            // start
            timer = new CountDownTimer(ms, Math.min(Math.max(50, ms / 200), 1000)) {
                @Override
                public void onTick(long l) {
                    tv.setText(formatMilli(l));
                    rbar.setProgress((int) (1000 * l / init_ms));
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                    onTick(0);
                    v.setEnabled(true);
                    started = false;
                    control.setText(R.string.btn_start);
                    reset.setEnabled(false);
                    onReset(null);
                }
            }.start();
            //pi = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //manager.set(AlarmManager.RTC_WAKEUP, alarmAt, pi);
            msStarted = System.currentTimeMillis();
            started = true;
            control.setText(R.string.btn_pause);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            // pause
            timer.cancel();
            long now = System.currentTimeMillis();
            msToCount -= (now - msStarted);
            control.setText(R.string.btn_continue);
            started = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        reset.setEnabled(!started);
    }

    public void onReset(final View v) {
        final TextView tv = findViewById(R.id.remaining_time);
        final RingProgressBar rbar = findViewById(R.id.progress);
        final Button control = findViewById(R.id.start_button);
        // pause
        if (timer != null) {
            timer.cancel();
        }
        control.setText(R.string.btn_start);
        started = false;
        // reset
        msToCount = init_msToCount;
        tv.setText(formatMilli(msToCount));
        rbar.setMax(1000);
        rbar.setProgress(1000);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int[] positions = DataLoader.getPositions();
        final Spinner exam_dropdown = findViewById(R.id.exam_dropdown);
        final Spinner subj_dropdown = findViewById(R.id.subj_dropdown);
        final Spinner paper_dropdown = findViewById(R.id.paper_dropdown);
        exam_dropdown.setEnabled(true);
        subj_dropdown.setEnabled(positions[0] > 0);
        paper_dropdown.setEnabled(positions[1] > 0);
    }

    public String formatMilli(long milli) {
        milli += 999;
        long hour = milli / 3600000;
        long minute = (milli / 60000) % 60;
        long second = ((milli / 1000) % 60);
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
    }

    public String gen_exam_date(long t) {
        if (t < 0) {
            return getResources().getString(R.string.text_exam_date, getResources().getString(R.string.text_exam_date_variable), "");
        }
        if (t == 0) {
            return getResources().getString(R.string.text_exam_date_placeholder);
        }
        Date d = new Date(t);
        return getResources().getString(R.string.text_exam_date, DateFormat.getDateFormat(this).format(d), DateFormat.getTimeFormat(this).format(d));
    }

    public void setInit_msToCount(long init_msToCount, DataLoader.LOL lol) {
        lol.hashCode();
        this.init_msToCount = init_msToCount;
    }
}
