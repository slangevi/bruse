package sc.bruse.engine;

/***********************************
 * Copyright 2008 Scott Langevin
 * 
 * All Rights Reserved.
 *
 * This file is part of BRUSE.
 *
 * BRUSE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BRUSE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BRUSE.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Scott Langevin (scott.langevin@gmail.com)
 *
 */

import java.util.*;

//TODO Make this more efficient
public class MaxSpanTree {
	Graph 						m_graph;
	ArrayList<GraphNode> 		m_reachableNodes;
	ArrayList<MaxSpanTreeEdge> 	m_edges;
	MaxHeap 					m_maxHeap;
	int 						m_numNodes;
	
	public MaxSpanTree(Graph graph) throws Exception {
		m_graph = new Graph();
		m_numNodes = graph.size();
		m_maxHeap = new MaxHeap(m_numNodes*m_numNodes);
		m_reachableNodes = new ArrayList<GraphNode>();
		m_edges = new ArrayList<MaxSpanTreeEdge>();
		
		if (graph.getNodes().isEmpty()) {
			throw new Exception("MaxSpanTree requires a graph with at least one node.");
		}
		
		// pick a node as the first vertex to add to the max span tree
		GraphNode first = graph.getNodes().get(0);
		
		// add to the list of reachable nodes
		m_reachableNodes.add(first);
		
		// Add the nodes edges to the heap
		m_maxHeap.addNodeEdges(first);
		
		// loop until we have reached every node in the original graph
		while (m_reachableNodes.size() < m_numNodes) {
			// get max edge in heap
			MaxSpanTreeEdge edge = m_maxHeap.popMaxEdge();
			
			// process the max edge
			processEdge(edge);
		}
		
		// we now have a list of edges that make up the max span tree
		// construct a graph(tree) from this list of edges
		constructTree();
	}
	
	public ArrayList<GraphNode> getNodes() {
		return m_graph.getNodes();
	}
	
	public Graph getTree() {
		return m_graph;
	}
	
	private void constructTree() {
		if (m_edges.size() == 0) {
			// This graph has no edges - it only has one node
			addNode(m_reachableNodes.get(0));
			return;
		}
		
		for (int i=0; i < m_edges.size(); i++) {
			MaxSpanTreeEdge edge = m_edges.get(i);
			GraphNode edgeLeft = edge.getLeftNode();
			GraphNode edgeRight = edge.getRightNode();
			
			// add the left and right nodes of the edge to the graph
			GraphNode leftNode = addNode(edgeLeft);
			GraphNode rightNode = addNode(edgeRight);
			
			// make the left and right node neighbors
			leftNode.addNeighbor(rightNode);
			leftNode.addEdgeWeight(rightNode, edge.getWeight());
			rightNode.addNeighbor(leftNode);
			rightNode.addEdgeWeight(leftNode, edge.getWeight());
		}
	}
	
	private void processEdge(MaxSpanTreeEdge edge) {
		try {
			GraphNode node = edge.getRightNode();
		
			// make sure the right node is not already reachable
			if (m_reachableNodes.contains(node) == false) {
				// this node is now reachable so add it to the reachable nodes list
				m_reachableNodes.add(node);
			
				// this is an edge in the max spanning tree
				m_edges.add(edge);
			
				// add all edges from the new node to our heap
				m_maxHeap.addNodeEdges(node);
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private GraphNode addNode(GraphNode node) {
		// Create a new node based on the passed in node
		// do not copy the neighbors - these will be added by the max span tree algo
		GraphNode clone;
		
		if (m_graph.containsNode(node.getName())) {
			// node already exists in graph
			clone = m_graph.getNode(node.getName());
		}
		else {
			// clone node and add to graph
			clone = node.clone();
			m_graph.addNode(clone);
		}
		return clone;
	}
}
