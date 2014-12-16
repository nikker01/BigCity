package com.doubleservice.bigcitynavigation.graph;



/**
 * Title:        Associate Prof.
 * Description:  Async Libraries for SOC Design
 * Copyright:    Copyright (c) 2000
 * Company:      Async VLSI System & Java Technoloyg Group, Tatung University
 * @author Fu-Chiung John Cheng
 * @version 1.0
 * Date: 2000/11/9
 * @see <a href="GraphAlgorithms.html">GraphAlgorithms Class</a>
 * @see <a href="Graph.html">Edge Class</a>
 * @see <a href="Node.html">Node Class</a>
 **/


import java.util.ArrayList;

// edges class
public class Edge implements java.io.Serializable {
  private String id = ""; // ex. E1, E2, ....
  private Object data;    // holding domain specific data
  private Node sourceNode;
  private Node targetNode;

  // get and set info
  public String getID() { return id; }
  public void setID(String s) { id = s; }

  // get and set data
  public Object getData() { return data; }
  public void setData(Object dat) { data = dat; }

  // get sourceNode
  public Node getSourceNode() { return sourceNode; }
  public void setSourceNode(Node node) {
    if (sourceNode!=null)                // remove this edge of out edge of original source node
      sourceNode.removeOutEdge(this);

    sourceNode = node;
    if (!sourceNode.memberOfOutEdges(this))  // if this edge is not an outedge of the sourceNode add it
      sourceNode.addOutEdge(this);
  }



  // get targetNode
  public Node getTargetNode() { return targetNode; }
  public void setTargetNode(Node node) {
    if (targetNode!=null)      // remove "this" edge from the outEdges of original source node
      targetNode.removeInEdge(this);

    targetNode = node;
    if (!targetNode.memberOfInEdges(this))  // if "this" edge is not a member of inEdges then add it
      targetNode.addInEdge(this);
  }



  // return the com.doubleservice.bigcitynavigation.graph
  public Graph getGraph() { return sourceNode.getGraph(); }

  // constructor
  public Edge() {sourceNode = null; targetNode = null; }
  public Edge(Node sNode, Node tNode, String id, Object data) {
    sourceNode = sNode;
    targetNode = tNode;
    setID(id);
    setData(data);
    sNode.addOutEdge(this);
    tNode.addInEdge(this);
  }  // end of Edge()

 void print() {
    System.out.print("Edge("+id+":");
    if (sourceNode != null) sourceNode.print();
    System.out.print("->");
    if (targetNode != null) targetNode.print();
    System.out.print(")");
  }

  public void println() {
    print();
    System.out.print("\n");
  }

} // end of Edge Class



