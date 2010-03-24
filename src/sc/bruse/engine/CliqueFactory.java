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

public class CliqueFactory {

	static public ArrayList<Clique> createCliques(BruseNetwork network) {
		// Create a moral graph for bruse network
		MoralGraph moralGraph = new MoralGraph(network);
		
		// Triangulate and generate cliques
		ArrayList<Clique> cliques = triangulate(moralGraph);
		
		// remove subset cliques
		cliques = getMaxCliques(cliques);
		
		setCliqueStats(cliques);
		
		//dumpSize(cliques);
		
		// assign potentials to cliques
		assignPotentials(network, cliques);
		
		// return cliques
		return cliques;
	}
	
	static protected void setCliqueStats(ArrayList<Clique> cliques) {
		long max = 0;
		long size = 0;
		long sum = 0;
		long numvars = 0;
		
		for (int i=0; i < cliques.size(); i++) {
			size = cliques.get(i).getTable().getTableValues().length;
			sum += size;
			
			if (size > max) max = size;
			
			size = cliques.get(i).getMembers().size();
			
			if (size > numvars) numvars = size;
		}
		
		BookKeepingMgr.NumCliques = cliques.size();
		BookKeepingMgr.TriangulationSize = sum;
		BookKeepingMgr.MaxCliqueSize = max;
		BookKeepingMgr.MaxVarsInClique = numvars;
	}
	
	static protected void dumpSize(ArrayList<Clique> cliques) {
		long sum = 0;
		
		for (int i=0; i < cliques.size(); i++) {
			sum += cliques.get(i).getTable().getTableValues().length;
		}
		
		System.out.println("Triangulation size: " + sum);
	}
	
	static protected void assignPotentials(BruseNetwork network, ArrayList<Clique> cliques) {
		Clique clique = null, bestClique = null;
		BruseNode node = null;
		BruseTable table = null;
		
		// Uniquely assign a potential to a clique that covers the domain of the potential
		// If there are several qualifying cliques we choose the first clique
		// TODO: Try a different hueristic where we assign the potential to the clique
		// with the min domain difference. 
		// Ex: pot(A,B) matches Clique(A,B,C,D) and Clique(A,B,E)
		//		choose Clique(A,B,E) because it has the min domain difference
		for (int i=0; i < network.getAllNodes().size(); i++) {
			node = network.getAllNodes().get(i);
			table = node.getTable();
			
			for (int j=0; j < cliques.size(); j++) {
				clique = cliques.get(j);
				
				if (clique.containsNodes(table.getVariableNames())) { // getMembers().containsAll(table.getVariables())) {
					if ((bestClique == null) || (bestClique.getPotentials().size() > clique.getPotentials().size())) {
						bestClique = clique;
					}
					//clique.addPotential(table);
					//break;
				}
			}
			bestClique.addPotential(table);
			bestClique = null;
		}
		
		// all cliques now have initial potentials set - this is for resetting potentials
		for (int i=0; i < cliques.size(); i++) {
			clique = cliques.get(i);
			clique.setInitPotentials();
		}
	}
	
	static protected ArrayList<Clique> triangulate(MoralGraph graph) {
		// Heuristic: Repeatedly remove a simplicial node and if none exists
		// then remove a node with the smallest size(family)
		ArrayList<Clique> cliques = new ArrayList<Clique>();
		
		// make a working copy of the graph
		MoralGraph workingGraph = (MoralGraph)graph.clone();
		
		// While there are more nodes in the working graph
		while (workingGraph.size() > 0) {
			// Find next node to process as a clique
			MoralGraphNode node = processNextNode(workingGraph);
			
			// create clique
			cliques.add( createClique(node) );
			
			// remove node from graph
			removeNode(workingGraph, node);
		}
		
		return cliques;
	}
	
	static protected Clique createClique(MoralGraphNode node) {
		BruseNode[] family = new BruseNode[node.getNeighbors().size() + 1];
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		// add the simplicial node to the family
		family[0] = node.getBruseNode();
		
		// add the simplicial nodes neighbors to the family
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			family[i+1] = neighbor.getBruseNode();
		}
		
		//System.out.println("Clique size: " + domSize(node));
		
		return (new Clique(family));
	}
	
	static protected MoralGraphNode processNextNode(MoralGraph workingGraph) {
		
		/*MoralGraphNode node = findBestNode(workingGraph);
		createFillIns(node);
		*/
		
		/*MoralGraphNode node = findMinDomNode(workingGraph);
		createFillIns(node);
		*/
		
		// BEST HUERISTIC SO FAR
		MoralGraphNode node = findSimplicialNode(workingGraph);
		
		if (node == null) {
			node = findMinDomNode(workingGraph);
			createFillIns(node);
		}
		
		/*
		MoralGraphNode node = findMinFillInNode(workingGraph);
		createFillIns(node);
		*/
		
		return node;
	}
	
	static protected void createFillIns(MoralGraphNode node)  {
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
	
	static protected long domSize(MoralGraphNode node) {
		long dom = node.getBruseNode().getStates().size();
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			dom *= neighbor.getBruseNode().getStates().size();
		}
		
		return dom;
	}
	
	static protected MoralGraphNode findMinDomNode(MoralGraph graph) {
		ArrayList<GraphNode> nodes = graph.getNodes();
		MoralGraphNode minDomNode = null, node = null;
		long dom = 0, minDom = Long.MAX_VALUE;
		
		for (int i=0; i < nodes.size(); i++) {
			node = (MoralGraphNode)nodes.get(i);
			dom = domSize(node);
			
			if ((dom < minDom) && (dom > 0)) {
				minDom = dom;
				minDomNode = node;
			}
		}
		
		return minDomNode;
	}
	
	static protected MoralGraphNode findBestNode(MoralGraph graph) {
		ArrayList<GraphNode> nodes = graph.getNodes();
		MoralGraphNode bestNode = null, node = null;
		long dom = 0, minDom = Long.MAX_VALUE;
		
		for (int i=0; i < nodes.size(); i++) {
			node = (MoralGraphNode)nodes.get(i);
			dom = domSize(node) / node.getBruseNode().getStates().size();
			
			if ((dom < minDom) && (dom > 0)) {
				minDom = dom;
				bestNode = node;
			}
		}
		
		return bestNode;
	}
	
	static protected MoralGraphNode findMinFillInNode(MoralGraph graph) {
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
	
	static protected MoralGraphNode findSimplicialNode(MoralGraph graph) {
		ArrayList<GraphNode> nodes = graph.getNodes();
		MoralGraphNode node = null;
		
		for (int i=0; i < nodes.size(); i++) {
			node = (MoralGraphNode)nodes.get(i);
			if (isSimplicialNode(node)) return node;
		}
		
		// No simplicial node exists return null
		return null;
	}
	
	static protected boolean isSimplicialNode(MoralGraphNode node) {
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
	
	static protected ArrayList<Clique> getMaxCliques(ArrayList<Clique> cliques) {
		Clique c1, c2;
		ArrayList<Clique> maxCliques = new ArrayList<Clique>();
		maxCliques.addAll(cliques);
		
		for (int i=0; i < cliques.size(); i++) {
			c1 = cliques.get(i);
			
			for (int j=0; j < cliques.size(); j++) {
				c2 = cliques.get(j);
				if ((i != j) && (c1.isSubSet(c2))) {
					maxCliques.remove(c1);
				}
			}
		}
		
		return maxCliques;
	}
	
	static protected void removeNode(MoralGraph graph, MoralGraphNode node) {
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			
			neighbor.removeNeighbor(node);
		}
		
		graph.removeNode(node);
	}
}
