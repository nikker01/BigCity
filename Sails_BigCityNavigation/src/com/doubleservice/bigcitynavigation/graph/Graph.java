package com.doubleservice.bigcitynavigation.graph;



/**
 * Title:        Associate Prof.
 * Description:  Async Libraries for SOC Design
 * Copyright:    Copyright (c) 2000
 * Company:      Async VLSI System & Java Technoloyg Group, Tatung University
 * @author Fu-Chiung John Cheng
 * @version 1.1
 * Date: 2000/11/9
 * @see <a href="Graph.html">Graph Class</a>
 * @see <a href="GraphAlgorithms.html">GraphAlgorithms Class</a>
 * @see <a href="Edge.html">Edge Class</a>
 * @see <a href="Node.html">Node Class</a>
 **/

import java.util.ArrayList;

/**
 * Graph class
 * @author Fu-Chiung John Cheng, Dept. of Computer Science and Engineering, Tatung University
 * @version: 1.1
 * Date: 2000/10/7
 * @see <a href="GraphAlgorithms.html">GraphAlgorithms Class</a>
 * @see <a href="Edge.html">Edge Class</a>
 * @see <a href="Node.html">Node Class</a>
 **/



// Graph class
public class Graph implements java.io.Serializable {
  /**
   * The edges of a com.doubleservice.bigcitynavigation.graph
   **/
  public ArrayList edges = new ArrayList();

  //protected ArrayList rectEdges = new ArrayList();

  /**
   * The nodes of a com.doubleservice.bigcitynavigation.graph
   **/
  protected ArrayList nodes = new ArrayList();

  /**
   * get the list of adjacent nodes of node
   * @param     node  a node in a com.doubleservice.bigcitynavigation.graph.
   * @return    list of adjacent nodes.
   **/

  public ArrayList adjacentNodes(Node node) {
    ArrayList adjcentNodes = new ArrayList();
    ArrayList adjcentEdges = node.getOutEdges();
    Edge aEdge;
    for (int i=0; i< adjcentEdges.size(); i++) {
      aEdge = ((Edge)adjcentEdges.get(i));
      adjcentNodes.add(aEdge.getTargetNode());
    }

    adjcentEdges = node.getInEdges();
    for (int i=0; i< adjcentEdges.size(); i++) {
      aEdge = ((Edge)adjcentEdges.get(i));
      adjcentNodes.add(aEdge.getSourceNode());
    }
    return adjcentNodes;
  } // endof adjcentNodes



  /**
   * Return a list of adjacent edges
   **/

  public ArrayList adjacentEdges(Node node) { return node.getEdges(); }


  public int numberOfNodes() { return nodes.size(); }
  public int numberOfEdges() { return edges.size(); }
  public ArrayList getNodes() { return nodes; }
  public ArrayList getEdges() { return edges; }


  /**
   * Create a new node
   * @param     String id       node id
   **/
   public Node newNode(String id) {
     return newNode(id, null);
  }


  /**
   * Create a new node
   * @param     String id       node id
   * @param     Object data     Domain Specific Data.
   **/
   public Node newNode(String id, Object data) {
     Node node = new Node(id, data);
     node.setGraph(this);
     nodes.add(node);
     return node;
  }




  /**
   * Create a new edge
   * @param     Node sNode       source node
   * @param     Node tNode       target node
   * @param     String id        node id
   **/

  public Edge newEdge(Node sNode, Node tNode, String id) {
    return newEdge(sNode, tNode, id, null);
  }

  /**
   * Create a new edge
   * @param     Node sNode       source node
   * @param     Node tNode       target node
   * @param     String id        node id
   * @param     Object data      Domain Specific Data.
   **/
  public Edge newEdge(Node sNode, Node tNode, String id, Object data) {
    Edge edge = new Edge(sNode, tNode, id, data);
    edges.add(edge);
    return edge;
  }

  public Node getNode(int index) {
    Node node = (Node) nodes.get(index);
    return node;
  }

  public Edge getEdge(int index) {
    Edge edge = (Edge) edges.get(index);
    return edge;
  }

  public void add(Node node) {
    nodes.add(node);
  }

  public int biggestNodeID() {
    int id = Integer.parseInt(this.getNode(0).getID()), tmpID;
    for(int i=1; i< this.numberOfNodes(); i++) {
      tmpID = Integer.parseInt(this.getNode(i).getID());
      if(id<tmpID) {
        id = tmpID;
      }
    }
    return id;
  }

  public int biggestEdgeID() {
  int id = Integer.parseInt(this.getEdge(0).getID()), tmpID;
  for(int i=1; i< this.numberOfEdges(); i++) {
    tmpID = Integer.parseInt(this.getEdge(i).getID());
    if(id<tmpID) {
      id = tmpID;
    }
  }
  return id;
}

