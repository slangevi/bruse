package sc.bruse.engine.bigclique;

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

import sc.bruse.engine.*;
import sc.bruse.network.*;

public class BigCliqueFactory extends CliqueFactory {

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
