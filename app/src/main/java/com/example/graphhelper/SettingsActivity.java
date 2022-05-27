package com.example.graphhelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    ArrayList <Edge> edgeList = new ArrayList<>();
    HashMap<Integer, Pair<Integer, Integer>> nodeCoord = new HashMap<>();
    ArrayList<Integer> isDelNode = new ArrayList<>();
    ArrayList <Integer> path = new ArrayList<>();
    View.OnClickListener clickListener;
    Button delNodeButton, addEdgeButton, delEdgeButton, searchButton;
    ImageButton backButton;
    EditText delNodeText, addEdgeText, delEdgeText, searchText;
    boolean directed;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        delNodeButton = findViewById(R.id.delete_node_button);
        addEdgeButton = findViewById(R.id.add_edge_button);
        delEdgeButton = findViewById(R.id.delete_edge_button);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);
        delNodeText = findViewById(R.id.delete_node_text);
        addEdgeText = findViewById(R.id.add_edge_text);
        delEdgeText = findViewById(R.id.delete_edge_text);
        searchText = findViewById(R.id.search_text);

        Bundle args = getIntent().getExtras();
        directed = args.getBoolean("directed");
        edgeList = decodeEdgeList(args.getString("edgeList"));
        nodeCoord = decodeCoord(args.getString("nodeCoord"));
        isDelNode = args.getIntegerArrayList("isDelNode");

        clickListener = v -> {
            String[] temp;
            switch (v.getId()){
                case (R.id.delete_node_button):
                    String newNode = delNodeText.getText().toString();
                    if (checkNode(newNode)){
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    temp = newNode.split("\n");
                    for (String str : temp){
                        int node = Integer.parseInt(str);
                        isDelNode.set(node, 1);
                        nodeCoord.remove(node);
                    }
                    for (int i = 0; i < edgeList.size(); ++i){
                        if (isDelNode.get(edgeList.get(i).startNode) == 1 ||
                            isDelNode.get(edgeList.get(i).finishNode) == 1){
                            edgeList.remove(i);
                            --i;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Успех!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case (R.id.add_edge_button):
                    String newEdge = addEdgeText.getText().toString();
                    if (!checkAddedEdge(newEdge)){
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    temp = newEdge.split("\n");
                    for (String str : temp){
                        String[] temp2 = str.split(" ");
                        int sn = Integer.parseInt(temp2[0]);
                        int fn = Integer.parseInt(temp2[1]);
                        int w = (temp2.length == 3? Integer.parseInt(temp2[2]) : 0);
                        if (find(new Edge(sn, fn, w)) != -1 || !directed && find(new Edge(fn, sn, w)) != -1)
                            continue;
                        edgeList.add(new Edge(sn, fn, w));
                    }
                    Toast.makeText(getApplicationContext(), "Успех!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case (R.id.delete_edge_button):
                    newEdge = delEdgeText.getText().toString();
                    if (!checkDeletedEdge(newEdge)){
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    temp = newEdge.split("\n");
                    for (String str : temp){
                        String[] temp2 = str.split(" ");
                        int sn = Integer.parseInt(temp2[0]);
                        int fn = Integer.parseInt(temp2[1]);
                        int w = 0;
                        int index = find(new Edge(sn, fn , w));
                        if (index != -1)
                            edgeList.remove(index);
                        if (!directed){
                            index = find(new Edge(fn, sn, w));
                            if (index != -1)
                                edgeList.remove(index);
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Успех!",
                            Toast.LENGTH_SHORT).show();
                    break;
                case (R.id.search_button):
                    String newPath = searchText.getText().toString();
                    temp = newPath.split(" ");
                    if (temp.length != 2 || checkNode(temp[0]) || checkNode(temp[1])){
                        Toast.makeText(getApplicationContext(), "Что-то пошло не так!",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Pair <int[][][], int[][]> tempPair = FloydAlgorithm();
                    int[][] next = tempPair.second;
                    int sn = Integer.parseInt(temp[0]), fn = Integer.parseInt(temp[1]);
                    if (next[sn][fn] == -1) {
                        Toast.makeText(getApplicationContext(), "Ни одного пути не найдено",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        while (sn != fn){
                            path.add(sn);
                            sn = next[sn][fn];
                        }
                        path.add(fn);
                        Toast.makeText(getApplicationContext(), "Путь проложен на главном экране",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case (R.id.back_button):
                    Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
                    intent.putIntegerArrayListExtra("path", path);
                    intent.putIntegerArrayListExtra("isDelNode", isDelNode);
                    intent.putExtra("nodeCoord", codeCoord(nodeCoord));
                    intent.putExtra("edgeList", codeEdgeList(edgeList));
                    intent.putExtra("directed", directed);
                    intent.putExtra("className", "settings");
                    startActivity(intent);
                    break;
            }
        };

        delNodeButton.setOnClickListener(clickListener);
        addEdgeButton.setOnClickListener(clickListener);
        delEdgeButton.setOnClickListener(clickListener);
        searchButton.setOnClickListener(clickListener);
        backButton.setOnClickListener(clickListener);
    }

    boolean checkNode(String s){
        for (int i = 0; i < s.length(); ++i){
            if ((s.charAt(i) < '0' || s.charAt(i) > '9') && s.charAt(i) != '\n')
                return true;
        }
        String[] temp = s.split("\n");
        for (String str : temp){
            if (str.isEmpty() || str.charAt(0) == '0')
                return true;
            int number = Integer.parseInt(str);
            if (number > isDelNode.size() || isDelNode.get(number) == 1)
                return true;
        }
        return false;
    }
    boolean checkAddedEdge(String s){
        for (int i = 0; i < s.length(); ++i){
            if ((s.charAt(i) < '0' || s.charAt(i) > '9') && s.charAt(i) != ' ' && s.charAt(i) != '\n')
                return false;
        }
        String[] temp = s.split("\n");
        for (String str : temp){
            String[] temp2 = str.split(" ");
            if (temp2.length < 2 || temp2.length > 3 || checkNode(temp2[0]) || checkNode(temp2[1]))
                return false;
        }
        return true;
    }
    boolean checkDeletedEdge(String s){
        for (int i = 0; i < s.length(); ++i){
            if ((s.charAt(i) < '0' || s.charAt(i) > '9') && s.charAt(i) != ' ' && s.charAt(i) != '\n')
                return false;
        }
        String[] temp = s.split("\n");
        for (String str : temp){
            String[] temp2 = str.split(" ");
            if (temp2.length != 2 || checkNode(temp2[0]) || checkNode(temp2[1]))
                return false;
        }
        return true;
    }
    int find(Edge newEdge){
        for (int i = 0; i < edgeList.size(); ++i){
            if (newEdge.equals(edgeList.get(i)))
                return i;
        }
        return -1;
    }
    Pair <int[][][], int[][]> FloydAlgorithm(){
        int sz = isDelNode.size();
        int[][][] dist = new int[sz][sz][sz];
        int[][] next = new int[sz][sz];
        for (int k = 0; k < sz; ++k){
            for (int i = 0; i < sz; ++i){
                for (int j = 0; j < sz; ++j){
                    dist[k][i][j] = -1;
                    next[i][j] = -1;
                    if (i == j) dist[k][i][j] = 0;
                }
            }
        }
        for (int i = 0; i < edgeList.size(); ++i){
            int sn = edgeList.get(i).startNode;
            int fn = edgeList.get(i).finishNode;
            int w = edgeList.get(i).weight;
            if (dist[0][sn][fn] == -1 || dist[0][sn][fn] > w){
                dist[0][sn][fn] = w;
                next[sn][fn] = fn;
                if (!directed){
                    dist[0][fn][sn] = w;
                    next[fn][sn] = sn;
                }
            }
        }
        for (int k = 1; k < sz; ++k){
            for (int i = 1; i < sz; ++i){
                for (int j = 1; j < sz; ++j){
                    if (dist[k-1][i][k] != -1 && dist[k-1][k][j] != -1
                            && (dist[k-1][i][j] == -1 || dist[k-1][i][j] >
                            dist[k-1][i][k] + dist[k-1][k][j])){

                        dist[k][i][j] = dist[k-1][i][k] + dist[k-1][k][j];
                        next[i][j] = next[i][k];
                        if (!directed) next[j][i] = next[j][k];
                    } else{
                        dist[k][i][j] = dist[k-1][i][j];
                    }
                }
            }
        }
        return new Pair<>(dist, next);
    }
    String codeEdgeList(ArrayList <Edge> arrayList){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arrayList.size(); ++i){
            str.append(arrayList.get(i).startNode).append(" ").append(arrayList.get(i).finishNode).append(" ").append(arrayList.get(i).weight);
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
            str.append(entry.getKey()).append(" ").append(entry.getValue().first).append(" ").append(entry.getValue().second).append("&");
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
}
