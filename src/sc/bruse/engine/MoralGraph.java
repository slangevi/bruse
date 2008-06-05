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

import sc.bruse.network.*;

import java.util.*;

public class MoralGraph extends Graph {
	
	public MoralGraph(BruseNetwork network) {
		super();
		create(network);
	}
	
	public MoralGraph() {
		super();
	}
	
	private void create(BruseNetwork network) {
		GraphNode node = null;
		BruseNode bNode = null;
		ArrayList<BruseNode> nodes = network.getAllNodes();
		
		for (int i=0; i < nodes.size(); i++) {
			bNode = nodes.get(i);
			node = this.getNode(bNode.getName());
			if (node == null) { // node does not exist yet - create
				node = new MoralGraphNode(bNode);
				this.addNode(node);
			}
			createEdges(bNode, node);
		}
	}
	
	private void createEdges(BruseNode bNode, GraphNode node) {
		BruseNode pbNode = null;
		GraphNode parent = null;
		ListIterator<BruseNode> it = bNode.getParents().listIterator();
		
		while (it.hasNext()) {
			pbNode = it.next();
			parent = this.getNode(pbNode.getName());
			if (parent == null) {
				parent = new MoralGraphNode(pbNode);
				this.addNode(parent);
			}
			// add edges
			node.addParent(parent);
			parent.addChild(node);
		}
		
		// moralize the parents
		ArrayList<GraphNode> parents = node.getParents();
		GraphNode n1 = null, n2 = null;
		for (int i=0; i < parents.size(); i++) {
			n1 = parents.get(i);
			for (int j=0; j < parents.size(); j++) {
				if (i != j) {
					n2 = parents.get(j);
					n1.addNeighbor(n2);
					//n2.addNeighbor(n1);
				}
			}
		}
	}
	
	@Override
	public Graph clone() {
		MoralGraphNode node = null;
		MoralGraph cloneGraph = new MoralGraph();
				
		for (int i=0; i < m_nodes.size(); i++) {
			node = (MoralGraphNode)m_nodes.get(i);
			if (cloneGraph.containsNode(node.getName()) == false) {
				cloneNode(node, cloneGraph);
			}
		}
		
		return cloneGraph;
	}
	
	private MoralGraphNode cloneNode(MoralGraphNode node, MoralGraph cloneGraph) {
		MoralGraphNode cloneNode = new MoralGraphNode(node.getBruseNode());
		cloneGraph.addNode(cloneNode);
		
		// clone neighbors
		for (int j=0; j < node.getNeighbors().size(); j++) {
			MoralGraphNode neighbor = (MoralGraphNode)node.getNeighbors().get(j);
			
			if (cloneGraph.containsNode(neighbor.getName())) {
				// neighbor node is already created fetch it from graph
				cloneNode.addNeighbor(cloneGraph.getNode(neighbor.getName()));
			}
			else {
				// clone neighor node and then add as neighbor
				cloneNode.addNeighbor(cloneNode(neighbor, cloneGraph));
			}
		}
		
		return cloneNode;
	}
	
}
