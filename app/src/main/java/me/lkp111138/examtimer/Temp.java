package me.lkp111138.examtimer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 2/6/18.
 */

public class Temp {
    public static void fuck(String[] list_exam, String[][] list_subj, String[][][] list_paper, int[][][] list_time, long[][][] list_exam_date, AppCompatActivity context) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("exams", new JSONArray());
        JSONArray exams = data.getJSONArray("exams");
        for (int i = 1; i < list_exam.length; ++i) {
            JSONObject exam = new JSONObject();
            exam.put("name", list_exam[i]);
            exam.put("abbr", "");
            JSONArray subjects = new JSONArray();
            for (int j = 1; j < list_subj[i].length; ++j) {
                JSONObject subject = new JSONObject();
                subject.put("name", list_subj[i][j]);
                JSONArray papers = new JSONArray();
                for (int k = 1; k < list_paper[i][j].length; ++k) {
                    JSONObject paper = new JSONObject();
                    paper.put("name", list_paper[i][j][k]);
                    paper.put("limit", list_time[i][j][k]);
                    paper.put("examdate", list_exam_date[i][j][k]);
                    papers.put(paper);
                }
                subject.put("papers", papers);
                subjects.put(subject);
            }
            exam.put("subjects", subjects);
            exams.put(exam);
        }
        data.put("exams", exams);
        try {
            String path = Environment.getExternalStorageDirectory().getPath() + "/data-en.json";
            File f = new File(path);
            f.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(data.toString(2).getBytes());
            Toast.makeText(context, "Written to " + path, Toast.LENGTH_SHORT);
        } catch (IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
