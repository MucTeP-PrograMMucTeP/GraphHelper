package com.example.graphhelper;

import androidx.annotation.NonNull;

public class Vector2D {
    public double x, y;
    public Vector2D (double x, double y){
        this.x = x;
        this.y = y;
    }
    public double scalarProduct (Vector2D other){
        return x * other.x + y * other.y;
    }
    public double crossProduct (Vector2D other){
        return x * other.y - y * other.x;
    }
    public void rotate (double angle){
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;
        x = newX;
        y = newY;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" + x + ", " + y + "}";
    }
}
