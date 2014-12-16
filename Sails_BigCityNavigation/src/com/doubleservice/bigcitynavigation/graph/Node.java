package com.doubleservice.bigcitynavigation.graph;



/**
 * Title:        Associate Prof.
 * Description:  Async Libraries for SOC Design
 * Copyright:    Copyright (c) 2000
 * Company:      Async VLSI System & Java Technoloyg Group, Tatung University
 * @author Fu-Chiung John Cheng
 * @version 1.0
 * @see <a href="GraphAlgorithms.html">GraphAlgorithms Class</a>
 * @see <a href="Edge.html">Edge Class</a>
 * @see <a href="Graph.html">Node Class</a>
 */







import java.util.*;

import java.util.BitSet;


public class Node implements java.io.Serializable {
  private Object data; // holding information other than ID
  private String id = ""; // id of a node
  private ArrayList inEdges = new ArrayList();  // indegree edges
  private ArrayList outEdges = new ArrayList(); // outdegree edges
  private Graph graph = null ; // the com.doubleservice.bigcitynavigation.graph to which this node belong

  // set and get id
  public String getID() { return id;}
  public void setID(String s) { id = s;}

  // get and set data
  public Object getData() { return data; }
  public void setData(Object dat) { data = dat; }

  // set and get com.doubleservice.bigcitynavigation.graph
  public Graph getGraph() { return graph; }
  public void setGraph(Graph g) { graph = g;}

  // add and get indegree Edges
  public ArrayList getInEdges() { return inEdges; }
  public void addInEdge(Edge edge) { inEdges.add(edge);}
  public void addAllInEdge(Collection edges) {inEdges.addAll(edges);}
  public void removeInEdge(Edge edge) { inEdges.remove(edge); }
  public void removeAllInEdges() {inEdges.clear();}

  // membership
  public boolean memberOfOutEdges(Edge edge) {
    return getOutEdges().indexOf(edge)>=0;
  }
  public boolean memberOfInEdges(Edge edge) {
    return getInEdges().indexOf(edge)>=0;
  }

  // add and get outdegree Edges
  public ArrayList getOutEdges() {return outEdges; }
  public void addOutEdge(Edge edge) { outEdges.add(edge);}
  public void addAllOutEdge(Collection edges) {outEdges.addAll(edges);}
  public void removeOutEdge(Edge edge) { outEdges.remove(edge); }
  public void removeAllOutEdges() {outEdges.clear();}

  public ArrayList getEdges() {
    ArrayList allEdges = new ArrayList();
    allEdges.addAll(inEdges);
    allEdges.addAll(outEdges);
    return allEdges;
  }

  // in and out degree
  public int inDegree() { return  inEdges.size(); }
  public int outDegree(){ return outEdges.size(); }
  public int degree() {return inEdges.size() + outEdges.size();}

  // remove node not needed since GC will do the job
  public void removeEdges() {
    this.inEdges.clear();
    this.outEdges.clear();
  }

  // constructors
  public Node() { }
  public Node(String s) {setID(s);}
  public Node(String s, Object data) {setID(s); setData(data);}

  // print and println
  public void print() {
    System.out.print("Node("+id+")");
  }

  public void println() {
    print();
    System.out.print("\n");
  }

  // print and println
  void print(String prompt) {
    System.out.print(prompt);
    print();
  }

  public void println(String prompt) {
    System.out.print(prompt);
    println();
  }


} // end of Node class



