package me.lkp111138.examtimer.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import me.lkp111138.examtimer.MainActivity;
import me.lkp111138.examtimer.R;

/**
 * Created by user on 2/5/18.
 */

public final class DataLoader {
    private static ArrayList<Exam> exams = new ArrayList<>();
    private static ArrayList<Exam> visibleExams = null;
    private static SparseArray<StandardObject> objects = new SparseArray<>();
    private static MainActivity context = null;
    private static int[] positions = {0, 0, 0};
    private static SharedPreferences pref;
    private DataLoader() {
    }

    public static void load(InputStream data, MainActivity _context) throws IOException, JSONException {
        context = _context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = data.read()) > 0) {
            sb.append((char) c);
        }
        JSONObject obj = new JSONObject(sb.toString());
        JSONArray _exams = obj.getJSONArray("exams");
        for (int i = 0; i < _exams.length(); ++i) {
            JSONObject _exam = _exams.getJSONObject(i);
            JSONObject _tts_text = _exam.getJSONObject("tts_text");
            Map<String, String[]> tts_text = new HashMap<>();
            String key;
            Iterator<String> it = _tts_text.keys();
            while (it.hasNext()) {
                key = it.next();
                JSONArray a = _tts_text.getJSONArray(key);
                tts_text.put(key, new String[]{a.getString(0), a.getString(1), a.getString(2)});
            }
            Exam exam = new Exam(_exam.getString("name"), _exam.getString("abbr"), _exam.getBoolean("tts"), tts_text);
            JSONArray subjects = _exam.getJSONArray("subjects");
            for (int j = 0; j < subjects.length(); ++j) {
                JSONObject subject = subjects.getJSONObject(j);
                Subject s = exam.addSubject(subject.getString("name"), subject.optBoolean("tts", exam.isTtsEnabled()));
                objects.append(s.getId(), s);
                JSONArray papers = subject.getJSONArray("papers");
                for (int k = 0; k < papers.length(); ++k) {
                    JSONObject paper = papers.getJSONObject(k);
                    Paper p = s.addPaper(paper.getString("name"), paper.getInt("limit"), paper.getLong("examdate"), paper.optBoolean("tts", s.isTtsEnabled()));
                    objects.append(p.getId(), p);
                }
            }
            exams.add(exam);
            objects.append(exam.getId(), exam);
        }

        // init
        init();

        final Spinner exam_dropdown = context.findViewById(R.id.exam_dropdown);
        final Spinner subj_dropdown = context.findViewById(R.id.subj_dropdown);
        final Spinner paper_dropdown = context.findViewById(R.id.paper_dropdown);
        paper_dropdown.setEnabled(false);
        subj_dropdown.setEnabled(false);

        // init handlers
        paper_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // selected paper, populate timer
                positions[2] = position;
                if (position == 0) {
                    return;
                }
                DataLoader.onItemSelected(2, positions);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
        subj_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // selected paper, populate timer
                positions[1] = position;
                if (position == 0) {
                    Spinner paper_dropdown = context.findViewById(R.id.paper_dropdown);
                    paper_dropdown.setEnabled(false);
                    paper_dropdown.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[0]));
                    return;
                }
                DataLoader.onItemSelected(1, positions);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
        exam_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // selected paper, populate timer
                positions[0] = position;
                if (position == 0) {
                    Spinner subj_dropdown = context.findViewById(R.id.subj_dropdown);
                    Spinner paper_dropdown = context.findViewById(R.id.paper_dropdown);
                    paper_dropdown.setEnabled(false);
                    paper_dropdown.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[0]));
                    subj_dropdown.setEnabled(false);
                    subj_dropdown.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[0]));
                    return;
                }
                DataLoader.onItemSelected(0, positions);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });
        final Context context = _context;
        exam_dropdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(!exam_dropdown.isEnabled()){
                        Toast.makeText(context, "Configure this at Settings > Personalization", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static void init() {
        String exam_selected = pref.getString("my_exam", "");
        if (!exam_selected.equals("")) {
            int a = Integer.parseInt(exam_selected);
            // selected an exam so show it only
            for (Exam exam : exams) {
                if (exam.getId() != a) {
                    exam.hide();
                }
            }
            // check subjects
            Set<String> subjects = pref.getStringSet("my_subjects", null);
            if (subjects != null) {
                // clone or data corruption
                subjects = new HashSet<>(subjects);
                // hide as appropriate
                Exam exam = getExams()[0];
                Subject[] subjects1 = exam.getVisibleSubjects();
                for (Subject subject : subjects1) {
                    // loop thru the set
                    subject.hide();
                    for (String subj_id : subjects) {
                        if (subject.getId() == Integer.parseInt(subj_id)) {
                            subject.unhide();
                            // remove from subjects cuz we dont need to loop it
                            subjects.remove(subj_id);
                            break;
                        }
                    }
                }
            }
        }

        final Spinner exam_dropdown = context.findViewById(R.id.exam_dropdown);
        final Spinner subj_dropdown = context.findViewById(R.id.subj_dropdown);
        final Spinner paper_dropdown = context.findViewById(R.id.paper_dropdown);
        ArrayAdapter<String> exam_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, getExamStrings());
        exam_dropdown.setAdapter(exam_adapter);
        ArrayAdapter<String> subj_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, getSubjectStrings(positions[0] - 1));
        subj_dropdown.setAdapter(subj_adapter);
        ArrayAdapter<String> paper_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, getPaperStrings(positions[0] - 1, positions[1] - 1));
        paper_dropdown.setAdapter(paper_adapter);
    }

    /* get all visible exams */
    @NonNull
    public static Exam[] getExams() {
        ArrayList<Exam> list = new ArrayList<>();
        for (Exam exam : exams) {
            if (!exam.isHidden()) {
                list.add(exam);
            }
        }
        return list.toArray(new Exam[0]);
    }

    private static String[] getExamStrings() {
        Exam[] exams = getExams();
        String[] strings = new String[1 + exams.length];
        for (int i = 0; i < exams.length; ++i) {
            strings[i + 1] = exams[i].getName();
        }
        strings[0] = context.getResources().getString(R.string.text_select);
        return strings;
    }

    /* get all visible subjects from exam with visible index i */
    static Subject[] getSubjects(int i) {
        if (i < 0) {
            return new Subject[0];
        }
        Exam exam = getExams()[i];
        return exam.getVisibleSubjects();
    }

    private static String[] getSubjectStrings(int i) {
        Subject[] subjects = getSubjects(i);
        String[] strings = new String[1 + subjects.length];
        for (int ii = 0; ii < subjects.length; ++ii) {
            strings[ii + 1] = subjects[ii].getName();
        }
        strings[0] = context.getResources().getString(R.string.text_select);
        return strings;
    }

    /* get all visible subjects from subject with visible index j in (exam with visible index i) */
    static Paper[] getPapers(int i, int j) {
        if (i < 0 || j < 0) {
            return new Paper[0];
        }
        Subject subject = getSubjects(i)[j];
        return subject.getVisiblePapers();
    }

    private static String[] getPaperStrings(int i, int j) {
        Paper[] papers = getPapers(i, j);
        String[] strings = new String[1 + papers.length];
        for (int ii = 0; ii < papers.length; ++ii) {
            strings[ii + 1] = papers[ii].getName();
        }
        strings[0] = context.getResources().getString(R.string.text_select);
        return strings;
    }

    public static void onItemSelected(int which, int[] positions) {
        System.out.printf("which = %d, pos = %d,%d,%d\n", which, positions[0], positions[1], positions[2]);
        // selected subject, populate paper_dropdown
        Spinner exam_dropdown = context.findViewById(R.id.exam_dropdown);
        Spinner subj_dropdown = context.findViewById(R.id.subj_dropdown);
        Spinner paper_dropdown = context.findViewById(R.id.paper_dropdown);
        if (which < 0) {
            ArrayAdapter<String> exam_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, getExamStrings());
            exam_dropdown.setAdapter(exam_adapter);
        }
        if (which < 1) {
            ArrayAdapter<String> subj_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, getSubjectStrings(positions[0] - 1));
            subj_dropdown.setAdapter(subj_adapter);
        }
        if (which < 2) {
            ArrayAdapter<String> paper_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, getPaperStrings(positions[0] - 1, positions[1] - 1));
            paper_dropdown.setAdapter(paper_adapter);
        }
        switch (which) { // 0=exam, 1=subj, 2=paper
            case -1:
                paper_dropdown.setEnabled(false);
                subj_dropdown.setEnabled(false);
                break;
            case 0:
                paper_dropdown.setEnabled(false);
                subj_dropdown.setEnabled(true);
                break;
            case 1:
                paper_dropdown.setEnabled(true);
                break;
            case 2:
                // update stuff
                Paper selected = getPapers(positions[0] - 1, positions[1] - 1)[positions[2] - 1];
                context.setInit_msToCount(selected.getTimeLimit(), new LOL());
                context.setPaper(selected, new LOL());
                ((TextView) context.findViewById(R.id.exam_date)).setText(context.gen_exam_date(selected.getAt()));
                context.onReset(null);
        }
    }

    public static StandardObject getObject(int id) {
        return objects.get(id);
    }

    public static int[] getPositions() {
        return positions;
    }

    public static class LOL {
        private LOL() {
        }

        public void fuckme() {

        }
    }
}