  public void add(Graph graph) {
    int nodeBase = this.biggestNodeID();
    int edgeBase = this.biggestEdgeID();

    ArrayList newNodes = graph.getNodes();
    ArrayList newEdges = graph.getEdges();

    for (int i = 0; i < newNodes.size(); i++) {
      String newID = Integer.toString(nodeBase + Integer.parseInt(((Node)newNodes.get(i)).getID()));
      ((Node)newNodes.get(i)).setID(newID);
//      Node n = this.newNode(newID,((Node)newNodes.get(i)).getData());
//      n.println();
    }

    for (int i = 0; i < newEdges.size(); i++) {
      String newID = Integer.toString(edgeBase + Integer.parseInt(((Edge)newEdges.get(i)).getID()));
      ((Edge)newEdges.get(i)).setID(newID);

//      Edge e = this.newEdge(sNode,tNode,newID,((Edge)newEdges.get(i)).getData());
//      e.println();
    }

    this.addNodes(newNodes);
    this.addEdges(newEdges);
  }

  public void addNodes(ArrayList aNodes) {
    nodes.addAll(aNodes);
  }

  public void addEdges(ArrayList aEdges) {
    edges.addAll(aEdges);
  }

  public void add(Edge edge) {
    edges.add(edge);
  }

  /**
   * remove a node from the com.doubleservice.bigcitynavigation.graph
   * @param node Node
   * @return boolean
   */
  public boolean remove(Node node) {
  boolean ret = nodes.remove(node);
    if (ret) {
      ArrayList edgesToBeDeleted = node.getEdges();
      for (int i=0;i<edgesToBeDeleted.size();i++) {
        ((Edge)edgesToBeDeleted.get(i)).getSourceNode().removeOutEdge((Edge)edgesToBeDeleted.get(i)); // add by Xavier
        ((Edge)edgesToBeDeleted.get(i)).getTargetNode().removeInEdge((Edge)edgesToBeDeleted.get(i));
        this.remove((Edge)edgesToBeDeleted.get(i));
      }
    }
    return ret;
  }

  /**
   * remove node from the com.doubleservice.bigcitynavigation.graph
   **/
  public void removeNode(int index) {
    Node node = (Node) nodes.get(index);
    remove(node);
  }

  /**
   * remove edge from the com.doubleservice.bigcitynavigation.graph
   **/
  public boolean remove(Edge edge) {
    edge.getSourceNode().removeOutEdge(edge);   // add by Xavier for remove edge list from source target node
    edge.getTargetNode().removeInEdge(edge);
    return edges.remove(edge);
  }

  /**
   * remove indexed-node from the com.doubleservice.bigcitynavigation.graph
   **/
  public void removeEdge(int index) {
    //edges.remove(index);
    Edge edge = (Edge)edges.get(index);     // change by Xavier
    remove(edge);
  }

  /**
   * remove all nodes (and edges) from the com.doubleservice.bigcitynavigation.graph
   **/
  public void removeNodes() {
    nodes.clear();
    removeEdges();
  }

  /**
   * remove all edges from the com.doubleservice.bigcitynavigation.graph
   **/
  public void removeEdges() {
    edges.clear();
  }

  /**
   * remove all nodes and edges from the com.doubleservice.bigcitynavigation.graph
   **/

  public void removeAll() {
    removeNodes();
    removeEdges();
  }

  /**
   * find a node base on data
   **/
  public Node find(Node node) {
    Object obj = node.getData();
    for (int i=0; i < nodes.size(); i++) {
      if (obj == ((Node) nodes.get(i)).getData()) {
        return (Node) nodes.get(i);
      }
    }
    return null;
  }



  /**
   * find a node base on id
   **/
  public Node find(String id) {
    Node node = null;
    for (int i=0; i < nodes.size(); i++) {
      node = ((Node) nodes.get(i));
      if (id.equalsIgnoreCase(node.getID())) {
        return node;
      }
    }
    return node;
  }



  // print, printNodes and printEdges

  /**
   * print all nodes
   **/
  public void printNodes() {
    for (int i=0; i < nodes.size(); i++) {
      ((Node) nodes.get(i)).println();
      System.out.print(" ");
    }
    System.out.println();
  }

  /**
   * print all edges
   **/
  public void printEdges() {
    for (int i=0; i < edges.size(); i++)
      ((Edge) edges.get(i)).println();
  }

  /**
   * print the com.doubleservice.bigcitynavigation.graph
   **/
  public void print() {
    System.out.print("Graph:\nNodes: "+ nodes.size()+"\n");
    printNodes();
    System.out.print("Edges: "+ edges.size()+"\n");
    printEdges();
  }

}  // end of Graph class



