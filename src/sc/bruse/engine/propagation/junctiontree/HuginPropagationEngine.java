package sc.bruse.engine.propagation.junctiontree;

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

import sc.bruse.engine.*;
import sc.bruse.engine.bigclique.*;
import sc.bruse.engine.propagation.*;
import sc.bruse.network.*;

/***
 * Lazy Hugin Propagation
 * 
 * @author langevin
 *
 */
public class HuginPropagationEngine extends PropagationEngine {
	protected ArrayList<Clique> m_cliques;
	protected JunctionTree m_junctionTree;
	
	
	public HuginPropagationEngine() {
		super();
	}
	
	public void init() {
		long StartTime = System.currentTimeMillis();
		
		/*// gen moral graph - do we need to keep this around?
		MoralGraph moralGraph = new MoralGraph(m_network);
		
		// add edges between each pair of soft evidence nodes
		BigCliqueFactory.connectSENodes(moralGraph, m_sEvidence);
		
		long StartTime = System.currentTimeMillis();
		
		// triangulate graph
		Triangulation.triangulate(moralGraph);
		
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimeTriangulation = (EndTime - StartTime);
		
		StartTime = System.currentTimeMillis();
		
		// gen cliques using the big clique 
		m_cliques = BigCliqueFactory.createCliques(moralGraph, m_sEvidence);
		
		long sum = 0;
		
		for (int i=0; i < m_cliques.size(); i++) {
			sum += m_cliques.get(i).getTable().getTableValues().length;
		}
		
		System.out.println("Triangulation size: " + sum);*/
		
		m_cliques = BigCliqueFactory.createCliques(m_network, m_sEvidence);
		
		//EndTime = System.currentTimeMillis();
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimeCreateCliques = (EndTime - StartTime);
		
		// TEST
		/*BruseTable.dumpTable(m_cliques.firstElement().getTable(), true);
		ArrayList<String> names = new ArrayList<String>();
		names.add("K");
		BruseTable.dumpTable(m_cliques.firstElement().getTable().getMarginal(names), true);
		*/
		
		StartTime = System.currentTimeMillis();
		
		// create junction tree
		m_junctionTree = new JunctionTree(m_cliques);
		
		EndTime = System.currentTimeMillis();		
		BookKeepingMgr.TimeCreateJunctionTree = (EndTime - StartTime);

		
		//TEST
		//JunctionTree.dumpJunctionTree(m_junctionTree);
		
		// set the root to the big clique in the junction tree
		// the BigCliqueFactory sets the first element in the list
		// to the Big Clique, if none is found whatever is the first element
		// is chosen
		m_junctionTree.setRoot(0);
		
		// propagation is needed since we have rebuilt the junction tree
		m_isDirty = true;
	}
	
	public void propagate() {
		
		//TODO should check if init() was called first
	
		// Don't propagate unless the dirty flag is set - evidence has changed or a propagate has not been done yet
		if (m_isDirty == false) return;
		
		long StartTime = System.currentTimeMillis();

		// Init the cliques - remove previous propagation values from cliques
		initCliques();

		// Reset the separators - remove previous propagation values from separators
		m_junctionTree.resetSeparators();
		
		// first apply hard evidence
		applyHardEvidence();
		
		// next collect evidence to root
		collectEvidence(m_junctionTree.getRoot(), null);
		
		// TEST
		//System.out.println("After collect evidence:");
		//JunctionTree.dumpJunctionTree(m_junctionTree);
		
		// apply the soft evidence to the big clique
		applySoftEvidence();
		
		// next distribute evidence from root
		distributeEvidence(m_junctionTree.getRoot(), null);
		
		// TEST
		//System.out.println("After distribute evidence:");
		//JunctionTree.dumpJunctionTree(m_junctionTree);
		
		// calculate the posterior marginals for each variable in the BN
		calculateMarginals();
		
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimePropagation = (EndTime - StartTime);
		
		// no longer dirty
		m_isDirty = false;
	}
	
