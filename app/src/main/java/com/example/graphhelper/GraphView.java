package com.example.graphhelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GraphView extends SurfaceView implements SurfaceHolder.Callback {

    ArrayList <Edge> dirEdgeList;
    ArrayList <Edge> undirEdgeList;
    HashMap<Integer, Pair <Integer, Integer>> dirNodeCoord;
    HashMap<Integer, Pair <Integer, Integer>> undirNodeCoord;
    ArrayList <Integer> path;
    ArrayList <Integer> dirIsDelNode;
    ArrayList <Integer> undirIsDelNode;
    boolean weighed = false, directed = false;
    double koeff = 1, oldKoeff = 1;
    Thread thread;
    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case (R.id.direction_switch):
                    GraphActivity.directed = isChecked;
                    break;
                case (R.id.weight_switch):
                    GraphActivity.weighed = isChecked;
                    break;
            }
            GraphActivity.path.clear();
            thread = new Thread(() -> {
                dirEdgeList = GraphActivity.dirEdgeList;
                undirEdgeList = GraphActivity.undirEdgeList;
                dirNodeCoord = GraphActivity.dirNodeCoord;
                undirNodeCoord = GraphActivity.undirNodeCoord;
                path = GraphActivity.path;
                dirIsDelNode = GraphActivity.dirIsDelNode;
                undirIsDelNode = GraphActivity.undirIsDelNode;
                directed = GraphActivity.directed;
                weighed = GraphActivity.weighed;
                Canvas canvas = getHolder().lockCanvas();
                Paint paint = new Paint();
                canvas.drawColor(Color.WHITE);
                if (directed){
                    paint.setStrokeWidth((int) (20 * koeff));
                    paint.setTextSize((int) (40 * koeff));
                    paint.setColor(Color.rgb(125, 249, 255));
                    for (int i = 0; i < dirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                    }
                    paint.setColor(Color.rgb(255, 192, 203));
                    for (int i = 0; i < path.size() - 1; ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                    }
                    if (weighed){
                        paint.setColor(Color.BLACK);
                        for (int i = 0; i < dirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                            int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                            canvas.drawText(String.valueOf(dirEdgeList.get(i).weight), midX, midY, paint);
                        }
                    }
                    for (int i = 1; i < dirIsDelNode.size(); ++i){
                        if (dirIsDelNode.get(i) == 0){
                            int x12 = Objects.requireNonNull(dirNodeCoord.get(i)).first;
                            int y12 = Objects.requireNonNull(dirNodeCoord.get(i)).second;
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                        }
                    }
                }
                else{
                    paint.setStrokeWidth((int) (20 * koeff));
                    paint.setTextSize((int) (40 * koeff));
                    paint.setColor(Color.rgb(125, 249, 255));
                    for (int i = 0; i < undirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                    }
                    paint.setColor(Color.rgb(255, 192, 203));
                    for (int i = 0; i < path.size() - 1; ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                    }
                    if (weighed){
                        paint.setColor(Color.BLACK);
                        for (int i = 0; i < undirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                            int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                            canvas.drawText(String.valueOf(undirEdgeList.get(i).weight), midX, midY, paint);
                        }
                    }
                    for (int i = 1; i < undirIsDelNode.size(); ++i){
                        if (undirIsDelNode.get(i) == 0){
                            int x12 = Objects.requireNonNull(undirNodeCoord.get(i)).first;
                            int y12 = Objects.requireNonNull(undirNodeCoord.get(i)).second;
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                        }
                    }
                }
                getHolder().unlockCanvasAndPost(canvas);
            });
            thread.start();
        }
    };
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case (R.id.upButton):
                    if (directed){
                        for (int i = 1; i < dirIsDelNode.size(); ++i){
                            if (dirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).second;
                                GraphActivity.dirNodeCoord.put(i, new Pair<>(x, y - 200));
                            }
                        }
                    }
                    else{
                        for (int i = 1; i < undirIsDelNode.size(); ++i){
                            if (undirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).second;
                                GraphActivity.undirNodeCoord.put(i, new Pair<>(x, y - 200));
                            }
                        }
                    }
                    break;
                case (R.id.downButton):
                    if (directed){
                        for (int i = 1; i < dirIsDelNode.size(); ++i){
                            if (dirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).second;
                                GraphActivity.dirNodeCoord.put(i, new Pair<>(x, y + 200));
                            }
                        }
                    }
                    else{
                        for (int i = 1; i < undirIsDelNode.size(); ++i){
                            if (undirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).second;
                                GraphActivity.undirNodeCoord.put(i, new Pair<>(x, y + 200));
                            }
                        }
                    }
                    break;
                case (R.id.leftButton):
                    if (directed){
                        for (int i = 1; i < dirIsDelNode.size(); ++i){
                            if (dirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).second;
                                GraphActivity.dirNodeCoord.put(i, new Pair<>(x - 200, y));
                            }
                        }
                    }
                    else{
                        for (int i = 1; i < undirIsDelNode.size(); ++i){
                            if (undirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).second;
                                GraphActivity.undirNodeCoord.put(i, new Pair<>(x - 200, y));
                            }
                        }
                    }
                    break;
                case (R.id.rightButton):
                    if (directed){
                        for (int i = 1; i < dirIsDelNode.size(); ++i){
                            if (dirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).second;
                                GraphActivity.dirNodeCoord.put(i, new Pair<>(x + 200, y));
                            }
                        }
                    }
                    else{
                        for (int i = 1; i < undirIsDelNode.size(); ++i){
                            if (undirIsDelNode.get(i) == 0){
                                int x = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).first;
                                int y = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).second;
                                GraphActivity.undirNodeCoord.put(i, new Pair<>(x + 200, y));
                            }
                        }
                    }
                    break;
            }
            thread = new Thread(() -> {
                dirEdgeList = GraphActivity.dirEdgeList;
                undirEdgeList = GraphActivity.undirEdgeList;
                dirNodeCoord = GraphActivity.dirNodeCoord;
                undirNodeCoord = GraphActivity.undirNodeCoord;
                path = GraphActivity.path;
                dirIsDelNode = GraphActivity.dirIsDelNode;
                undirIsDelNode = GraphActivity.undirIsDelNode;
                directed = GraphActivity.directed;
                weighed = GraphActivity.weighed;
                Canvas canvas = getHolder().lockCanvas();
                Paint paint = new Paint();
                canvas.drawColor(Color.WHITE);
                if (directed){
                    paint.setStrokeWidth((int) (20 * koeff));
                    paint.setTextSize((int) (40 * koeff));
                    paint.setColor(Color.rgb(125, 249, 255));
                    for (int i = 0; i < dirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                    }
                    paint.setColor(Color.rgb(255, 192, 203));
                    for (int i = 0; i < path.size() - 1; ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                    }
                    if (weighed){
                        paint.setColor(Color.BLACK);
                        for (int i = 0; i < dirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                            int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                            canvas.drawText(String.valueOf(dirEdgeList.get(i).weight), midX, midY, paint);
                        }
                    }
                    for (int i = 1; i < dirIsDelNode.size(); ++i){
                        if (dirIsDelNode.get(i) == 0){
                            int x12 = Objects.requireNonNull(dirNodeCoord.get(i)).first;
                            int y12 = Objects.requireNonNull(dirNodeCoord.get(i)).second;
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                        }
                    }
                }
                else{
                    paint.setStrokeWidth((int) (20 * koeff));
                    paint.setTextSize((int) (40 * koeff));
                    paint.setColor(Color.rgb(125, 249, 255));
                    for (int i = 0; i < undirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                    }
                    paint.setColor(Color.rgb(255, 192, 203));
                    for (int i = 0; i < path.size() - 1; ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                    }
                    if (weighed){
                        paint.setColor(Color.BLACK);
                        for (int i = 0; i < undirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                            int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                            canvas.drawText(String.valueOf(undirEdgeList.get(i).weight), midX, midY, paint);
                        }
                    }
                    for (int i = 1; i < undirIsDelNode.size(); ++i){
                        if (undirIsDelNode.get(i) == 0){
                            int x12 = Objects.requireNonNull(undirNodeCoord.get(i)).first;
                            int y12 = Objects.requireNonNull(undirNodeCoord.get(i)).second;
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                        }
                    }
                }
                getHolder().unlockCanvasAndPost(canvas);
            });
            thread.start();
        }
    };

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX(), y = (int) event.getY();
        if (GraphActivity.dirIsDelNode.isEmpty()){
            GraphActivity.dirIsDelNode.add(0);
            GraphActivity.undirIsDelNode.add(0);
        }
        if (GraphActivity.directed){
            int index = GraphActivity.dirIsDelNode.indexOf(1);
            if (index == -1){
                index = GraphActivity.dirIsDelNode.size();
                GraphActivity.dirIsDelNode.add(0);
            }
            else{
                GraphActivity.dirIsDelNode.set(index, 0);
            }
            GraphActivity.dirNodeCoord.put(index, new Pair<>(x, y));
        }
        else{
            int index = GraphActivity.undirIsDelNode.indexOf(1);
            if (index == -1){
                index = GraphActivity.undirIsDelNode.size();
                GraphActivity.undirIsDelNode.add(0);
            }
            else{
                GraphActivity.undirIsDelNode.set(index, 0);
            }
            GraphActivity.undirNodeCoord.put(index, new Pair<>(x, y));
        }
        thread = new Thread(() -> {
            dirEdgeList = GraphActivity.dirEdgeList;
            undirEdgeList = GraphActivity.undirEdgeList;
            dirNodeCoord = GraphActivity.dirNodeCoord;
            undirNodeCoord = GraphActivity.undirNodeCoord;
            path = GraphActivity.path;
            dirIsDelNode = GraphActivity.dirIsDelNode;
            undirIsDelNode = GraphActivity.undirIsDelNode;
            directed = GraphActivity.directed;
            weighed = GraphActivity.weighed;
            Canvas canvas = getHolder().lockCanvas();
            Paint paint = new Paint();
            canvas.drawColor(Color.WHITE);
            if (directed){
                paint.setStrokeWidth((int) (20 * koeff));
                paint.setTextSize((int) (40 * koeff));
                paint.setColor(Color.rgb(125, 249, 255));
                for (int i = 0; i < dirEdgeList.size(); ++i){
                    int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                    int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                    int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                    int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                    canvas.drawLine(x1, y1, x2, y2, paint);
                    calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                }
                paint.setColor(Color.rgb(255, 192, 203));
                for (int i = 0; i < path.size() - 1; ++i){
                    int x1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).first;
                    int y1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).second;
                    int x2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).first;
                    int y2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).second;
                    canvas.drawLine(x1, y1, x2, y2, paint);
                    calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                }
                if (weighed){
                    paint.setColor(Color.BLACK);
                    for (int i = 0; i < dirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                        int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                        canvas.drawText(String.valueOf(dirEdgeList.get(i).weight), midX, midY, paint);
                    }
                }
                for (int i = 1; i < dirIsDelNode.size(); ++i){
                    if (dirIsDelNode.get(i) == 0){
                        int x12 = Objects.requireNonNull(dirNodeCoord.get(i)).first;
                        int y12 = Objects.requireNonNull(dirNodeCoord.get(i)).second;
                        paint.setColor(Color.GREEN);
                        canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                        paint.setColor(Color.BLACK);
                        canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                    }
                }
            }
            else{
                paint.setStrokeWidth((int) (20 * koeff));
                paint.setTextSize((int) (40 * koeff));
                paint.setColor(Color.rgb(125, 249, 255));
                for (int i = 0; i < undirEdgeList.size(); ++i){
                    int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                    int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                    int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                    int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
                paint.setColor(Color.rgb(255, 192, 203));
                for (int i = 0; i < path.size() - 1; ++i){
                    int x1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).first;
                    int y1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).second;
                    int x2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).first;
                    int y2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).second;
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
                if (weighed){
                    paint.setColor(Color.BLACK);
                    for (int i = 0; i < undirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                        int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                        canvas.drawText(String.valueOf(undirEdgeList.get(i).weight), midX, midY, paint);
                    }
                }
                for (int i = 1; i < undirIsDelNode.size(); ++i){
                    if (undirIsDelNode.get(i) == 0){
                        int x12 = Objects.requireNonNull(undirNodeCoord.get(i)).first;
                        int y12 = Objects.requireNonNull(undirNodeCoord.get(i)).second;
                        paint.setColor(Color.GREEN);
                        canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                        paint.setColor(Color.BLACK);
                        canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                    }
                }
            }
            getHolder().unlockCanvasAndPost(canvas);
        });
        thread.start();
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        GraphActivity.upButton.setOnClickListener(clickListener);
        GraphActivity.downButton.setOnClickListener(clickListener);
        GraphActivity.leftButton.setOnClickListener(clickListener);
        GraphActivity.rightButton.setOnClickListener(clickListener);
        GraphActivity.dirSwitch.setOnCheckedChangeListener(changeListener);
        GraphActivity.wSwitch.setOnCheckedChangeListener(changeListener);
        koeff = (double) GraphActivity.seekBar.getProgress() / ((double) GraphActivity.seekBar.getMax() / 2);

        GraphActivity.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                oldKoeff = koeff;
                koeff = (double) Math.max(progress, 5) / ((double) seekBar.getMax() / 2);

                if (directed){
                    for (int i = 1; i < dirIsDelNode.size(); ++i){
                        if (dirIsDelNode.get(i) == 0){
                            int x = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).first;
                            int y = Objects.requireNonNull(GraphActivity.dirNodeCoord.get(i)).second;
                            GraphActivity.dirNodeCoord.put(i, new Pair<>((int)(x / oldKoeff * koeff),
                                    (int)(y / oldKoeff * koeff)));
                        }
                    }
                }
                else{
                    for (int i = 1; i < undirIsDelNode.size(); ++i){
                        if (undirIsDelNode.get(i) == 0){
                            int x = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).first;
                            int y = Objects.requireNonNull(GraphActivity.undirNodeCoord.get(i)).second;
                            GraphActivity.undirNodeCoord.put(i, new Pair<>((int)(x / oldKoeff * koeff),
                                    (int)(y / oldKoeff * koeff)));
                        }
                    }
                }

                thread = new Thread(() -> {
                    dirEdgeList = GraphActivity.dirEdgeList;
                    undirEdgeList = GraphActivity.undirEdgeList;
                    dirNodeCoord = GraphActivity.dirNodeCoord;
                    undirNodeCoord = GraphActivity.undirNodeCoord;
                    path = GraphActivity.path;
                    dirIsDelNode = GraphActivity.dirIsDelNode;
                    undirIsDelNode = GraphActivity.undirIsDelNode;
                    directed = GraphActivity.directed;
                    weighed = GraphActivity.weighed;
                    Canvas canvas = getHolder().lockCanvas();
                    Paint paint = new Paint();
                    canvas.drawColor(Color.WHITE);
                    if (directed){
                        paint.setStrokeWidth((int) (20 * koeff));
                        paint.setTextSize((int) (40 * koeff));
                        paint.setColor(Color.rgb(125, 249, 255));
                        for (int i = 0; i < dirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                            canvas.drawLine(x1, y1, x2, y2, paint);
                            calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                        }
                        paint.setColor(Color.rgb(255, 192, 203));
                        for (int i = 0; i < path.size() - 1; ++i){
                            int x1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).first;
                            int y1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).second;
                            int x2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).first;
                            int y2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).second;
                            canvas.drawLine(x1, y1, x2, y2, paint);
                            calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                        }
                        if (weighed){
                            paint.setColor(Color.BLACK);
                            for (int i = 0; i < dirEdgeList.size(); ++i){
                                int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                                int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                                int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                                int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                                int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                                canvas.drawText(String.valueOf(dirEdgeList.get(i).weight), midX, midY, paint);
                            }
                        }
                        for (int i = 1; i < dirIsDelNode.size(); ++i){
                            if (dirIsDelNode.get(i) == 0){
                                int x12 = Objects.requireNonNull(dirNodeCoord.get(i)).first;
                                int y12 = Objects.requireNonNull(dirNodeCoord.get(i)).second;
                                paint.setColor(Color.GREEN);
                                canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                                paint.setColor(Color.BLACK);
                                canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                            }
                        }
                    }
                    else{
                        paint.setStrokeWidth((int) (20 * koeff));
                        paint.setTextSize((int) (40 * koeff));
                        paint.setColor(Color.rgb(125, 249, 255));
                        for (int i = 0; i < undirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                            canvas.drawLine(x1, y1, x2, y2, paint);
                        }
                        paint.setColor(Color.rgb(255, 192, 203));
                        for (int i = 0; i < path.size() - 1; ++i){
                            int x1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).first;
                            int y1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).second;
                            int x2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).first;
                            int y2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).second;
                            canvas.drawLine(x1, y1, x2, y2, paint);
                        }
                        if (weighed){
                            paint.setColor(Color.BLACK);
                            for (int i = 0; i < undirEdgeList.size(); ++i){
                                int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                                int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                                int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                                int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                                int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                                canvas.drawText(String.valueOf(undirEdgeList.get(i).weight), midX, midY, paint);
                            }
                        }
                        for (int i = 1; i < undirIsDelNode.size(); ++i){
                            if (undirIsDelNode.get(i) == 0){
                                int x12 = Objects.requireNonNull(undirNodeCoord.get(i)).first;
                                int y12 = Objects.requireNonNull(undirNodeCoord.get(i)).second;
                                paint.setColor(Color.GREEN);
                                canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                                paint.setColor(Color.BLACK);
                                canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                            }
                        }
                    }
                    getHolder().unlockCanvasAndPost(canvas);
                });
                thread.start();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        thread = new Thread(){
            @Override
            public void run() {
                dirEdgeList = GraphActivity.dirEdgeList;
                undirEdgeList = GraphActivity.undirEdgeList;
                dirNodeCoord = GraphActivity.dirNodeCoord;
                undirNodeCoord = GraphActivity.undirNodeCoord;
                path = GraphActivity.path;
                dirIsDelNode = GraphActivity.dirIsDelNode;
                undirIsDelNode = GraphActivity.undirIsDelNode;
                directed = GraphActivity.directed;
                weighed = GraphActivity.weighed;
                Canvas canvas = getHolder().lockCanvas();
                Paint paint = new Paint();
                canvas.drawColor(Color.WHITE);
                if (directed){
                    paint.setStrokeWidth((int) (20 * koeff));
                    paint.setTextSize((int) (40 * koeff));
                    paint.setColor(Color.rgb(125, 249, 255));
                    for (int i = 0; i < dirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                    }
                    paint.setColor(Color.rgb(255, 192, 203));
                    for (int i = 0; i < path.size() - 1; ++i){
                        int x1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).first;
                        int y1 = Objects.requireNonNull(dirNodeCoord.get(path.get(i))).second;
                        int x2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).first;
                        int y2 = Objects.requireNonNull(dirNodeCoord.get(path.get(i + 1))).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        calculateAndDrawPointer(x1, y1, x2, y2, canvas, paint);
                    }
                    if (weighed){
                        paint.setColor(Color.BLACK);
                        for (int i = 0; i < dirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(dirNodeCoord.get(dirEdgeList.get(i).finishNode)).second;
                            int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                            canvas.drawText(String.valueOf(dirEdgeList.get(i).weight), midX, midY, paint);
                        }
                    }
                    for (int i = 1; i < dirIsDelNode.size(); ++i){
                        if (dirIsDelNode.get(i) == 0){
                            int x12 = Objects.requireNonNull(dirNodeCoord.get(i)).first;
                            int y12 = Objects.requireNonNull(dirNodeCoord.get(i)).second;
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                        }
                    }
                }
                else{
                    paint.setStrokeWidth((int) (20 * koeff));
                    paint.setTextSize((int) (40 * koeff));
                    paint.setColor(Color.rgb(125, 249, 255));
                    for (int i = 0; i < undirEdgeList.size(); ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                    }
                    paint.setColor(Color.rgb(255, 192, 203));
                    for (int i = 0; i < path.size() - 1; ++i){
                        int x1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).first;
                        int y1 = Objects.requireNonNull(undirNodeCoord.get(path.get(i))).second;
                        int x2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).first;
                        int y2 = Objects.requireNonNull(undirNodeCoord.get(path.get(i + 1))).second;
                        canvas.drawLine(x1, y1, x2, y2, paint);
                    }
                    if (weighed){
                        paint.setColor(Color.BLACK);
                        for (int i = 0; i < undirEdgeList.size(); ++i){
                            int x1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).first;
                            int y1 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).startNode)).second;
                            int x2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).first;
                            int y2 = Objects.requireNonNull(undirNodeCoord.get(undirEdgeList.get(i).finishNode)).second;
                            int midX = (x1 + x2) / 2, midY = (y1 + y2) / 2;
                            canvas.drawText(String.valueOf(undirEdgeList.get(i).weight), midX, midY, paint);
                        }
                    }
                    for (int i = 1; i < undirIsDelNode.size(); ++i){
                        if (undirIsDelNode.get(i) == 0){
                            int x12 = Objects.requireNonNull(undirNodeCoord.get(i)).first;
                            int y12 = Objects.requireNonNull(undirNodeCoord.get(i)).second;
                            paint.setColor(Color.GREEN);
                            canvas.drawCircle(x12, y12, (int) (50 * koeff), paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawText(String.valueOf(i), x12 - (int)(9 * koeff), y12 + (int)(10 * koeff), paint);
                        }
                    }
                }
                getHolder().unlockCanvasAndPost(canvas);
            }
        };
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {}

    public void calculateAndDrawPointer (double sx, double sy, double fx, double fy, Canvas canvas, Paint paint){
        double midX = (sx + fx) / 2;
        double midY = (sy + fy) / 2;
        Vector2D horizontal = new Vector2D(1, 0);
        Vector2D edge = new Vector2D(fx - sx, fy - sy);
        Vector2D left = new Vector2D(-60 * koeff, 30 * koeff);
        Vector2D right = new Vector2D(-60 * koeff, -30 * koeff);
        double angle = Math.atan2(horizontal.crossProduct(edge), horizontal.scalarProduct(edge));
        left.rotate(angle);
        right.rotate(angle);
        double leftEndX = midX + left.x, leftEndY = midY + left.y;
        double rightEndX = midX + right.x, rightEndY = midY + right.y;
        paint.setStrokeWidth((int) (10 * koeff));
        canvas.drawLine( (float) midX, (float) midY, (float) leftEndX, (float) leftEndY, paint);
        canvas.drawLine( (float) midX, (float) midY, (float) rightEndX, (float) rightEndY, paint);
        paint.setStrokeWidth((int) (20 * koeff));
    }
}
