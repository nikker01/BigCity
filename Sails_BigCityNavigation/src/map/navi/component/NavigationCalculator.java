package map.navi.component;

import java.util.ArrayList;
import java.util.LinkedList;

import com.doubleservice.bigcitynavigation.graph.BiGraph;
import com.doubleservice.bigcitynavigation.graph.Edge;
import com.doubleservice.bigcitynavigation.graph.Node;

import lib.locate.algorithm.Math.MathProxy;
import map.navi.Data.NaviNode;
import map.navi.Data.NaviPlan;
import map.navi.PathAlgorithmn.DijkstraAlgorithm;
import android.util.Log;


public class NavigationCalculator {
	//private nodeHelper nodeHelper;
		private NaviPlan plan;
		private String TAG ="NavigationCalculator";
		public NavigationCalculator(NaviPlan plan) {
			this.plan = plan;
			//nodeHelper = new nodeHelper();
			//nodeHelper.initVariable();
		}
		
		public LinkedList<Node> getShortPathByStartXYAndEndXY(float startX,float startY,float endX,float endY) {
			//int startNode = getNavStartNode(this.currentTargetID);
			
			//Log.i(TAG, "Start x = "+startX+",y = "+startY);
			
			BiGraph cloneGraph = this.cloneBiGraph(this.plan.naviPlanGraph);
			
			
			
			NaviNode sNode = this.getTempNodeToProjectLine(cloneGraph,"temp1",startX, startY);
			Log.i(TAG, "End x = "+endX+",y = "+endY);
			NaviNode eNode = this.getTempNodeToProjectLine(cloneGraph,"temp2",endX, endY);
			//Log.i(TAG, "temp Start x = "+sNode.x+",y = "+sNode.y);
			//Log.i(TAG, "temp End x = "+eNode.x+",y = "+eNode.y);
			//cloneGraph.add(sNode);
			//cloneGraph.add(eNode);
			//int startEdge = getNearlyPathIndexByXY(sNode.x,sNode.y,false);
			//int endEdge = getNearlyPathIndexByXY(sNode.x,sNode.y,false);
			//NaviNode[] startPathNodes = this.getNearlyPathByXY(sNode.x,sNode.y,false);
			//NaviNode[] endPathNodes = this.getNearlyPathByXY(eNode.x,eNode.y, false);
			
			//this.addEdgeToGraph(cloneGraph, sNode, startPathNodes[0]);
			//this.addEdgeToGraph(cloneGraph, sNode, startPathNodes[1]);
			//this.addEdgeToGraph(cloneGraph, eNode, endPathNodes[0]);
			//this.addEdgeToGraph(cloneGraph, eNode, endPathNodes[1]);
			
			
			this.addNodeToGraph(cloneGraph, sNode);
			this.addNodeToGraph(cloneGraph, eNode);
			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm (cloneGraph);
			dijkstra.execute(sNode);
			LinkedList<Node> path = dijkstra.getPath(eNode);
			
			//LinkedList<Node> shortestPathBeginS0 = this.findShortestPathByOneStartNodeAndTwoEndNodesInDijkstra(startX,startY,endX,endY,dijkstra, startNodes[0], endNodes[0],endNodes[1]);		

			//LinkedList<Node> shortestPathBeginS1 = this.findShortestPathByOneStartNodeAndTwoEndNodesInDijkstra(startX,startY,endX,endY,dijkstra, startNodes[1], endNodes[0],endNodes[1]);		
			
			return path;//this.compareToPathShortest(shortestPathBeginS0, shortestPathBeginS1, startX, startY, endX, endY);
		}	
		
		private void addNodeToGraph(BiGraph graph,NaviNode node) {
			NaviNode[] nearlyPathNodes = this.getNearlyPathByXY(graph,node.x,node.y,false);
			Edge nodeNearEdge = this.getNearlyPathIndexByXY(graph,node.x,node.y,false);
			graph.add(node);
			graph.remove(nodeNearEdge);
			this.addEdgeToGraph(graph, node, nearlyPathNodes[0]);
			this.addEdgeToGraph(graph, node, nearlyPathNodes[1]);
			
		}
		
