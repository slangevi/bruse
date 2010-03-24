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

import sc.bruse.network.*;

/***
 * 
 * This class uses BayesBall to determine d-separation properties of a BruseNetwork
 * 
 * @author scott
 */
public class DSeparationAnalyzer {

	private BruseNetwork m_network;
	static private String ATTR_TOP = "top";
	static private String ATTR_BOTTOM = "bottom";
	private Hashtable<String, Boolean> m_evidence;
	private ArrayList<BruseNode> m_dsNodes;
	
	public DSeparationAnalyzer(BruseNetwork net) {
		m_network = net;
		m_evidence = new Hashtable<String, Boolean>();
		m_dsNodes = new ArrayList<BruseNode>();
	}
	
	private void init(ArrayList<String> query, ArrayList<String> evidence) throws BruseAPIException {
		BruseNode node = null;
		
		m_evidence.clear();
		m_dsNodes.clear();

		// clear the top and bottom marks of all nodes
		for (int i=0; i < m_network.getAllNodes().size(); i++) {
			node = m_network.getAllNodes().get(i);
			node.setAttribute(ATTR_TOP, "");
			node.setAttribute(ATTR_BOTTOM, "");
			m_evidence.put(node.getName(), false);
			
			m_dsNodes.add(node);	// initially all nodes are added to the dsNodes
		}
		
		// set the evidence
		for (int j=0; j < evidence.size(); j++) {
			m_evidence.put(evidence.get(j), true);
		}
	}
	
	public ArrayList<BruseNode> getDconnection(ArrayList<String> query, ArrayList<String> evidence) {
		ArrayList<BruseNode> result = new ArrayList<BruseNode>();
		ArrayList<BruseNode> dsep = null;
		BruseNode node = null;
	
		dsep = getDseparation(query, evidence);
				
		for (int i=0; i < m_network.getAllNodes().size(); i++) {
			node = m_network.getAllNodes().get(i);
			if (dsep.contains(node) == false) {
				result.add(node);
			}
		}
		
		return result;
	}
	
	public ArrayList<BruseNode> getDseparation(ArrayList<String> query, ArrayList<String> evidence) {
		ArrayList<BruseNode> result = null;
		BruseNode node = null;
	
		//TODO review dseparation - I think I should consider anything not requisite as d-separated rather than not visited
		try {
			init(query, evidence);
			
			for (int i=0; i < query.size(); i++) {
				node = m_network.getNode(query.get(i));
				
				visitFromChild(node);
				
				// TEST
//				if (hasEvidence(node)) {
//					for (BruseNode parent: node.getParents()) {
//						visitFromChild(parent);
//					}
//					for (BruseNode child: node.getChildren()) {
//						visitFromParent(child);
//					}
//				}
			}
			
			return m_dsNodes;
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	private void visitFromParent(BruseNode node) {
		if (hasEvidence(node)) {
			if (!isTopMarked(node)) {
				// visit all parents
				markTop(node);
				visitParents(node);
			}
		}
		else {
			if (!isBottomMarked(node)) {
				// visit all children
				markBottom(node);
				visitChildren(node);
			}
		}
		// node is visited so not dseparated
		m_dsNodes.remove(node);
	}
	
	private void visitFromChild(BruseNode node) {
		if (!hasEvidence(node)) {
			if (!isTopMarked(node)) {
				// visit all parents
				markTop(node);
				visitParents(node);
			}
		
			if (!isBottomMarked(node)) {
				// visit all children
				markBottom(node);
				visitChildren(node);
			}
		}
		// node is visited so not dseparated
		m_dsNodes.remove(node);
	}
	
	private void visitChildren(BruseNode node) {
		ListIterator<BruseNode> it = node.getChildren().listIterator();
		
		while (it.hasNext()) {
			visitFromParent(it.next());
		}
	}
	
	private void visitParents(BruseNode node) {
		ListIterator<BruseNode> it = node.getParents().listIterator();
		
		while (it.hasNext()) {
			visitFromChild(it.next());
		}
	}
	
	private boolean hasEvidence(BruseNode node) {
		return m_evidence.get(node.getName());
	}
	
	private boolean isTopMarked(BruseNode node) {
		return (node.getAttribute(ATTR_TOP).compareTo("*") == 0);
	}
	
	private boolean isBottomMarked(BruseNode node) {
		return (node.getAttribute(ATTR_BOTTOM).compareTo("*") == 0);
	}
	
	private void markTop(BruseNode node) {
		node.setAttribute(ATTR_TOP, "*");
	}
	
	private void markBottom(BruseNode node) {
		m_dsNodes.remove(node);	// nodes marked on bottom are not d-separated
		node.setAttribute(ATTR_BOTTOM, "*");
	}
}
