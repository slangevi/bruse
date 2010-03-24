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

import java.util.ArrayList;
import java.util.Hashtable;

import sc.bruse.network.BruseAPIException;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNode;

public class DConnected {

	private Graph							m_infoGraph;
	private BruseNetwork 					m_network;
	private Hashtable<String, boolean[]> 	m_info;
	
	private final static int NUM_INFO_ITEMS = 3;
	private final static int HAS_EVIDENCE 	= 0;
	private final static int HAS_DESCENDENT = 1;
	private final static int VISITED 		= 2;  // Don't think I need this
	
	public DConnected(BruseNetwork network) {
		m_network = network;
		m_info = new Hashtable<String, boolean[]>();
		m_infoGraph = createInfoGraph();
		initInfo();
	}
	
	private void initInfo() {
		for (BruseNode node: m_network.getAllNodes()) {
			m_info.put(node.getName(), new boolean[NUM_INFO_ITEMS]);
		}
	}
	
	public void setEvidence(ArrayList<String> evidence) {
		boolean[] info;
		for (String ev: evidence) {
			m_info.get(ev)[HAS_EVIDENCE] = true;
		}
		initDescendents();
	}
	
	public ArrayList<BruseNode> getConnected(ArrayList<String> query) {
		int i = 1;
		GraphNode node = null;
		ArrayList<GraphNode[]> tmpedges, curedges = new ArrayList<GraphNode[]>(); 
		ArrayList<BruseNode> connected = new ArrayList<BruseNode>();
		
		try {
			// traverse infograph starting at each query node
			// all nodes reachable are d-connected
			for (String nodeName: query) {
				node = m_infoGraph.getNode(nodeName);
				
				// query node is d-connected to itself
				connected.add(m_network.getNode(nodeName));
				
				// add children of query node are d-connected
				for (GraphNode child: node.getChildren()) {
					node.addEdgeWeight(child, i);
					// node is reachable and therefore is d-connected to query
					connected.add(m_network.getNode(child.getName()));
					// keep track of the traversed edges
					curedges.add(new GraphNode[]{node, child});
				}
			}
			// process edges until there are no more traversed edges
			while (curedges.size() > 0) {
				i++;
				tmpedges = new ArrayList<GraphNode[]>(); 
				
				// traverse edges
				for (GraphNode[] edge: curedges) {
					
					for (GraphNode child: edge[1].getChildren()) {
						if (isValidTraversal(edge, child)) {
							edge[1].addEdgeWeight(child, i);
							// node is reachable and therefore is d-connected to query
							connected.add(m_network.getNode(child.getName()));
							// keep track of the traversed edges
							tmpedges.add(new GraphNode[]{edge[1], child});
						}
					}
				}
				// update the current edges processed
				curedges = tmpedges;
			}
		}
		catch (BruseAPIException e) {
			System.err.println("Error determining d-connected nodes to query: " + query);
			e.printStackTrace();
		}
		return connected;
	}
	
	private boolean isValidTraversal(GraphNode[] fromEdge, GraphNode toNode) {
		// make sure the toNode hasn't been visited from this edge before
		if (fromEdge[1].getEdgeWeight(toNode) > 0) return false;
		
		// X - Y - X is not valid
		if (fromEdge[0] == toNode) return false;
		
		// check if fromEdge[1] is not a head-to-head node along path in bayesian network and does not have evidence
		
		// check if fromEdge[1] is a head-to-head node along path in bayesian network and has descendent evidence
		
		if (isHeadtoHeadNode(fromEdge[1].getName(), fromEdge[0].getName(), toNode.getName())) {
			if (!hasDescendent(fromEdge[1].getName())) return false;
		}
		else if (hasEvidence(fromEdge[1].getName())) {
			return false;
		}
		
		return true;
	}
	
	private boolean isHeadtoHeadNode(String y, String x, String z) {
		boolean result = false;
		
		try {
			BruseNode nx = m_network.getNode(x);
			BruseNode ny = m_network.getNode(y);
			BruseNode nz = m_network.getNode(z);
		
			// Check if:  x -> y <- z in bayesian network
			result = (nx.getChildren().contains(ny) && nz.getChildren().contains(ny));
		}
		catch (Exception e) {
			System.err.println("Error checking if node " + y + " is a head-to-head node along path: " + x + "-" + y + "-" + z);
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void initDescendents() {
		for (BruseNode node: m_network.getAllNodes()) {
			m_info.get(node.getName())[HAS_DESCENDENT] = isDescendent(node);
		}
	}
	
	private boolean isDescendent(BruseNode node) {
		boolean result = false;
		
		if (hasEvidence(node.getName())) {
			result = true;
		}
		else {
			for (BruseNode child: node.getChildren()) {
				if (isDescendent(child)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	private boolean isVisited(String node) {
		return m_info.get(node)[VISITED];
	}
	
	private boolean hasEvidence(String node) {
		return m_info.get(node)[HAS_EVIDENCE];
	}
	
	private boolean hasDescendent(String node) {
		return m_info.get(node)[HAS_DESCENDENT];
	}
	
	private Graph createInfoGraph() {
		Hashtable<BruseNode, GraphNode> index = new Hashtable<BruseNode, GraphNode>();
		Graph infoGraph = new Graph();
		GraphNode node = null, child = null, parent = null;
		
		for (BruseNode bnode: m_network.getAllNodes()) {
			if (index.containsKey(bnode)) {
				node = index.get(bnode);
			}
			else {
				node = new GraphNode(bnode.getName());
				infoGraph.addNode(node);
			
				// map original bruse node to graph node
				index.put(bnode, node);
			}
			
			// create children
			for (BruseNode bchild: bnode.getChildren()) {
				if (index.containsKey(bchild)) {
					child = index.get(bchild);
					node.addChild(child);
				}
				else {
					child = new GraphNode(bchild.getName());
					infoGraph.addNode(child);
					node.addChild(child);
					// map original bruse node to graph node
					index.put(bchild, child);
				}
				
				// create a reverse edge from child to node
				child.addChild(node);
				node.addParent(child);
			}
			
			// create parents
			for (BruseNode bparent: bnode.getParents()) {
				if (index.containsKey(bparent)) {
					node.addParent(index.get(bparent));
				}
				else {
					parent = new GraphNode(bparent.getName());
					infoGraph.addNode(parent);
					node.addParent(parent);
					// map original bruse node to graph node
					index.put(bparent, parent);
				}
			}
		}
		
		return infoGraph;
	}
}
