package sc.bruse.engine;

import java.util.*;

import sc.bruse.api.*;

/***
 * Generates a vector of Cliques for the given moral graph
 * 
 * @author langevin
 *
 */
public class CliqueFactory {

	/***
	 * Creates a list of cliques for the passed in triangulated moral graph
	 * 
	 * @param graph is the triangulated moral graph
	 * @return a vector of cliques
	 */
	static public ArrayList<Clique> createCliques(MoralGraph graph) {
		ArrayList<Clique> cliques = new ArrayList<Clique>();
		MoralGraph workingGraph = (MoralGraph)graph.clone();
		ArrayList<BruseNode[]> families = new ArrayList<BruseNode[]>();
		
		while (workingGraph.size() > 0) {
			MoralGraphNode node = Triangulation.findSimplicialNode(workingGraph);
			BruseNode family[] = new BruseNode[node.getNeighbors().size() + 1];
			ArrayList<GraphNode> neighbors = node.getNeighbors();
			
			// add the simplicial node to the family
			family[0] = node.getBruseNode();
			
			// add the simplicial nodes neighbors to the family
			for (int i=0; i < neighbors.size(); i++) {
				MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
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
		
		// assign potentials to cliques
		assignPotentials(graph, cliques);
		
		return cliques;
	}
	
	public static void assignPotentials(MoralGraph graph, ArrayList<Clique> cliques) {
		Clique clique = null;
		MoralGraphNode node = null;
		BruseTable table = null;
		
		// Uniquely assign a potential to a clique that covers the domain of the potential
		// If there are several qualifying cliques we choose the first clique
		// TODO: Try a different hueristic where we assign the potential to the clique
		// with the min domain difference. 
		// Ex: pot(A,B) matches Clique(A,B,C,D) and Clique(A,B,E)
		//		choose Clique(A,B,E) because it has the min domain difference
		for (int i=0; i < graph.getNodes().size(); i++) {
			node = (MoralGraphNode)graph.getNodes().get(i);
			table = node.getBruseNode().getTable();
			
			for (int j=0; j < cliques.size(); j++) {
				clique = cliques.get(j);
				
				if (clique.containsNodes(table.getVariableNames())) {// getMembers().containsAll(table.getVariables())) {
					clique.addPotential(table);
					break;
				}
			}
		}
		
		// all cliques now have initial potentials set - this is for resetting potentials
		for (int i=0; i < cliques.size(); i++) {
			clique = cliques.get(i);
			clique.setInitPotentials();
		}
	}
	
	protected static void removeNode(MoralGraph graph, MoralGraphNode node) {
		ArrayList<GraphNode> neighbors = node.getNeighbors();
		
		for (int i=0; i < neighbors.size(); i++) {
			MoralGraphNode neighbor = (MoralGraphNode)neighbors.get(i);
			
			neighbor.removeNeighbor(node);
		}
		
		graph.removeNode(node);
	}
	
	protected static boolean isSubSetFamily(BruseNode[] family1, BruseNode[] family2) {
		boolean found = false;
		
		for (int i=0; i < family1.length; i++) {
			found = false;
			
			for (int j=0; j < family2.length; j++) {
				if (family1[i] == family2[j]) found = true;
			}
			if (found == false) return false;
		}
		
		return true;
	}
	
	protected static ArrayList<Clique> getMaxCliques(ArrayList<BruseNode[]> families) {
		ArrayList<Clique> maxCliques = new ArrayList<Clique>();
		BruseNode[] family = null, test = null;
		boolean isSubSet = false;
		
		// init to all the cliques, we will remove subset cliques below
		//maxCliques.addAll(cliques);
		
		for (int i=0; i < families.size(); i++) {
			family = families.get(i);
			isSubSet = false;
			
			for (int j=0; j < families.size(); j++) {
				if (i != j) {
					test = families.get(j);
					if (isSubSetFamily(family, test)) {
						isSubSet = true;
						break;
					}
				}
			}
			
			if (isSubSet == false) {
				maxCliques.add(new Clique(family));
			}
		}
		
		return maxCliques;
	}
}
