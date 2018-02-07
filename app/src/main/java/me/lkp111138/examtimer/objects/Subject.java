package me.lkp111138.examtimer.objects;

import java.util.ArrayList;

/**
 * Created by user on 2/5/18.
 */

public class Subject implements StandardObject {
    private int nextPaperId = 1;
    private int id; // 0x7cfxxyy0
    private ArrayList<Paper> papers = new ArrayList<>();
    private ArrayList<Paper> visiblePapers = null;
    private String name;
    private Exam parent;
    private boolean hidden = false;

    private Subject() {
    }

    Subject(Exam exam, String name) {
        this.name = name;
        this.parent = exam;
        this.id = exam.getId() + (exam.getNextSubjectId() << 4);
    }

    public Paper addPaper(String name, int timeLimit, long at) {
        Paper p = new Paper(this, name, timeLimit, at);
        this.papers.add(p);
        return p;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Paper> getPapers() {
        return papers;
    }

    public Paper[] getVisiblePapers() {
        if (true || visiblePapers == null) {
            ArrayList<Paper> list = new ArrayList<>();
            for (Paper paper : papers) {
                if (!paper.isHidden()) {
                    list.add(paper);
                }
            }
            visiblePapers = list;
            return list.toArray(new Paper[0]);
        } else {
            return visiblePapers.toArray(new Paper[0]);
        }
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
}
