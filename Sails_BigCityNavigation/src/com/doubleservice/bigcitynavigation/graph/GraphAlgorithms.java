// -------------------------------------------------------------------------

// Copyright (c) 2002 Bestis. Co.

// Bestis Confidential Level: TS(top secret), S(secret),

//                            VIP(Very important partner), BP(Bestis partner)

//                            P (public)

// -------------------------------------------------------------------------

// Revision History:

// create : name, date, short description

// bugfix : name, date, what bugs?

// extend : name, date, what new functions?

// -------------------------------------------------------------------------

// Keywords: general file searching keywords; leave blank if none

// -------------------------------------------------------------------------

// Notes: some important points that you want those

// -------------------------------------------------------------------------

// To-do list: functions that have not implemented yet

// -------------------------------------------------------------------------



package com.doubleservice.bigcitynavigation.graph;



/**

 * Title:        Associate Prof.

 * Description:  Async Libraries for SOC Design

 * Copyright:    Copyright (c) 2000

 * Company:      Async VLSI System & Java Technology Group, Tatung University

 * @author Fu-Chiung John Cheng

 * @version 1.0

 */





import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.LinkedList;

import android.util.Log;



/**
 * com.doubleservice.bigcitynavigation.graph algorithms
 * @author Fu-Chiung John Cheng, Dept. of Computer Science and Engineering, Tatung University
 * @version 1.0
 * Date: 1999/10/7
 * @see <a href="Graph.html">GraphAlgorithms Class</a>
 * @see <a href="Edge.html">Edge Class</a>
 * @see <a href="Node.html">Node Class</a>
 **/

public class GraphAlgorithms {

  /**
   * breadthFirstSearch algorithms: given a root node, perform breadth-first search algorithm
   * @param     g     the com.doubleservice.bigcitynavigation.graph
   * @param     root-node  a root node of a com.doubleservice.bigcitynavigation.graph.
   * @return    list of nodes of breadth first traversal.
   *
   **/
  static public ArrayList breadthFirstSearch(Graph g, Node root) {
    Node aNode = root, tempNode;
    ArrayList visited = new ArrayList();  // result
    ArrayList adjNodes;
    LinkedList toBeProcessed = new LinkedList(); // a queue
    toBeProcessed.add(root);        // start from root node

    // bread first travasal in first in first out fashion

    while (!toBeProcessed.isEmpty()) {
      aNode = (Node) toBeProcessed.removeFirst();  // first in first out
      visited.add(aNode);    // append
      adjNodes = g.adjacentNodes(aNode);
      for (int i=0; i< adjNodes.size(); i++) {
        tempNode = (Node) adjNodes.get(i);
        if (!visited.contains(tempNode) && !toBeProcessed.contains(tempNode)) {
          toBeProcessed.add(tempNode); // add to the end
        } // end if
      } // end of for
    } // end of while

    return visited;
  }  // end of breadFirstSearch



  /**
   * depthFirstSearch algorithms: given a root node, perform depth-first search algorithm
   * @param     g     the com.doubleservice.bigcitynavigation.graph
   * @param     root  a root node of a com.doubleservice.bigcitynavigation.graph.
   * @return    list of nodes of depthfirst traversal.
   **/

  static public ArrayList depthFirstSearch(Graph g, Node root) {
    Node aNode = root, tempNode;
    ArrayList visited = new ArrayList();  // result
    ArrayList adjNodes;
    Stack toBeProcessed = new Stack();

    toBeProcessed.push(root);
    // depth first travasal in last in first out fashion
    while (!toBeProcessed.isEmpty()) {
      aNode = (Node) toBeProcessed.pop();
      visited.add(aNode);
      adjNodes = g.adjacentNodes(aNode);
      for (int i=0; i< adjNodes.size(); i++) {
        tempNode = (Node) adjNodes.get(i);
        if (!visited.contains(tempNode)) {
          toBeProcessed.push(tempNode);   // push the object
        } // end if
      } // end of for
    } // end of while

    return visited;
  }  // end of breadFirstSearch



