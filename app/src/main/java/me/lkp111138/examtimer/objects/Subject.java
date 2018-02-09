package me.lkp111138.examtimer.objects;

import java.util.ArrayList;

/**
 * Created by user on 2/5/18.
 */

public class Subject implements StandardObject {
    private int nextPaperId = 1;
    private int id; // 0x7cfxxyy0
    private ArrayList<Paper> papers = new ArrayList<>();
    private String name;
    private Exam parent;
    private boolean hidden = false;
    private boolean ttsEnabled;

    private Subject() {
    }

    Subject(Exam exam, String name, boolean tts) {
        this.name = name;
        this.parent = exam;
        this.ttsEnabled = tts;
        this.id = exam.getId() + (exam.getNextSubjectId() << 4);
    }

    Paper addPaper(String name, int timeLimit, long at, Boolean tts) {
        if (tts == null) {
            tts = ttsEnabled;
        }
        Paper p = new Paper(this, name, timeLimit, at, tts);
        this.papers.add(p);
        return p;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Paper> getPapers() {
        return papers;
    }

    Paper[] getVisiblePapers() {
        ArrayList<Paper> list = new ArrayList<>();
        for (Paper paper : papers) {
            if (!paper.isHidden()) {
                list.add(paper);
            }
        }
        return list.toArray(new Paper[0]);
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
}
