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
import java.util.*;

/**
 * Graph class
 * @author Fu-Chiung John Cheng, Dept. of Computer Science and Engineering,
 * Tatung University
 * @version: 1.1 Date: 2000/10/7
 * @see <a href="GraphAlgorithms.html">GraphAlgorithms Class</a>
 * @see <a href="Edge.html">Edge Class</a>
 * @see <a href="Node.html">Node Class</a>
 */
// Graph class
public class BiGraph extends Graph {
  /**
   * get the list of adjacent child nodes of node
   * @param node
   * a node in a com.doubleservice.bigcitynavigation.graph.
   * @return list (ArrayList) of adjacent nodes.
   */
  public static ArrayList<Node> adjacentNode(Node node) {
    ArrayList<Edge> adjcentEdges = node.getEdges();
    ArrayList<Node> adjcentNodes = new ArrayList<Node>();
    Edge aEdge;
    for (int i = 0; i < adjcentEdges.size(); i++) {
        aEdge = adjcentEdges.get(i);
        
        if(aEdge.getTargetNode()==node)
      	  adjcentNodes.add(aEdge.getSourceNode());
        else
      	  adjcentNodes.add(aEdge.getTargetNode());
        //adjcentNodes.add(aEdge.getSourceNode());
      }
    return adjcentNodes;
  } // endof adjcentNodes

  /**
   * get the list of adjacent parent nodes of node
   * @param node
   * a node in a com.doubleservice.bigcitynavigation.graph.
   * @return list (ArrayList) of adjacent nodes.
   */
/*  public static ArrayList adjacentParentNodes(Node node) {
    ArrayList adjcentEdges = node.getEdges();
    ArrayList adjcentNodes = new ArrayList();
    Edge aEdge;
    for (int i = 0; i < adjcentEdges.size(); i++) {
      aEdge = ((Edge) adjcentEdges.get(i));
      adjcentNodes.add(aEdge.getSourceNode());
    }
    return adjcentNod s;
  } // endof adjcentNodes
*/
  /**
   * Return a list of adjacent edges
   */
  public ArrayList<Edge> adjacentEdges(Node node) {
    return node.getEdges();
  }

  /*public ArrayList adjacentOutEdges(Node node) {
    return node.getOutEdges();
  }

  public ArrayList adjacentInEdges(Node node) {
    return node.getInEdges();
  }
*/
 /* public int inDegree(Node node) {
    return node.inDegree();
  }

  public int outDegree(Node node) {
    return node.outDegree();
  }*/

  /**
   * get start nodes
   */
  public ArrayList<Node> getStartNodes() {
    ArrayList<Node> ret = new ArrayList<Node>();
    Node node;
    for (int i = 0; i < this.nodes.size(); i++) {
      node = (Node) nodes.get(i);
      if (node.inDegree() == 0)
        ret.add(node);
    }
    return ret;
  }

  /**
   * get end(sink) nodes
   */
  public ArrayList<Node> getSinkNodes() {
    ArrayList<Node> ret = new ArrayList<Node>();
    Node node;
    for (int i = 0; i < this.nodes.size(); i++) {
      node = (Node) nodes.get(i);
      if (node.outDegree() == 0)
        ret.add(node);
    }
    return ret;
  }

  /**
   * get Sub Graphs (note that com.doubleservice.bigcitynavigation.graph is not cloned)
   */
  static public ArrayList<BiGraph> getSubGraphs(BiGraph digraph) {
    ArrayList<BiGraph> subgraphs = new ArrayList<BiGraph>();
    ArrayList<Node> roots = digraph.getStartNodes();
    if (roots.size() == 1) {
      subgraphs.add(digraph);
      return subgraphs;
    }
    BiGraph dg;
    for (int i = 0; i < roots.size(); i++) {
      dg = BiGraph.getSubGraph(digraph, roots.get(i));
      subgraphs.add(dg);
    }
    return subgraphs;
  }

  /**
   * getSubGraph by a root node
   * @param dg
   * @param root
   * @return
   */
  public static BiGraph getSubGraph(BiGraph digraph, Node root) {
    BiGraph retdg = new BiGraph();
    Node aNode = root, tNode;
    Edge tempEdge;
    ArrayList<Node> visited = new ArrayList<Node>(); // result
    ArrayList adjChildNodes;
    ArrayList adjChildEdges;
    LinkedList<Node> toBeProcessed = new LinkedList<Node>(); // a queue
    toBeProcessed.add(root); // start from root node
    // bread first travasal in first in first out fashion
    while (!toBeProcessed.isEmpty()) {
      aNode = toBeProcessed.removeFirst(); // first in first out
      retdg.add(aNode);
      visited.add(aNode); // append
      // adjChildNodes = digraph.adjacentChildNodes(aNode);
      adjChildEdges = aNode.getOutEdges();
      for (int i = 0; i < adjChildEdges.size(); i++) {
        tempEdge = (Edge) adjChildEdges.get(i);
        tNode = (Node) tempEdge.getTargetNode();
        if (!visited.contains(tNode) && !toBeProcessed.contains(tNode)) {
          toBeProcessed.add(tNode); // add to the end
        } // end if
        retdg.add(tempEdge);
      } // end of for
    } // end of while
    return retdg;
  }

  /**
   * print all nodes
   */
  public void printNodes() {
    for (int i = 0; i < nodes.size(); i++) {
      ((Node) nodes.get(i)).print();
      System.out.println();
    }
    System.out.println();
  }

