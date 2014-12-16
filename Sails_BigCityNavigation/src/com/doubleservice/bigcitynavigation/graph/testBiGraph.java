package com.doubleservice.bigcitynavigation.graph;



/**
 * Title:        Associate Prof.
 * Description:  Async Libraries for SOC Design
 * Copyright:    Copyright (c) 2000
 * Company:      Async VLSI System & Java Technoloyg Group, Tatung University
 * @author Fu-Chiung John Cheng
 * @version 1.0
 */

import java.util.ArrayList;

import java.util.Stack;

import java.util.LinkedList;


public class testBiGraph {

  //Main method
  public static void main(String[] args) {
    BiGraph g = new BiGraph();
    System.out.println("==========================================");
    System.out.println("Test1 output:");
    test1(g);
    g.removeAll();
    g.print();

   /* System.out.println("==========================================");
    System.out.println("\nTest2 output:");
    test2(g);
    g.removeAll();
    g.print();
    System.out.println("==========================================");
  */
  }


  // test set 1: test basic functions
  static void test1(BiGraph g) {
    // testing nodes and edges creating
    Node node1=g.newNode("node 1");
    Node node2=g.newNode("node 2");
    g.newEdge(node1, node2, "edge 1->2");
    g.print();
    Node node3=g.newNode("node 3");
    g.newEdge(node3, node1, "edge 3->1");
    g.newEdge(node1, g.newNode("node 4"), "edge 1->4");
    g.print();
    // test adjacentNodes
    System.out.println("==========================================");
    ArrayList adjNodes=g.adjacentNode(node1);
    System.out.println("node 1's adjacent nodes:");
    for (int i = 0; i < adjNodes.size();i++)
      ((Node) adjNodes.get(i)).println();
    adjNodes=g.adjacentNode(node2);
    System.out.println("node 2's adjacent nodes:");
    for (int i = 0; i < adjNodes.size();i++)
      ((Node) adjNodes.get(i)).println();
    adjNodes=g.adjacentNode(node3);
    System.out.println("node 3's adjacent nodes:");
    for (int i = 0; i < adjNodes.size();i++)
      ((Node) adjNodes.get(i)).println();

    // test inDegree and outDegree
    Node aNode;
    System.out.println("==========================================");
    ArrayList nodes=g.getNodes();
    for (int i = 0; i< nodes.size(); i++) {
      aNode = (Node) nodes.get(i);
      System.out.println(i+". indegree="+aNode.inDegree()+" outDegree="+aNode.outDegree());
    }
  }  // end of test1



  // test GraphAlgorithm class: Depth First Search and Breadth First Search
  static void test2(BiGraph g) {
    Node node1=g.newNode("node 1");
    Node node2=g.newNode("node 2");
    Node node3=g.newNode("node 3");
    Node node4=g.newNode("node 4");
    Node node5=g.newNode("node 5");
    Node node6=g.newNode("node 6");
    Node node7=g.newNode("node 7");
    Node node8=g.newNode("node 8");
    Node node9=g.newNode("node 9");
    Node node10=g.newNode("node 10");
    Node node11=g.newNode("node 11");

    g.newEdge(node1, node2, "edge 1->2");
    g.newEdge(node1, node3, "edge 1->3");
    g.newEdge(node2, node4, "edge 2->4");
    g.newEdge(node2, node5, "edge 2->5");
    g.newEdge(node3, node6, "edge 3->6");
    g.newEdge(node3, node7, "edge 3->7");
    g.newEdge(node4, node8, "edge 4->8");
    g.newEdge(node4, node9, "edge 4->9");
    g.newEdge(node2, node6, "edge 2->6");
    g.newEdge(node6, node10, "edge 6->10");
    g.newEdge(node6, node11, "edge 6->11");
    g.print();
    
    // BFS
    ArrayList bfs=GraphAlgorithms.breadthFirstSearch(g, node1);
    System.out.println("===================BFS=======================");
    for (int i=0; i<bfs.size(); i++) {
      ((Node) bfs.get(i)).println();
    }

    // DFS
    ArrayList dfs=GraphAlgorithms.depthFirstSearch(g, node1);
    System.out.println("===================DFS=======================");
    for (int i=0; i<dfs.size(); i++) {
      ((Node) dfs.get(i)).println();
    }
  } // end of test2
}

