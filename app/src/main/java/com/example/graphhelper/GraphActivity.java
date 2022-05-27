package com.example.graphhelper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static SeekBar seekBar;
    static SwitchCompat dirSwitch;
    static SwitchCompat wSwitch;
    static boolean weighed = false;
    static boolean directed = false;
    ImageButton tutorialButton, settingsButton;
    @SuppressLint("StaticFieldLeak")
    static ImageButton upButton, downButton, leftButton, rightButton;
    View.OnClickListener clickListener;
    static ArrayList <Edge> dirEdgeList = new ArrayList<>();
    static ArrayList <Edge> undirEdgeList = new ArrayList<>();
    static HashMap<Integer, Pair <Integer, Integer>> dirNodeCoord = new HashMap<>();
    static HashMap<Integer, Pair <Integer, Integer>> undirNodeCoord = new HashMap<>();
    static ArrayList <Integer> path = new ArrayList<>();
    static ArrayList <Integer> dirIsDelNode = new ArrayList<>();
    static ArrayList <Integer> undirIsDelNode = new ArrayList<>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_activity);

        seekBar = findViewById(R.id.seekbar);
        dirSwitch = findViewById(R.id.direction_switch);
        wSwitch = findViewById(R.id.weight_switch);
        tutorialButton = findViewById(R.id.tutorial_button);
        settingsButton = findViewById(R.id.settings_button);
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        preferences = getSharedPreferences("graph", MODE_PRIVATE);
        editor = preferences.edit();

        seekBar.setProgress(preferences.getInt("seekBarProgress", seekBar.getMax() / 2));
        Bundle args = getIntent().getExtras();
        if (args != null && args.getString("className", "").equals("settings")){
            path = args.getIntegerArrayList("path");
            directed = args.getBoolean("directed");
            weighed = preferences.getBoolean("weighed", false);
            if (directed){
                dirEdgeList = decodeEdgeList(args.getString("edgeList", ""));
                undirEdgeList = decodeEdgeList(preferences.getString("edgeList", ""));
                dirNodeCoord = decodeCoord(args.getString("nodeCoord", ""));
                undirNodeCoord = decodeCoord(preferences.getString("nodeCoord", ""));
                dirIsDelNode = args.getIntegerArrayList("isDelNode");
                undirIsDelNode = decodeArrayList(preferences.getString("isDelNode", ""));
            }
            else{
                dirEdgeList = decodeEdgeList(preferences.getString("edgeList", ""));
                undirEdgeList = decodeEdgeList(args.getString("edgeList"));
                dirNodeCoord = decodeCoord(preferences.getString("nodeCoord", ""));
                undirNodeCoord = decodeCoord(args.getString("nodeCoord", ""));
                dirIsDelNode = decodeArrayList(preferences.getString("isDelNode", ""));
                undirIsDelNode = args.getIntegerArrayList("isDelNode");
            }
        }
        else{
            dirNodeCoord = decodeCoord(preferences.getString("dirNodeCoord", ""));
            undirNodeCoord = decodeCoord(preferences.getString("undirNodeCoord", ""));
            dirIsDelNode = decodeArrayList(preferences.getString("dirIsDelNode", ""));
            undirIsDelNode = decodeArrayList(preferences.getString("undirIsDelNode", ""));
            directed = preferences.getBoolean("directed", false);
            weighed = preferences.getBoolean("weighed", false);
            dirEdgeList = decodeEdgeList(preferences.getString("dirEdgeList", ""));
            undirEdgeList = decodeEdgeList(preferences.getString("undirEdgeList", ""));
        }
        dirSwitch.setChecked(directed);
        wSwitch.setChecked(weighed);

        clickListener = v -> {
            Intent intent;
            editor.clear();
            switch (v.getId()) {
                case (R.id.tutorial_button):
                    editor.putBoolean("directed", directed);
                    editor.putBoolean("weighed", weighed);
                    editor.putString("dirEdgeList", codeEdgeList(dirEdgeList));
                    editor.putString("dirNodeCoord", codeCoord(dirNodeCoord));
                    editor.putString("dirIsDelNode", codeArrayList(dirIsDelNode));
                    editor.putString("undirEdgeList", codeEdgeList(undirEdgeList));
                    editor.putString("undirNodeCoord", codeCoord(undirNodeCoord));
                    editor.putString("undirIsDelNode", codeArrayList(undirIsDelNode));
                    editor.putInt("seekBarProgress", seekBar.getProgress());
                    editor.commit();
                    startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
                    break;
                case (R.id.settings_button):
                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    intent.putExtra("directed", directed);
                    editor.putBoolean("weighed", weighed);
                    editor.putInt("seekBarProgress", seekBar.getProgress());
                    if (directed){
                        intent.putExtra("edgeList", codeEdgeList(dirEdgeList));
                        intent.putExtra("nodeCoord", codeCoord(dirNodeCoord));
                        intent.putIntegerArrayListExtra("isDelNode", dirIsDelNode);
                        editor.putString("edgeList", codeEdgeList(undirEdgeList));
                        editor.putString("nodeCoord", codeCoord(undirNodeCoord));
                        editor.putString("isDelNode", codeArrayList(undirIsDelNode));
                    }
                    else{
                        intent.putExtra("edgeList", codeEdgeList(undirEdgeList));
                        intent.putExtra("nodeCoord", codeCoord(undirNodeCoord));
                        intent.putIntegerArrayListExtra("isDelNode", undirIsDelNode);
                        editor.putString("edgeList", codeEdgeList(dirEdgeList));
                        editor.putString("nodeCoord", codeCoord(dirNodeCoord));
                        editor.putString("isDelNode", codeArrayList(dirIsDelNode));
                    }
                    editor.commit();
                    startActivity(intent);
                    break;
            }
        };

        tutorialButton.setOnClickListener(clickListener);
        settingsButton.setOnClickListener(clickListener);
    }

    String codeEdgeList(ArrayList <Edge> arrayList){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arrayList.size(); ++i){
            str.append(arrayList.get(i).startNode).append(" ").append(arrayList.get(i).finishNode).
                    append(" ").append(arrayList.get(i).weight);
            if (i + 1 != arrayList.size())
                str.append("&");
        }
        return str.toString();
    }
    ArrayList <Edge> decodeEdgeList(String str){
        if (str.equals(""))
            return new ArrayList<>();
        ArrayList <Edge> arrayList = new ArrayList<>();
        String[] temp = str.split("&");
        for (String s : temp){
            String[] temp2 = s.split(" ");
            int sn = Integer.parseInt(temp2[0]), fn = Integer.parseInt(temp2[1]), w = Integer.parseInt(temp2[2]);
            arrayList.add(new Edge(sn, fn, w));
        }
        return arrayList;
    }
    String codeCoord(HashMap <Integer, Pair<Integer, Integer>> coord){
        StringBuilder str = new StringBuilder();
        for (Map.Entry <Integer, Pair <Integer, Integer>> entry : coord.entrySet()){
            str.append(entry.getKey()).append(" ").append(entry.getValue().first).append(" ").
                    append(entry.getValue().second).append("&");
        }
        return str.toString();
    }
    HashMap<Integer, Pair<Integer, Integer>> decodeCoord(String str){
        if (str.equals(""))
            return new HashMap<>();
        String[] temp = str.split("&");
        HashMap<Integer, Pair<Integer, Integer>> coord = new HashMap<>();
        for (String s : temp){
            if (s.isEmpty()) continue;
            String[] temp2 = s.split(" ");
            int key = Integer.parseInt(temp2[0]), fir = Integer.parseInt(temp2[1]), sec = Integer.parseInt(temp2[2]);
            coord.put(key, new Pair<>(fir, sec));
        }
        return coord;
    }
    String codeArrayList(ArrayList <Integer> arrayList){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arrayList.size(); ++i){
            str.append(arrayList.get(i));
            if (i + 1 != arrayList.size())
                str.append(" ");
        }
        return str.toString();
    }
    ArrayList <Integer> decodeArrayList(String str){
        if (str.equals(""))
            return new ArrayList<>();
        ArrayList <Integer> arrayList = new ArrayList<>();
        String[] temp = str.split(" ");
        for (String s : temp)
            arrayList.add(Integer.parseInt(s));
        return arrayList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.clear();
        editor.putString("dirEdgeList", codeEdgeList(dirEdgeList));
        editor.putString("undirEdgeList", codeEdgeList(undirEdgeList));
        editor.putBoolean("directed", directed);
        editor.putBoolean("weighed", weighed);
        editor.putString("dirNodeCoord", codeCoord(dirNodeCoord));
        editor.putString("undirNodeCoord", codeCoord(undirNodeCoord));
        editor.putString("dirIsDelNode", codeArrayList(dirIsDelNode));
        editor.putString("undirIsDelNode", codeArrayList(undirIsDelNode));
        editor.putInt("seekBarProgress", seekBar.getProgress());
        editor.commit();
    }
}