package me.lkp111138.examtimer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

//ca-app-pub-4459787821471144~8472444057
//ca-app-pub-4459787821471144/5052073799
public class MainActivity extends AppCompatActivity {
    long init_msToCount = 0;
    long msToCount = init_msToCount;
    CountDownTimer timer = null;
    boolean started = false;
    long msStarted;
    int[] positions = {0, 0, 0};
    TextToSpeech tts;
    boolean focus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Spinner exam_dropdown = findViewById(R.id.exam_dropdown);
        final Spinner subj_dropdown = findViewById(R.id.subj_dropdown);
        final Spinner paper_dropdown = findViewById(R.id.paper_dropdown);
        // item 0 is always empty to ensure that the user choose from the list
        final String[] list_exam = new String[]{getResources().getString(R.string.text_select), getResources().getString(R.string.text_exam_hkdse)};
        final String[][] list_subj = new String[][]{
                {},
                {
                    getResources().getString(R.string.text_select),
                    getResources().getString(R.string.text_subject_hkdse_chin),
                    getResources().getString(R.string.text_subject_hkdse_eng),
                    getResources().getString(R.string.text_subject_hkdse_math),
                    getResources().getString(R.string.text_subject_hkdse_ls),
                    getResources().getString(R.string.text_subject_hkdse_m2),
                    getResources().getString(R.string.text_subject_hkdse_ict),
                    getResources().getString(R.string.text_subject_hkdse_phy),
                    getResources().getString(R.string.text_subject_hkdse_chem),
                    getResources().getString(R.string.text_subject_hkdse_bio),
                    getResources().getString(R.string.text_subject_hkdse_econ),
                    getResources().getString(R.string.text_subject_hkdse_bafs),
                    getResources().getString(R.string.text_subject_hkdse_geog),
                    getResources().getString(R.string.text_subject_hkdse_chist),
                    getResources().getString(R.string.text_subject_hkdse_hist),
                    getResources().getString(R.string.text_subject_hkdse_clit),
                    getResources().getString(R.string.text_subject_hkdse_elit),
                    getResources().getString(R.string.text_subject_hkdse_dat),
                    getResources().getString(R.string.text_subject_hkdse_tl)
                }
        };
        final String[][][] list_paper = new String[][][]{
                {{}},
                {
                    {},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_chin1), getResources().getString(R.string.text_paper_hkdse_chin2), getResources().getString(R.string.text_paper_hkdse_chin3), getResources().getString(R.string.text_paper_hkdse_chin4)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_eng1), getResources().getString(R.string.text_paper_hkdse_eng2), getResources().getString(R.string.text_paper_hkdse_eng3), getResources().getString(R.string.text_paper_hkdse_eng4)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_math1), getResources().getString(R.string.text_paper_hkdse_math2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_ls1), getResources().getString(R.string.text_paper_hkdse_ls2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_m2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_ict1), getResources().getString(R.string.text_paper_hkdse_ict2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_phy1), getResources().getString(R.string.text_paper_hkdse_phy2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_chem1), getResources().getString(R.string.text_paper_hkdse_chem2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_bio1), getResources().getString(R.string.text_paper_hkdse_bio2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_econ1), getResources().getString(R.string.text_paper_hkdse_econ2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_bafs1), getResources().getString(R.string.text_paper_hkdse_bafs2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_geog1), getResources().getString(R.string.text_paper_hkdse_geog2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_chist1), getResources().getString(R.string.text_paper_hkdse_chist2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_hist1), getResources().getString(R.string.text_paper_hkdse_hist2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_clit1), getResources().getString(R.string.text_paper_hkdse_clit2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_elit1), getResources().getString(R.string.text_paper_hkdse_elit2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_dat1), getResources().getString(R.string.text_paper_hkdse_dat2)},
                    {getResources().getString(R.string.text_select), getResources().getString(R.string.text_paper_hkdse_tl1), getResources().getString(R.string.text_paper_hkdse_tl2)},
                }
        };
        final int[][][] list_time = new int[][][]{
                {{}},
                {
                    {},
                    {-1, 5400000, 5400000, 4500000, 900000}, // chin
                    {-1, 5400000, 7200000, 4500000, 480000}, // eng
                    {-1, 8100000, 4500000}, // math
                    {-1, 7200000, 4500000}, // ls
                    {-1, 9000000}, // m1/m2
                    {-1, 7200000, 5400000}, // ict
                    {-1, 9000000, 3600000}, // phy
                    {-1, 9000000, 3600000}, // chem
                    {-1, 9000000, 3600000}, // bio
                    {-1, 3600000, 8100000}, // econ
                    {-1, 4500000, 8100000}, // bafs
                    {-1, 9000000, 4500000}, // geog
                    {-1, 8100000, 4800000}, // chist
                    {-1, 7200000, 5400000}, // hist
                    {-1, 7200000, 7200000}, // clit
                    {-1, 10800000, 7200000}, // elit
                    {-1, 7200000, 7200000}, // dat
                    {-1, 5400000, 7200000} // t&l
                }
        };
        final String[][][] list_exam_dates = new String[][][]{
                {{}},
                {
                    {},
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1523233800000L), gen_exam_date(1523241900000L), gen_exam_date(1523322900000L), gen_exam_date(-1L)}, // chin
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1523579400000L), gen_exam_date(1523588400000L), gen_exam_date(1523668500000L), gen_exam_date(-1L)}, // eng
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1523838600000L), gen_exam_date(1523849400000L)}, // math
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1523406600000L), gen_exam_date(1523416500000L)}, // ls
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524875400000L)}, // m1/m2
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1523061000000L), gen_exam_date(1523070900000L)}, // ict
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524011400000L), gen_exam_date(1524023100000L)}, // phy
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524443400000L), gen_exam_date(1524455100000L)}, // chem
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1522974600000L), gen_exam_date(1522986300000L)}, // bio
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1525048200000L), gen_exam_date(1525054500000L)}, // econ
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524616200000L), gen_exam_date(1524623400000L)}, // bafs
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524789000000L), gen_exam_date(1524800700000L)}, // geog
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524789000000L), gen_exam_date(1524799800000L)}, // chist
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1524270600000L), gen_exam_date(1524280500000L)}, // hist
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1522715400000L), gen_exam_date(1522725300000L)}, // clit
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1522801800000L), gen_exam_date(1522819800000L)}, // elit
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1522801800000L), gen_exam_date(1522811700000L)}, // dat
                    {getResources().getString(R.string.text_exam_date_placeholder), gen_exam_date(1523925000000L), gen_exam_date(1523933100000L)} // t&l
                }
        };
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list_exam);
        //set the spinners adapter to the previously created one.
        exam_dropdown.setAdapter(adapter);
        exam_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //init_msToCount = msList[position];
                // selected exam, populate subj_dropdown
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, list_subj[position]);
                subj_dropdown.setAdapter(adapter);
                subj_dropdown.setEnabled(position > 0);
                paper_dropdown.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[0]));
                paper_dropdown.setEnabled(false);
                positions[0] = position;
                if (position == 0) {
                    //disable all
                    subj_dropdown.setEnabled(false);
                }
                onReset(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
        subj_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // selected subject, populate paper_dropdown
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, list_paper[positions[0]][position]);
                paper_dropdown.setAdapter(adapter);
                paper_dropdown.setEnabled((position > 0) && (positions[0] > 0));
                positions[1] = position;
                onReset(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
        paper_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // selected paper, populate timer
                positions[2] = position;
                if (position > 0) {
                    init_msToCount = list_time[positions[0]][positions[1]][positions[2]];
                    ((TextView) findViewById(R.id.exam_date)).setText(list_exam_dates[positions[0]][positions[1]][positions[2]]);
                    onReset(null);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
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
        try {
            final InputStream data = getAssets().open("data.json");
            DataLoader.load(data);
        } catch (IOException|JSONException e) {
            Toast.makeText(this, "Sorry, we are unable to read the necessary data. " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
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
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    public void onClick(final View v) {
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

    private String gen_exam_date(long t) {
        if (t < 0) {
            return getResources().getString(R.string.text_exam_date, getResources().getString(R.string.text_exam_date_variable), "");
        }
        Date d = new Date(t);
        return getResources().getString(R.string.text_exam_date, DateFormat.getDateFormat(this).format(d), DateFormat.getTimeFormat(this).format(d));
    }
}
