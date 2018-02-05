package me.lkp111138.examtimer.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import me.lkp111138.examtimer.MainActivity;

/**
 * Created by user on 2/5/18.
 */

public final class DataLoader {
    private static ArrayList<Exam> exams;
    private DataLoader() {
    }

    public static void load(InputStream data) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = data.read()) > 0) {
            sb.append((char) c);
        }
        JSONObject obj = new JSONObject(sb.toString());
        // TODO: design json structur and logic here
    }
}