	/*public BruseTable getMarginal(ArrayList<String> vars) {
		BruseTable minDomTable = null;
		
		//TODO should first search separators
		
		for (int i=0; i < m_cliques.size(); i++) {
			BruseTable table = m_cliques.get(i).getTable();
			
			if (table.containsVariables(vars)) {
				if (minDomTable == null) {
					minDomTable = table;
				}
				else if (table.getVariables().size() < minDomTable.getVariables().size()){
					minDomTable = table;
				}
			}
		}
		
		// marginalize the minDomTable on variables
		minDomTable = minDomTable.getMarginal(vars);
		
		// normalize the marginal table
		minDomTable.normalize();
		
		return minDomTable;
	}*/
	/*
	public BruseTable getMarginal(ArrayList<String> vars) {
		BruseTable table = null, marg = null;
		String var = null;
		BruseNode node = null;
		
		try {
			for (int i=0; i < vars.size(); i++) {
				for (int j=0; j < m_cliques.size(); j++) {
					Clique clique = m_cliques.get(j);
					if (clique.containsNode(vars.get(i))) {
						if (marg == null) {
							marg = clique.getTable().getMarginal(vars);
						}
						else {
							marg = marg.multiplyBy(clique.getTable().getMarginal(vars));
						}
						break;
					}
				}
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return marg;
	}*/
	
	public ArrayList<Clique> getCliques() {
		return m_cliques;
	}
	
	public void setDirty() {
		m_isDirty = true;
	}
	
	protected void collectEvidence(JunctionNode node, JunctionNode caller) {
		for (int i=0; i < node.getNeighbors().size(); i++) {
			JunctionNode neighbor = (JunctionNode)node.getNeighbors().get(i);
			
			// call collectEvidence on all neighbors except caller
			if (neighbor != caller) {
				// collect evidence from neighbor
				collectEvidence(neighbor, node);
			
				// multiply this nodes table by the innerMsg of separator with neighbor
				BruseTable msg = node.getSeparator(neighbor).getInnerMsg();
				node.getClique().getTable().absorb(msg);
				/*BruseTable nodeTable = node.getClique().getTable();
				nodeTable = nodeTable.multiplyBy(msg);
				node.getClique().setTable(nodeTable);*/
			}
		}
		
		// after all neighbors have returned evidence send message to separator

		if (caller != null) {
			// calculate marginal with separator
			JunctionSeparator separator = node.getSeparator(caller);
			BruseTable nodeTable = node.getClique().getTable();
			BruseTable msg = nodeTable.getMarginal(getVarNames(separator.getVariables()));
		
			// set separator inner message
			separator.setInnerMsg(msg);
		}
	}
	
	protected ArrayList<String> getVarNames(ArrayList<BruseNode> vars) {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i=0; i < vars.size(); i++) {
			BruseNode var = vars.get(i);
			names.add(var.getName());
		}
		
