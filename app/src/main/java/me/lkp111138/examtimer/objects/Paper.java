package me.lkp111138.examtimer.objects;

/**
 * Created by user on 2/5/18.
 */

class Paper implements StandardObject {
    private int id; // 0x7cfxxyyz
    private String name;
    private Subject parent;
    private int timeLimit; // in ms
    private long at;
    private boolean hidden = false;

    private Paper() {
    }

    Paper(Subject subject, String name, int timeLimit, long at) {
        this.name = name;
        this.parent = subject;
        this.timeLimit = timeLimit;
        this.at = at;
        this.id = subject.getId() + subject.getNextPaperId();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Subject getParent() {
        return parent;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public long getAt() {
        return at;
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