		private NaviNode getTempNodeToProjectLine(BiGraph graph,String id,float x,float y) {
			NaviNode[] pathStartAndEnd = this.getNearlyPathByXY(graph,x, y, false);
			//Log.i(TAG, "path start x = "+pathStartAndEnd[0].x+", y = "+pathStartAndEnd[0].y+" path end x = "+pathStartAndEnd[1].x+", y = "+pathStartAndEnd[1].y);
			float[] projectionCoordinate = MathProxy.getProjectionPoint(pathStartAndEnd[0], pathStartAndEnd[1], x, y);
			NaviNode tempNode = new NaviNode(id,(int)projectionCoordinate[0],(int)projectionCoordinate[1]);
			return tempNode;
		}
		
		public float[] findProjectionToLineInCurrentGraph(float x,float y) {
			 float[] pointXY = new float [2];
			  NaviNode[] pathStartAndEnd = this.getNearlyPathByXY(this.plan.naviPlanGraph,x, y, false);
			  pointXY = MathProxy.getProjectionPoint(pathStartAndEnd[0], pathStartAndEnd[1], x, y);
			  return pointXY;
		}
		
		public float[] findProjectionWithExistRoute(LinkedList<Node> route,float x,float y) {
			 float[] pointXY = new float [2];
			  NaviNode[] pathStartAndEnd = this.getNearlyPathByXYOnNavigationRoute(route,x, y);
			  pointXY = MathProxy.getProjectionPoint(pathStartAndEnd[0], pathStartAndEnd[1], x, y);
			  return pointXY;
		}
		
		
		private BiGraph cloneBiGraph(BiGraph graph) {
			BiGraph newGraph = new BiGraph();
			ArrayList nodes = new ArrayList();
			ArrayList edges = new ArrayList();
			for(Object node :graph.getNodes()) {
				nodes.add(node);
			}
			for(Object edge :graph.getEdges()) {
				edges.add(edge);
			}
			newGraph.addNodes(nodes);
			newGraph.addEdges(edges);
			return newGraph;
		}
		
		private void addEdgeToGraph(BiGraph graph,Node sourceNode,Node targetNode) {
			NaviNode sNode = (NaviNode) sourceNode;
			NaviNode tNode = (NaviNode) targetNode;
			Float distance = (float) Math.sqrt(Math.pow(sNode.x-tNode.x,2)+Math.pow(sNode.y-tNode.y,2));
			graph.newEdge(sourceNode, targetNode, sourceNode.getID()+"-"+targetNode.getID(),distance);
			graph.newEdge(targetNode, sourceNode, targetNode.getID()+"-"+sourceNode.getID(),distance);
		}	
		/*
		private  LinkedList<Node> findShortestPathByOneStartNodeAndTwoEndNodesInDijkstra(float sourceX,float sourceY,float endX,float endY,DijkstraAlgorithm dijkstra,NaviNode start,NaviNode end1,NaviNode end2) {
			dijkstra.execute(start);
			LinkedList<Node> path1 = dijkstra.getPath(end1);
			LinkedList<Node> path2 = dijkstra.getPath(end2);
			return this.compareToPathShortest(path1, path2, sourceX, sourceY, endX, endY);
			//return this.compareToPathShortest(path1,path2);
		}
		*/
		
		public float calculateDistanceOnLine(NaviPlan plan,float originX,float originY,float newX,float newY) {
			LinkedList<Node>  path =   this.getShortPathByStartXYAndEndXY(originX, originY, newX, newY);
			float distanceMeter = this.calculatePathLength(path)/plan.currentMapPixelperMeter;//(MathProxy.getDistance(originX, originY, newX, newY))/plan.currentMapPixelperMeter;//this.plan.currentMapPixelperMeter;
			return distanceMeter;
		}
		
