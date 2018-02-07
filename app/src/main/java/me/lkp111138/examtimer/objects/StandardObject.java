package me.lkp111138.examtimer.objects;

/**
 * Created by user on 2/6/18.
 */

interface StandardObject {
    boolean isHidden();
    int getId();
    String getName();
    void hide();
    void unhide();
}
