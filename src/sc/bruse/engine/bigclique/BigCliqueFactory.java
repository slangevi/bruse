package sc.bruse.engine.bigclique;

import sc.bruse.api.BruseEvidence;
import sc.bruse.api.BruseNode;
import sc.bruse.engine.*;

import java.util.*;

public class BigCliqueFactory extends CliqueFactory {
	
	static public ArrayList<Clique> createCliques(MoralGraph graph, ArrayList<BruseEvidence> softEvidence) {
		// create a list of cliques from the moral graph but also create a Big Clique from the soft evidence
		// order the list of cliques so that the first clique is the Big Clique
		Clique bigClique = null, clique = null;
		ArrayList<BruseNode[]> families = new ArrayList<BruseNode[]>();
		ArrayList<Clique> cliques = new ArrayList<Clique>();
		ArrayList<GraphNode> neighbors = null;
		MoralGraphNode node = null, neighbor = null;
		BruseNode family[];
		
		MoralGraph workingGraph = (MoralGraph)graph.clone();
		
		//TODO this is duplicate code from CliqueFactory
		// see if we can use helper routines to minimize code duplication
		while (workingGraph.size() > 0) {
			node = Triangulation.findSimplicialNode(workingGraph);
			family = new BruseNode[node.getNeighbors().size()+1];//.clear();
			neighbors = node.getNeighbors();
			
			// add the simplicial node to the family
			family[0] = node.getBruseNode();
			//family.add(node.getBruseNode());
			
			// add the simplicial nodes neighbors to the family
			for (int i=0; i < neighbors.size(); i++) {
				neighbor = (MoralGraphNode)neighbors.get(i);
				family[i+1] = neighbor.getBruseNode();
			}
			
			// save family
			families.add(family);
			
			// if the family of the simplicial node includes 
			// all the remaining nodes then we are done
			if (workingGraph.size() == family.length) break;
				
			// remove simplicial node from working Graph
			removeNode(workingGraph, node);
		}
		
		// remove the subset cliques and return the maximal cliques
		cliques = getMaxCliques(families);
		
		//TODO do this more efficiently
		// find the big clique and set as first clique in list
		for (int i=0; i < cliques.size(); i++) {
			clique = cliques.get(i);
			bigClique = clique;
			for (int j=0; j < softEvidence.size(); j++) {
				if (clique.containsNode(softEvidence.get(j).getNodeName()) == false) {
					bigClique = null;
					break;
				}
			}
			if (bigClique != null) break;
		}
		cliques.remove(bigClique);
		cliques.add(0, bigClique);
		
		// assign potentials to cliques
		assignPotentials(graph, cliques);
		
		// the clique list should be ordered so the first element is the big clique if it exists
		return cliques;
	}
	
	private static boolean isSENode(BruseNode node, ArrayList<BruseEvidence> softEvidence) {
		for (int i=0; i < softEvidence.size(); i++) {
			if (softEvidence.get(i).getNodeName().compareToIgnoreCase(node.getName()) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isSENode(GraphNode node, ArrayList<BruseEvidence> softEvidence) {
		for (int i=0; i < softEvidence.size(); i++) {
			if (softEvidence.get(i).getNodeName().compareToIgnoreCase(node.getName()) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void connectSENodes(MoralGraph graph, ArrayList<BruseEvidence> softEvidence) {
		GraphNode n1, n2;
		
		// add edges between each pair of soft evidence nodes
		for (int i=0; i < softEvidence.size(); i++) {
			n1 = graph.getNode(softEvidence.get(i).getNodeName());
			
			for (int j=0; j < softEvidence.size(); j++) {
				n2 = graph.getNode(softEvidence.get(j).getNodeName());
				
				if (n1 != n2) {
					n1.addNeighbor(n2);
				}
			}
		}
	}
	
}
