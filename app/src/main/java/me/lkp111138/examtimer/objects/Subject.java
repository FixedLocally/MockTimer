package me.lkp111138.examtimer.objects;

import java.util.ArrayList;

/**
 * Created by user on 2/5/18.
 */

class Subject {
    private int nextPaperId = 1;
    private int id; // 0x7cfxxyy0
    private ArrayList<Paper> papers = new ArrayList<>();
    private String name;
    private Exam parent;

    private Subject() {
    }

    Subject(Exam exam, String name) {
        this.name = name;
        this.parent = exam;
        this.id = exam.getId() + (exam.getNextSubjectId() << 4);
    }

    public Subject addPaper(String name, int timeLimit) {
        Paper p = new Paper(this, name, timeLimit);
        this.papers.add(p);
        return this;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Paper> getPapers() {
        return papers;
    }

    public String getName() {
        return name;
    }

    public Exam getParent() {
        return parent;
    }

    int getNextPaperId() {
        return nextPaperId;
    }
}