		public float[] calculateLocateToNewCoordinate(NaviPlan plan,float originX,float originY,float newX,float newY,float distanceMeterLimit,float distanceOutMeterLimit) {
			float[] newXY = new float[2];
			//LinkedList<Node>  path =   this.getShortPathByStartXYAndEndXY(originX, originY, newX, newY);
			float distanceMeter = this.calculateDistanceOnLine(plan, originX, originY, newX, newY);//this.calculatePathLength(path)/plan.currentMapPixelperMeter;//(MathProxy.getDistance(originX, originY, newX, newY))/plan.currentMapPixelperMeter;//this.plan.currentMapPixelperMeter;
			
			
			//Log.i(TAG, "Locate And KNN locate distance = "+distanceMeter );
			if(distanceMeter <= distanceMeterLimit || distanceMeter>=distanceOutMeterLimit) {
				newXY[0] = originX;
				newXY[1] = originY;
			}
			else {
				newXY[0] = newX;
				newXY[1] = newY;
				//NaviNode[] orginStartAndEnd = this.getNearlyPathByXY(plan.naviPlanGraph,originX, originY, false);
				//float[] projectNewXY = MathProxy.getProjectionPoint(orginStartAndEnd[0], orginStartAndEnd[1], newX, newY);
				//newXY[0] = newX;//projectNewXY[0];//(originX+projectNewXY[0])/2f;
				//newXY[1] = newY;//projectNewXY[1];//(originY+projectNewXY[1])/2f;
			}
			
			if(newXY[0] ==0&&newXY[1]==0) {
				//Log.i(TAG, "path"+path);
				//Log.i(TAG, "meter = "+distanceMeter);
				//Log.i(TAG, "X = "+originX+",Y = "+originY);
				newXY[0] = originX;
				newXY[1] = originY;			
			}
			return newXY;
		}
		
		/*
		public LinkedList<Node> getShortPathByCurrentXYAndTargetID(float sourceX,float sourceY, int currentTargetID) {
			
			//int startNode = getNavStartNode(this.currentTargetID);
			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm (this.plan.naviPlanGraph);
			NaviNode[] startNodes = this.getNearlyPathByXY(sourceX,sourceY,false);
			NaviNode endNode = (NaviNode) this.plan.getNodeById(currentTargetID);
			
			
			dijkstra.execute(startNodes[0]);
			
			LinkedList<Node> path1 = dijkstra.getPath(endNode);//new LinkedList<Node>();
			//tmpPathfromsNode = dijkstra.getPath(endNode);
			dijkstra.execute(startNodes[1]);
			
			LinkedList<Node> path2 = dijkstra.getPath(endNode);//new LinkedList<Node>();
			//dijkstra.execute(startNodes[1]);
			//tmpPathfromtNode = dijkstra.getPath(endNode);
			return this.compareToPathShortest(path1, path2);
			//Log.v("get startNode","sNode: "+tmpPathfromsNode.size()+", tNode: "+tmpPathfromtNode.size());
			/*if(tmpPathfromsNode.size()<tmpPathfromtNode.size()){
				return tmpPathfromsNode;
			}else{
				return tmpPathfromtNode;
			}		
			*/
			/*
			ArrayList<Node> DFStrace = new ArrayList<Node>();
			DFStrace  = nodeHelper.DFS(this.plan.getNodeById(startNode),this.plan.naviPlanGraph);
			
			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(plan.naviPlanGraph);
			dijkstra.execute(plan.getNodeById(startNode));
			
			if(!DFStrace.contains(this.plan.getNodeById(this.currentTargetID))){
				//showNoPathDialog();
				Log.i(TAG,"No Path" );
			}
			else {
				this.currentNavigationPath = dijkstra.getPath(plan.getNodeById(this.currentTargetID));
			}
			*/
		//}	
		 /*
		private LinkedList<Node> compareToPathShortest(LinkedList<Node> path1,LinkedList<Node> path2) {
			if(this.calculatePathLength(path1)<=this.calculatePathLength(path2)) {
				return path1;
			}
			else 
				return path2;
		}
		*/
		private LinkedList<Node> compareToPathShortest(LinkedList<Node> path1,LinkedList<Node> path2,float startX,float startY,float endX,float endY) {
			
			float path1Distance = 0f;
			float path2Distance = 0f;
			
			if(path1.size()>0) {
				float distanceStart = MathProxy.getDistance(startX,startY,(NaviNode)path1.get(0));
				float distanceEnd = MathProxy.getDistance(endX,endY,(NaviNode)path1.get(path1.size()-1));
				path1Distance = distanceStart+this.calculatePathLength(path1)+distanceEnd;
			}
			
			if(path2.size()>0) {
				float distanceStart = MathProxy.getDistance(startX,startY,(NaviNode)path2.get(0));
				float distanceEnd = MathProxy.getDistance(endX,endY,(NaviNode)path2.get(path2.size()-1));
				path2Distance = distanceStart+this.calculatePathLength(path2)+distanceEnd;
			}
			
			if(path1Distance==0&&path2Distance>0) {
				return path2;
			}
			else if(path1Distance>0&&path2Distance==0) {
				return path1;
			}
			else if(path1Distance<=path2Distance) {
				return path1;
			}
			else 
				return path2;
		}
		
		
		private float calculatePathLength(LinkedList<Node> path) {
			float distance = 0f;
			if(path.size()>0) {
				NaviNode lastTimeNode = (NaviNode)path.get(0);
				for(Node node : path) {
					distance += MathProxy.getDistance(lastTimeNode, (NaviNode)node);
					lastTimeNode = (NaviNode) node;
				}
			}
			return distance;
		}
		public Node getNodeById(BiGraph graph,String id) {
			//Node node = (Node) this.nodeCollection.get(String.valueOf(id));
			Node node = new Node();
			for(Object object:graph.getNodes()) {
				if(((Node)object).getID().equals(id))
					node = (Node)object;
			}
			return node;
		}
		
