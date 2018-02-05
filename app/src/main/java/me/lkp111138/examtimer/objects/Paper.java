package me.lkp111138.examtimer.objects;

/**
 * Created by user on 2/5/18.
 */

class Paper {
    private int id; // 0x7cfxxyyz
    private String name;
    private Subject parent;
    private int timeLimit; // in ms

    private Paper() {
    }

    Paper(Subject subject, String name, int timeLimit) {
        this.name = name;
        this.parent = subject;
        this.timeLimit = timeLimit;
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
}
