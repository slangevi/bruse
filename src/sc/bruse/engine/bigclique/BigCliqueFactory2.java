package sc.bruse.engine.bigclique;

import java.util.ArrayList;

import sc.bruse.api.*;
import sc.bruse.engine.*;

public class BigCliqueFactory2 extends CliqueFactory2 {

	static public ArrayList<Clique> createCliques(BruseNetwork network, ArrayList<BruseEvidence> softEvidence) {
		// Create a moral graph for bruse network
		MoralGraph moralGraph = new MoralGraph(network);
		
		connectSENodes(moralGraph, softEvidence);
		
		// Triangulate and generate cliques
		ArrayList<Clique> cliques = triangulate(moralGraph);
		
		// remove subset cliques
		cliques = getMaxCliques(cliques);
		
		setCliqueStats(cliques);
		
		//dumpSize(cliques);
		
		Clique clique = null, bigClique = null;
		// TODO do this more efficiently
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
		assignPotentials(network, cliques);
		
		// return cliques
		return cliques;
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