  /**
   * print all edges
   */
  public void printEdges() {
    for (int i = 0; i < edges.size(); i++)
      ((Edge) edges.get(i)).println();
  }

  /**
   * print the com.doubleservice.bigcitynavigation.graph
   */
  public void print() {
    System.out.print("Graph:\nNodes: " + nodes.size() + "\n");
    printNodes();
    System.out.print("Edges: " + edges.size() + "\n");
    printEdges();
  }

  /**
   * merge two nodes in the com.doubleservice.bigcitynavigation.graph
   */
  public void merge(Node sNode, Node tNode) {
    ArrayList tNodeInEdges = tNode.getInEdges();
    ArrayList tNodeOutEdges = tNode.getOutEdges();
    Edge inEdge, outEdge = null;
    for (int i = 0; i < tNodeInEdges.size(); i++) {
      inEdge = (Edge) tNodeInEdges.get(i);
      inEdge.setTargetNode(sNode);
    }
    for (int i = 0; i < tNodeOutEdges.size(); i++) {
      outEdge = (Edge) tNodeOutEdges.get(i);
      outEdge.setSourceNode(sNode);
    }
    this.remove(tNode);
  }

  /**
   * return the edges from sNode to tNode
   */
  public ArrayList getEdges(Node sNode, Node tNode) {
    // System.out.println("sNode =
    // "+((VisualNode)sNode).getShapeControl().getLabel());
    // System.out.println("tNode =
    // "+((VisualNode)tNode).getShapeControl().getLabel());
    ArrayList out = sNode.getOutEdges();
    ArrayList in = tNode.getInEdges();
    ArrayList edges = new ArrayList();
    for (int i = 0; i < out.size(); i++) {
      for (int j = 0; j < in.size(); j++) {
        if (out.get(i).equals(in.get(j))) {
          edges.add(in.get(j));
        }
      }
    }
    return edges;
  }

  public ArrayList<Edge> getConnectionEdge(Node nodeA, Node nodeB) {
    ArrayList<Edge> connectionEdges = new ArrayList<Edge>();
    ArrayList inEdgesOfnodeA = nodeA.getInEdges();
    ArrayList outEdgesOfnodeA = nodeA.getOutEdges();
    ArrayList inEdgesOfnodeB = nodeB.getInEdges();
    ArrayList outEdgesOfnodeB = nodeB.getOutEdges();
    Edge edge = new Edge();
    if ((inEdgesOfnodeA.size() == 0 || outEdgesOfnodeB.size() == 0) && (inEdgesOfnodeB.size() != 0 || outEdgesOfnodeA
            .size() != 0)) {
      boolean findEdge = false;
      for (int i = 0; i < outEdgesOfnodeA.size(); i++) {
        for (int j = 0; j < inEdgesOfnodeB.size(); j++) {
          if (((Edge) outEdgesOfnodeA.get(i)).getID() == ((Edge) inEdgesOfnodeB.get(j)).getID()) {
            findEdge = true;
            connectionEdges.add((Edge) inEdgesOfnodeB.get(j));
            return connectionEdges;
          }
        }
      }
      if (!findEdge) {
        return null;
      }
    } else if ((inEdgesOfnodeB.size() == 0 || outEdgesOfnodeA.size() == 0) && (inEdgesOfnodeA.size() != 0 || outEdgesOfnodeB
            .size() != 0)) {
      boolean findEdge = false;
      for (int i = 0; i < outEdgesOfnodeB.size(); i++) {
        for (int j = 0; j < inEdgesOfnodeA.size(); j++) {
          if (((Edge) outEdgesOfnodeB.get(i)).getID() == ((Edge) inEdgesOfnodeA.get(j)).getID()) {
            findEdge = true;
            connectionEdges.add((Edge) inEdgesOfnodeA.get(j));
            return connectionEdges;
          }
        }
      }
      if (!findEdge) {
        return null;
      }
    }
    // nodeA �P nodeB �����O�_�I�β��I
    else if (inEdgesOfnodeB.size() != 0 && outEdgesOfnodeA.size() != 0 && inEdgesOfnodeA.size() != 0 && outEdgesOfnodeB
            .size() != 0) {
      boolean findEdge = false;
      for (int i = 0; i < outEdgesOfnodeA.size(); i++) {
        for (int j = 0; j < inEdgesOfnodeB.size(); j++) {
          if (((Edge) outEdgesOfnodeA.get(i)).getID() == ((Edge) inEdgesOfnodeB.get(j)).getID()) {
            findEdge = true;
            connectionEdges.add((Edge) inEdgesOfnodeB.get(j));
            return connectionEdges;
          }
        }
      }
      for (int i = 0; i < outEdgesOfnodeB.size(); i++) {
        for (int j = 0; j < inEdgesOfnodeA.size(); j++) {
          if (((Edge) outEdgesOfnodeB.get(i)).getID() == ((Edge) inEdgesOfnodeA.get(j)).getID()) {
            findEdge = true;
            connectionEdges.add((Edge) inEdgesOfnodeA.get(j));
            return connectionEdges;
          }
        }
      }
      if (!findEdge) {
        return null;
      }
    } else {
      System.out
              .println(inEdgesOfnodeA.size() + " " + outEdgesOfnodeA.size() + " " + inEdgesOfnodeB.size() + " " + outEdgesOfnodeB
                      .size());
      System.out.println("getConnectionEdge() error");
    }
    return null;
  }
} // end of Graph class
