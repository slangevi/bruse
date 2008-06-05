package sc.bruse.engine;

import sc.bruse.network.*;

import java.util.*;

public class Triangulation {

	public static void triangulate(MoralGraph graph) {
		//Heuristic: repeatedly eliminiate a simplicial node and if none exist
		// then eliminate node with minimum number of fill-ins created
		
		// make a working copy of the graph
		MoralGraph workingGraph = (MoralGraph)graph.clone();
		
		while (workingGraph.size() > 0) {
			// Find next node to remove and create any fillIns required
			MoralGraphNode node = processNextNode(workingGraph, graph);
			
			// remove node from graph
			removeNode(workingGraph, node);
		}
	}
	
	private static void fullyConnectNodes(ArrayList<String> neighbors, MoralGraph graph) {
		for (int i=0; i < neighbors.size(); i++) {
			String neighborName = neighbors.get(i);
			MoralGraphNode neighbor = (MoralGraphNode)graph.getNode(neighborName);
			
			for (int j=0; j < neighbors.size(); j++) {
				if (i != j) {
					String otherNeighborName = neighbors.get(j);
					MoralGraphNode otherNeighbor = (MoralGraphNode)graph.getNode(otherNeighborName);
					neighbor.addNeighbor(otherNeighbor);
				}
			}
		}
	}
	
	private static MoralGraphNode processNextNode(MoralGraph workingGraph, MoralGraph graph) {
		MoralGraphNode node = findSimplicialNode(workingGraph);
		
		if (node == null) {
			node = findMinFillInNode(workingGraph);
			//node = findMinDomNode(workingGraph);
			createFillIns(node);
			
			// fully connect the nodes in the graph
			fullyConnectNodes(Graph.getNodeNames(node.getNeighbors()), graph);
		}
		
		return node;
	}
	
	private static void removeNode(MoralGraph graph, MoralGraphNode node) {
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			
			neighbor.removeNeighbor(node);
		}
		
		graph.removeNode(node);
	}
	
	private static void createFillIns(MoralGraphNode node)  {
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			
			for (int j=0; j < neighbors.size(); j++) {
				if (i != j) {
					MoralGraphNode otherNeighbor = (MoralGraphNode)neighbors.get(j);
				
					// Add the neighbor
					// Note this will ignore if already a neighbor
					neighbor.addNeighbor(otherNeighbor);
				}
			}
		}
	}
	private static long domSize(MoralGraphNode node) {
		long dom = node.getBruseNode().getStates().size();
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			dom *= neighbor.getBruseNode().getStates().size();
		}
		
		return dom;
	}
	
	private static MoralGraphNode findMinDomNode(MoralGraph graph) {
		ArrayList<GraphNode> nodes = graph.getNodes();
		MoralGraphNode minDomNode = null, node = null;
		long dom = 0, minDom = Long.MAX_VALUE;
		
		for (int i=0; i < nodes.size(); i++) {
			node = (MoralGraphNode)nodes.get(i);
			dom = domSize(node);
			
			if (dom < minDom) {
				minDom = dom;
				minDomNode = node;
			}
		}
		
		return minDomNode;
	}
	
	private static MoralGraphNode findMinFillInNode(MoralGraph graph) {
		ArrayList<GraphNode> nodes = graph.getNodes();
		MoralGraphNode minFillInNode = null, node = null;
		int fillIns = 0, minFillIns = Integer.MAX_VALUE;
		
		for (int i=0; i < nodes.size(); i++) {
			node = (MoralGraphNode)nodes.get(i);
			fillIns = numFillIns(node);
			
			if (fillIns < minFillIns) {
				minFillIns = fillIns;
				minFillInNode = node;
			}
		}
		
		return minFillInNode;
	}
	
	private static int numFillIns(MoralGraphNode node) {
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		int numFillIns = 0;
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			
			for (int j=0; j < neighbors.size(); j++) {
				if (i != j) {
					MoralGraphNode otherNeighbor = (MoralGraphNode)neighbors.get(j);
				
					// If neighbor is not connected then a fillin would be required
					if (neighbor.getNeighbors().contains(otherNeighbor) == false) 
						numFillIns++;
				}
			}
		}
		return numFillIns;
	}
	
	public static MoralGraphNode findSimplicialNode(MoralGraph graph) {
		ArrayList<GraphNode> nodes = graph.getNodes();
		MoralGraphNode node = null;
		
		for (int i=0; i < nodes.size(); i++) {
			node = (MoralGraphNode)nodes.get(i);
			if (isSimplicialNode(node)) return node;
		}
		
		// No simplicial node exists return null
		return null;
	}
	
	public static boolean isSimplicialNode(MoralGraphNode node) {
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			
			for (int j=0; j < neighbors.size(); j++) {
				if (i != j) {
					MoralGraphNode otherNeighbor = (MoralGraphNode)neighbors.get(j);
				
					// If neighbor is not fully connected then this is not a simplicial node
					if (neighbor.getNeighbors().contains(otherNeighbor) == false) 
						return false;
				}
			}
		}
		
		// must be a simplicial node if we make it here
		return true;
	}
}
