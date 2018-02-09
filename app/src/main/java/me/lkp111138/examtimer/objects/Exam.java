package me.lkp111138.examtimer.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2/5/18.
 */

public class Exam implements StandardObject {
    private static int nextId = 1;
    private int nextSubjectId = 1;
    private int id; // 0x7cfxx000
    private ArrayList<Subject> subjects = new ArrayList<>();
    private String name;
    private String abbr;
    private boolean hidden;
    private boolean ttsEnabled;
    private Map<String, String[]> tts_text = new HashMap<>();

    private Exam() {
    }

    Exam(String name, String abbr, boolean tts, Map<String, String[]> tts_text) {
        this.name = name;
        this.abbr = abbr;
        this.ttsEnabled = tts;
        this.tts_text = tts_text;
        this.id = 0x7cf00000 + (nextId << 12);
        ++nextId;
    }

    Subject addSubject(String name, Boolean tts) {
        if (tts == null) {
            tts = ttsEnabled;
        }
        Subject s = new Subject(this, name, tts);
        this.subjects.add(s);
        ++nextSubjectId;
        return s;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    Subject[] getVisibleSubjects() {
        ArrayList<Subject> list = new ArrayList<>();
        for (Subject subject : subjects) {
            if (!subject.isHidden()) {
                list.add(subject);
            }
        }
        return list.toArray(new Subject[0]);
    }

    public String getName() {
        return name;
    }

    public String getAbbr() {
        return abbr;
    }

    int getNextSubjectId() {
        return nextSubjectId;
    }
    
    public void hide() {
        hidden = true;
    }

    public void unhide() {
        hidden = false;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isTtsEnabled() {
        return ttsEnabled;
    }

    public Map<String, String[]> getTtsText() {
        return tts_text;
    }
}
