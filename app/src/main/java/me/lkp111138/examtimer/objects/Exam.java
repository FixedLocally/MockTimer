package me.lkp111138.examtimer.objects;

import java.util.ArrayList;

/**
 * Created by user on 2/5/18.
 */

public class Exam {
    private static int nextId = 1;
    private int nextSubjectId = 1;
    private int id; // 0x7cfxx000
    private ArrayList<Subject> subjects = new ArrayList<>();
    private String name;
    private String abbr;

    private Exam() {
    }

    public Exam(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
        this.id = 0x7cf00000 + (nextId << 12);
        ++nextId;
    }

    public Exam addSubject(String name) {
        Subject s = new Subject(this, name);
        this.subjects.add(s);
        return this;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
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
}