		public NaviNode[] getNearlyPathByXYOnNavigationRoute(LinkedList<Node> route,float X,float Y) {
			float tmpLength = Float.POSITIVE_INFINITY;
			NaviNode[] nodes = new NaviNode[2];
			
			for(int index =1;index<route.size();index++) {
				NaviNode sNode = (NaviNode) route.get(index-1);//this.plan.getNodeById(Integer.parseInt(nodeIdStr[0]));
				NaviNode tNode = (NaviNode) route.get(index);//this.plan.getNodeById(Integer.parseInt(nodeIdStr[1]));
				if(tmpLength>MathProxy.getLength(sNode,tNode,X,Y)){
					tmpLength = MathProxy.getLength(sNode,tNode,X,Y);
					nodes[0] = sNode;
					nodes[1] = tNode;  
				}
			}
			return nodes;
		}
		
		public NaviNode[] getNearlyPathByXY(BiGraph graph,float X,float Y,boolean containPOI) {
			float tmpLength = Float.POSITIVE_INFINITY;
			NaviNode[] nodes = new NaviNode[2];
			for(int i=0;i<graph.edges.size();i++) {//this.plan.naviPlanGraph.edges.size();i++) {
			 //Log.v("Edge algor","edge id: "+((Edge)this.plan.planNaviLine.edges.get(i)).getID());
				String edgeID =((Edge)graph.edges.get(i)).getID() ;//((Edge)this.plan.naviPlanGraph.edges.get(i)).getID();
				String [] nodeIdStr = edgeID.split("-");
				
				NaviNode sNode = (NaviNode) this.getNodeById(graph, nodeIdStr[0]);//this.plan.getNodeById(Integer.parseInt(nodeIdStr[0]));
				NaviNode tNode = (NaviNode) this.getNodeById(graph, nodeIdStr[1]);//this.plan.getNodeById(Integer.parseInt(nodeIdStr[1]));
				
				if(!containPOI) {
					String sNodeName = sNode.getClass().getSimpleName();
					String tNodeName = tNode.getClass().getSimpleName();
					if(sNodeName.equals("POI")||tNodeName.equals("POI")) {
						continue;
					}
				}
				float dis =  MathProxy.getLength(sNode,tNode,X,Y);
				//Log.i(TAG, "every lenght start x = "+sNode.x+", y ="+sNode.y+" end x = "+tNode.x+", y = "+tNode.y+", dis = "+dis);
				if(tmpLength>MathProxy.getLength(sNode,tNode,X,Y)){
					
					tmpLength = MathProxy.getLength(sNode,tNode,X,Y);
					nodes[0] = sNode;
					nodes[1] = tNode;  
				}
			}

			return nodes;
		}
		