		return names;
	}
	
	protected ArrayList<String> getEvidenceVarNames(ArrayList<BruseEvidence> ev) {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i=0; i < ev.size(); i++) {
			BruseEvidence evidence = ev.get(i);
			names.add(evidence.getNodeName());
		}
		
		return names;
	}
	
	protected void distributeEvidence(JunctionNode node, JunctionNode caller) {
		JunctionSeparator separator;
		BruseTable nodeTable, msg;
		
		// if this is not the root then multiply the node by the outer msg of separator with the caller
		if (caller != null) {
			separator = node.getSeparator(caller);
			nodeTable = node.getClique().getTable();
			// divide outer msg by inner msg of separator and mulitply node table by result
			msg = separator.getOuterMsg().divideBy(separator.getInnerMsg());
			//WARNING: Althought this is more efficient it destroys outer message and is dangerous
			//msg = separator.getOuterMsg().div(separator.getInnerMsg());
			nodeTable.absorb(msg);
			//node.getClique().setTable(nodeTable.multiplyBy(msg));
		}
		
		for (int i=0; i < node.getNeighbors().size(); i++) {
			JunctionNode neighbor = (JunctionNode)node.getNeighbors().get(i);
			
			if (neighbor != caller) {
				// calculate marginal with separator
				separator = node.getSeparator(neighbor);
				nodeTable = node.getClique().getTable();
				msg = nodeTable.getMarginal(getVarNames(separator.getVariables()));
			
				// set the outer message
				separator.setOuterMsg(msg);
				
				// distribute neighbors evidence
				distributeEvidence(neighbor, node);
			}
		}
	}
	
	protected void calculateMarginals() {
		// calculate the marginal prob for each variable in BN
		// search separators first, then junction nodes
		
		ArrayList<BruseNode> nodes = m_network.getAllNodes();
		for (int i=0; i < nodes.size(); i++) {
			BruseNode node = nodes.get(i);
			calculateMarginal(node);
		}
	}
	
	protected BruseTable calculateMarginal(BruseNode node) {
		ArrayList<String> varName = new ArrayList<String>();
		varName.add(node.getName());
		ArrayList<JunctionSeparator> separators = m_junctionTree.getSeparators();
		BruseTable minDomTable = null;
		
		for (int i=0; i < separators.size(); i++) {
			JunctionSeparator separator = separators.get(i);
			
			if (separator.getVariables().contains(node)) {
				if (minDomTable == null) {
					minDomTable = separator.getOuterMsg();
				}
				else if (separator.getVariables().size() < minDomTable.getVariables().length){
					minDomTable = separator.getOuterMsg();
				}
			}
			
			// if the tables domain is size 1 then break since there is no smaller domain
			if (minDomTable != null && minDomTable.getVariables().length == 1) break;
		}
		
		if (minDomTable == null) {
			for (int i=0; i < m_cliques.size(); i++) {
				BruseTable table = m_cliques.get(i).getTable();
				
				if (table.containsVariable(node.getName())) {
					if (minDomTable == null) {
						minDomTable = table;
					}
					else if (table.getVariables().length < minDomTable.getVariables().length){
						minDomTable = table;
					}
				}
			}
		}
		
		// marginalize the minDomTable on variable
		minDomTable = minDomTable.getMarginal(varName);
		
		// normalize the marginal table
		minDomTable.normalize();
		
		// update the variables states
		node.updateStates(minDomTable);
		
		return minDomTable;
	}
	
	protected void applyHardEvidence() {
		BruseEvidence finding = null;
		Clique clique = null;
		BruseNode node = null;
		
		for (int i=0; i < m_hEvidence.size(); i++) {
			finding = m_hEvidence.get(i);
			
			for (int j=0; j < m_cliques.size(); j++) {
				clique = m_cliques.get(j);
				
				if (clique.containsNode(finding.getNodeName())) {
					BruseTable ev = finding.getTable(m_network);
					//clique.setTable(clique.getTable().multiplyBy(ev));
					clique.getTable().absorb(ev);
					break;
				}
			}
		}
	}
	
	protected void applySoftEvidence() {
		// Use IPFP to absorb soft evidence
		BruseEvidence finding = null;
		BruseTable softFinding = null;
		ArrayList<BruseTable> evidence = new ArrayList<BruseTable>();
		BruseTable table = m_junctionTree.getRoot().getClique().getTable();
		//BruseTable.dumpTable(table, false);
		if (m_sEvidence.size() == 0) return;
		
		for (int i=0; i < m_sEvidence.size(); i++) {
			finding = m_sEvidence.get(i);
			softFinding = finding.getTable(m_network);
			evidence.add(softFinding);
		}
		
		try {
			long StartTime = System.currentTimeMillis();
			
			table = IPFP.absorb(table, evidence);
			
			long EndTime = System.currentTimeMillis();
			BookKeepingMgr.TimeApplySoftEvidence = (EndTime - StartTime);
			
			// update the big clique table
			m_junctionTree.getRoot().getClique().setTable(table);
		}
		catch (BruseAPIException e) {
			// TODO this will happen when IPFP fails to converge
		}
	}
	
	protected void initCliques() {
		for (int i=0; i < m_cliques.size(); i++) {
			Clique clique = m_cliques.get(i);
			long s = System.currentTimeMillis();
			clique.combinePotentials();
			long e = System.currentTimeMillis();
			//System.out.println("Clique size: " + clique.getTable().getTableValues().length + " Time: " + (e-s));
		}
	}
}
