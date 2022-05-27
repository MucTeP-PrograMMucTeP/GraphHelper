package com.example.graphhelper;

class Edge {

    int startNode, finishNode, weight;
    protected Edge(int startNode, int finishNode, int weight){
        this.startNode = startNode;
        this.finishNode = finishNode;
        this.weight = weight;
    }

    protected boolean equals(Edge other){
        return startNode == other.startNode && finishNode == other.finishNode;
    }
}