		private Edge getNearlyPathIndexByXY(BiGraph graph,float X,float Y,boolean containPOI) {
			float tmpLength = Float.POSITIVE_INFINITY;
			//NaviNode[] nodes = new NaviNode[2];
			Edge edge = new Edge();
			for(int i=0;i<graph.edges.size();i++) {//this.plan.naviPlanGraph.edges.size();i++) {
				 //Log.v("Edge algor","edge id: "+((Edge)this.plan.planNaviLine.edges.get(i)).getID());
					String edgeID =((Edge)graph.edges.get(i)).getID() ;//((Edge)this.plan.naviPlanGraph.edges.get(i)).getID();
					String [] nodeIdStr = edgeID.split("-");
					
					NaviNode sNode = (NaviNode) this.getNodeById(graph, nodeIdStr[0]);//this.plan.getNodeById(Integer.parseInt(nodeIdStr[0]));
					NaviNode tNode = (NaviNode) this.getNodeById(graph, nodeIdStr[1]);//this.plan.getNodeById(Integer.parseInt(nodeIdStr[1]));
					
				if(!containPOI) {
					String sNodeName = sNode.getClass().getSimpleName();
					String tNodeName = tNode.getClass().getSimpleName();
					if(sNodeName.equals("POI")||tNodeName.equals("POI")) {
						continue;
					}
				}
				if(tmpLength>MathProxy.getLength(sNode,tNode,X,Y)){
					tmpLength = MathProxy.getLength(sNode,tNode,X,Y);
					edge = (Edge)graph.edges.get(i);//nodes[0] = sNode;
					//nodes[1] = tNode;  
				}
			}

			return edge;
		}
		
		
		/*
		private int getNavStartNode(int endNode){
			int startNode = -1;
		   	
			String [] nodeIdStr = ((Edge)this.plan.naviPlanGraph.edges.get(this.currentEdge)).getID().split("-");
			int sNode = Integer.valueOf(nodeIdStr[0]);
			int tNode = Integer.valueOf(nodeIdStr[1]);
			
			
			//Log.v("get startNode","endNode: "+endNode+" tNode: "+tNode+", sNode: "+sNode);
			//ArrayList<Integer> tmpPathfromtNode = new ArrayList<Integer>();
			DijkstraAlgorithm dijkstra = new DijkstraAlgorithm (this.plan.naviPlanGraph);
			LinkedList<Node> tmpPathfromtNode = new LinkedList<Node>();
			dijkstra.execute(this.plan.getNodeById(tNode));
			tmpPathfromtNode = dijkstra.getPath(this.plan.getNodeById(endNode));
			
			LinkedList<Node> tmpPathfromsNode = new LinkedList<Node>();
			dijkstra.execute(this.plan.getNodeById(sNode));
			tmpPathfromsNode = dijkstra.getPath(this.plan.getNodeById(endNode));
			
			Log.v("get startNode","sNode: "+tmpPathfromsNode.size()+", tNode: "+tmpPathfromtNode.size());
			if(tmpPathfromtNode.size()<tmpPathfromsNode.size()){
				startNode = tNode;
			}else{
				startNode = sNode;
			}		
			return startNode;
		}
		 */
	}
