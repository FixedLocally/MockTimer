package me.lkp111138.examtimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
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

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
import me.lkp111138.examtimer.objects.DataLoader;
import me.lkp111138.examtimer.objects.Paper;

//ca-app-pub-4459787821471144~8472444057
//ca-app-pub-4459787821471144/5052073799
public class MainActivity extends AppCompatActivity {
    long init_msToCount = 0;
    long msToCount = init_msToCount;
    CountDownTimer timer = null;
    boolean started = false;
    long msStarted;
    boolean focus = false;
    static Paper paper = null;
    private ShareActionProvider mShareActionProvider;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MobileAds.initialize(this, "ca-app-pub-4459787821471144~8472444057");
        AdView adv = findViewById(R.id.adView);
        adv.loadAd(new AdRequest.Builder().build());
        try {
            final InputStream data = getAssets().open(getResources().getString(R.string.json_file));
            DataLoader.load(data, this);
        } catch (IOException|JSONException e) {
            StackTraceElement[] elems = e.getStackTrace();
            StackTraceElement elem = elems[0];
            int i = 0;
            while (!elem.getClassName().startsWith("me.lkp111138") && (i < elems.length)) {
                ++i;
                elem = elems[i];
            }
            Toast.makeText(this, String.format("Sorry, we are unable to read the necessary data. %s at %s:%d (%s)", e.getLocalizedMessage(), elem.getFileName(), elem.getLineNumber(), elem.getClassName()), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
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
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getResources().getString(R.string.text_share));
        sendIntent.setType("text/plain");
        setShareIntent(sendIntent);
        // Return true to display menu
        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.text_share));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
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
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        exam_dropdown.setEnabled(false);
        subj_dropdown.setEnabled(false);
        paper_dropdown.setEnabled(false);
        reset.setEnabled(false);
        rbar.setMax(1000);
        final Paper _paper = paper;
        String _tts_lang = Locale.getDefault().getLanguage().split("[-_]+")[0];
        switch (_tts_lang) {
            case "en":
            case "zh":
                break;
            default:
                _tts_lang = "en";
        }
        final String tts_lang = _tts_lang;
        if (!started) {
            // start
            timer = new CountDownTimer(ms, Math.min(Math.max(50, ms / 200), 1000)) {
                boolean has_passed_15min_mark = false;
                boolean has_passed_5min_mark = false;
                @Override
                public void onTick(long l) {
                    tv.setText(formatMilli(l));
                    rbar.setProgress((int) (1000 * l / init_ms));
                    // why dont just fucking use pre-recorded files?
                    if ((l < 900000) && (l > 899000) && !has_passed_15min_mark && _paper.isTtsEnabled()) {
                        if (!pref.getBoolean("silent", false) && pref.getBoolean("enable_tts", false)) {
                            MediaPlayer mp;
                            switch (tts_lang) {
                                case "en":
                                    mp = MediaPlayer.create(MainActivity.this, R.raw.en_15mins);
                                    mp.start();
                                    break;
                                case "zh":
                                    mp = MediaPlayer.create(MainActivity.this, R.raw.zh_15mins);
                                    mp.start();
                                    break;
                            }
                        }
                        has_passed_15min_mark = true;
                    }
                    if ((l < 300000) && (l > 299000) && !has_passed_5min_mark && _paper.isTtsEnabled()) {
                        MediaPlayer mp;
                        switch (tts_lang) {
                            case "en":
                                mp = MediaPlayer.create(MainActivity.this, R.raw.en_5mins);
                                mp.start();
                                break;
                            case "zh":
                                mp = MediaPlayer.create(MainActivity.this, R.raw.zh_5mins);
                                mp.start();
                                break;
                        }
                        has_passed_5min_mark = true;
                    }
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
        lol.fuckme();
        this.init_msToCount = init_msToCount;
    }

    public void setPaper(Paper paper, DataLoader.LOL lol) {
        lol.fuckme();
        MainActivity.paper = paper;
    }

    public static Paper getPaper() {
        return paper;
    }
}
