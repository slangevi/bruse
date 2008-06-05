package sc.bruse.engine;

import java.util.*;

import sc.bruse.api.BruseNode;

public class Graph {
	
	// Keeps a name index of nodes in the Graph
	protected Hashtable<String, GraphNode> 	m_index;
	protected ArrayList<GraphNode> 			m_nodes;
	
	public Graph() {
		m_index = new Hashtable<String, GraphNode>();
		m_nodes = new ArrayList<GraphNode>();
	}
	
	public static ArrayList<String> getNodeNames(ArrayList<GraphNode> nodes) {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i=0; i < nodes.size(); i++) {
			GraphNode node = nodes.get(i);
			names.add(node.getName());
		}
		
		return names;
	}
	
	public boolean containsNode(String name) {
		return m_index.containsKey(name);
	}
	
	public GraphNode getNode(String name) {
		GraphNode node = null;
		
		// If the index contains the key then return the value
		// otherwise this will return null  (not found)
		//if (m_index.containsKey(name)) {
			node = m_index.get(name);
		//}
		return node;
	}
	
	public void addNode(GraphNode node) {
		m_nodes.add(node);
		indexNode(node);
	}
	
	public void removeNode(GraphNode node) {
		m_nodes.remove(node);
		unindexNode(node);
	}
	
	public int size() {
		return m_nodes.size();
	}
	
	public ArrayList<GraphNode> getNodes() {
		return m_nodes;
	}
	
	public Graph clone() {
		Graph cloneGraph = new Graph();
		GraphNode node = null, cloneNode = null, neighbor = null, cloneNeighbor = null;
		Hashtable<GraphNode, GraphNode> index = new Hashtable<GraphNode, GraphNode>();
		
		for (int i=0; i < m_nodes.size(); i++) {
			node = m_nodes.get(i);
			cloneNode = new GraphNode(node.getName());
			cloneGraph.addNode(cloneNode);
			
			// map original node to cloned node
			index.put(node, cloneNode);
			
			// clone neighbors
			for (int j=0; j < node.getNeighbors().size(); j++) {
				neighbor = node.getNeighbors().get(j);
				
				if (index.containsKey(neighbor)) {
					// neighbor node is already created fetch it from index
					cloneNode.addNeighbor(index.get(neighbor));
				}
				else {
					cloneNeighbor = new GraphNode();
					cloneNode.addNeighbor(cloneNeighbor);
					
					// map original node to cloned node
					index.put(neighbor, cloneNeighbor);
				}
			}
		}
		
		return cloneGraph;
	}
	
	private void indexNode(GraphNode node) {
		// Index the node by the node name
		String name = node.getName();
		m_index.put(name, node);
	}
	
	private void unindexNode(GraphNode node) {
		// Remove the node from the index
		String name = node.getName();
		m_index.remove(name);
	}
	
	static public void dumpGraph(Graph graph) {
		
		for (int i=0; i < graph.getNodes().size(); i++) {
			GraphNode node = graph.getNodes().get(i);
			System.out.println("Graph Node " + node.getName());
			System.out.print("\nNeighbors: ");
			
			for (int j=0; j < node.getNeighbors().size(); j++) {
				GraphNode neighbor = node.getNeighbors().get(j);
				System.out.print(neighbor.getName() + " (edge weight: " + node.getEdgeWeight(neighbor) + "), ");
			}
			
			System.out.println();
			System.out.println();
		}
		
		
	}
}
