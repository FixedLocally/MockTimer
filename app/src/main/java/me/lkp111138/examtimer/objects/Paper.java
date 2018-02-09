package me.lkp111138.examtimer.objects;

/**
 * Created by user on 2/5/18.
 */

public class Paper implements StandardObject {
    private int id; // 0x7cfxxyyz
    private String name;
    private Subject parent;
    private int timeLimit; // in ms
    private long at;
    private boolean hidden = false;
    private boolean ttsEnabled;

    private Paper() {
    }

    Paper(Subject subject, String name, int timeLimit, long at, boolean tts) {
        this.name = name;
        this.parent = subject;
        this.timeLimit = timeLimit;
        this.at = at;
        this.ttsEnabled = tts;
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

    public boolean isTtsEnabled() {
        return ttsEnabled;
    }
}