  /**
   * path algorithms: given a source node and a target node in a grph,
   *                  find the path from the source node to the target node
   * @param     g     the com.doubleservice.bigcitynavigation.graph
   * @param     sNode the source node of a com.doubleservice.bigcitynavigation.graph.
   * @param     tNode the target node of a com.doubleservice.bigcitynavigation.graph.
   * @return    the path (ArrayList of nodes of depthfirst traversal.)
   **/
  static public ArrayList path(Graph g, Node sNode, Node tNode) {
	    Node aNode = sNode, tempNode;
	    ArrayList visited = new ArrayList();
	    ArrayList path = new ArrayList();  // result
	    ArrayList deleted = new ArrayList();  // result
	    ArrayList adjNodes;
	    Stack toBeProcessed = new Stack();
	    toBeProcessed.push(aNode);

	    while (!toBeProcessed.isEmpty()) {
	      aNode = (Node) toBeProcessed.pop();
	      visited.add(aNode);
	      path.add(aNode);
	      adjNodes = g.adjacentNodes(aNode);
	      for (int i=0; i< adjNodes.size(); i++) {
	        tempNode = (Node) adjNodes.get(i);
	        if (!visited.contains(tempNode)) {
	          toBeProcessed.push(tempNode);   // push the object
	        }
	      } // end of for
	    } // end of while

	    return path;
	  }  // end of breadFirstSearch
  static public ArrayList path2(BiGraph g, Node sNode, Node tNode) {
	    Node aNode = sNode, tempNode;
	    ArrayList visited = new ArrayList();
	    ArrayList path = new ArrayList();  // result
	    ArrayList adjNodes;
	    ArrayList path2 = new ArrayList();
	    Stack toBeProcessed = new Stack();
	    int count=0;
	    int tmpCount = 0;
	    Node forkNode = new Node();
	   // ArrayList<Integer> tmpCount = new ArrayList<Integer>();
	    toBeProcessed.push(aNode);

	    
	    while (!toBeProcessed.isEmpty()) {
	      aNode = (Node) toBeProcessed.pop();
	      //Log.v("aNode = ", aNode.getID());
	      
	      visited.add(aNode);
	      path.add(aNode);
	      if(aNode.equals(tNode)){
	    	  toBeProcessed.clear();
	    	  break;
	      }
	     adjNodes = bubbleSort(g.adjacentNodes(aNode));
	      //adjNodes = g.adjacentNodes(aNode);
	      /*
	      for(int i=0;i<adjNodes.size();i++)
	    	  Log.v("adj node: ", ((Node) adjNodes.get(i)).getID());
	      */
	      //for(int i=0;i<path.size();i++)
	    	  //Log.v("path node: ", ((Node) path.get(i)).getID());
	      if(adjNodes.size()>2){
	    	  //tmpCount.add(count);
	    	  //tmpCount = count;
	    	  //toBeProcessed.push(aNode);
	    	  count=0;
	      }
	      else if(adjNodes.size()==1&&path.size()>0){
	    	  //Log.v("count", Integer.toString(count));
	    	  if(aNode.equals(tNode)){
	    		  toBeProcessed.clear();
		    	  break;
		      }
	    	  for(int i=0;i<count;i++){
	    		  path.remove(path.size()-1);
	    	  }
	    	  //stepCount.remove(stepCount.size()-1);
	    	  count=0;
	      }
	      
	      boolean noPath = true;
	      for (int i=0; i< adjNodes.size(); i++) {
	        tempNode = (Node) adjNodes.get(i);
	        if (!visited.contains(tempNode)) {
	          toBeProcessed.push(tempNode);   // push the object
	          noPath = false;
	        }
	      } // end of for
	     if(noPath){
	    	 //Log.v("aNode = ", aNode.getID());
	    	 for(int i=0;i<count;i++){
	    		  path.remove(path.size()-1);
	    	  }
	    	  //stepCount.remove(stepCount.size()-1);
	    	  count=0; 
	     }
	      count++;
	    } // end of while
	    return path;
	  }  // end of breadFirstSearch
	private static ArrayList bubbleSort(ArrayList nodes)
	{
		Node temp;
		 if (nodes.size()>1) // check if the number of orders is larger than 1
	        {
	            for (int index=0; index<nodes.size(); index++) // bubble sort outer loop
	            {
	                for (int i=0; i < nodes.size()-index-1; i++) 
	                {
	                	
	                    if (Integer.valueOf(((Node)nodes.get(i)).getID())<Integer.valueOf(((Node)nodes.get(i+1)).getID()))
	                    {
	                        temp = (Node) nodes.get(i);
	                        nodes.set(i,nodes.get(i+1) );
	                        nodes.set(i+1, temp);
	                    }
	                }
	            }
	        }
		 return nodes;
	}
}

