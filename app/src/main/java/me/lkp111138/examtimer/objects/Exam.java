package me.lkp111138.examtimer.objects;

import java.util.ArrayList;

/**
 * Created by user on 2/5/18.
 */

public class Exam implements StandardObject {
    private static int nextId = 1;
    private int nextSubjectId = 1;
    private int id; // 0x7cfxx000
    private ArrayList<Subject> subjects = new ArrayList<>();
    private ArrayList<Subject> visibleSubjects = null;
    private String name;
    private String abbr;
    private boolean hidden;

    private Exam() {
    }

    public Exam(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
        this.id = 0x7cf00000 + (nextId << 12);
        ++nextId;
    }

    public Subject addSubject(String name) {
        Subject s = new Subject(this, name);
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

    public Subject[] getVisibleSubjects() {
        if (true || visibleSubjects == null) {
            ArrayList<Subject> list = new ArrayList<>();
            for (Subject subject : subjects) {
                if (!subject.isHidden()) {
                    list.add(subject);
                }
            }
            visibleSubjects = list;
            return list.toArray(new Subject[0]);
        } else {
            return visibleSubjects.toArray(new Subject[0]);
        }
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
}
