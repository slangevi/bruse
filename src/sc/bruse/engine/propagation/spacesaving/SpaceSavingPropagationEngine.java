package sc.bruse.engine.propagation.spacesaving;

import java.util.ArrayList;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.IPFP;
import sc.bruse.engine.Clique;
import sc.bruse.engine.JunctionNode;
import sc.bruse.engine.JunctionSeparator;
import sc.bruse.engine.propagation.hugin.HuginPropagationEngine;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseTable;

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

public class SpaceSavingPropagationEngine extends HuginPropagationEngine {

	private static double IPFP_DELTA = 0.00000001;	// convergence stopping condition for IPFP
	
	private ArrayList<BruseEvidence> m_softEvidence;

	public SpaceSavingPropagationEngine() {
		super();
		m_softEvidence = new ArrayList<BruseEvidence>();
	}

	@Override
	public void init() {
		// TODO any initializing should be done here
		super.init();
	}

	@Override
	public void propagate() {
		
		// Don't propagate unless the dirty flag is set - evidence has changed or a propagate has not been done yet
		if (m_isDirty == false) return;
		
		long StartTime = System.currentTimeMillis();
		
		// apply hard evidence - do a full propagation
		super.propagate();
		
		BruseEvidence se;
		Clique clique;
		JunctionNode node;
		BruseTable seTable, cTable, vTable;
		int k = 0, count = 0, i = 0, m = m_softEvidence.size();
		
		while (true) {
			if (converged()) break;
			
			// Clear the separators for this iteration run
			m_junctionTree.resetSeparators();
			
			se = m_softEvidence.get(i);
			seTable = se.getTable(m_network);
			
			// Find clique and junction node that contains this soft evidence var
			node = this.findRelNode(se.getNodeName());
			clique = node.getClique();
			cTable = clique.getTable();
			
			vTable = seTable.divideBy(cTable.getMarginal(se.getNodeName()));
			
			// Update clique table
			clique.setTable(cTable.multiplyBy(vTable));
			
			// distribute the information from this clique
			redistributeEvidence(node, null);
			
			super.calculateMarginals();
			
			// Move to next soft evidence finding
			i = ++count % m;
			
			k++;
		}
		
		// calculate the marginals
		super.calculateMarginals();
		
		BookKeepingMgr.NumIPFPIterations = k;
		
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimeApplySoftEvidence = (EndTime - StartTime);
		BookKeepingMgr.TimePropagation = (EndTime - StartTime);
		
		// no longer dirty
		m_isDirty = false;
	}
	
	protected void redistributeEvidence(JunctionNode node, JunctionNode caller) {
		JunctionSeparator separator;
		BruseTable nodeTable, msg;
		
		// if this is not the root then multiply the node by the outer msg of separator with the caller
		if (caller != null) {
			separator = node.getSeparator(caller);
			nodeTable = node.getClique().getTable();
		
			msg = separator.getOuterMsg().divideBy(nodeTable.getMarginal(getVarNames(separator.getVariables())));
			node.getClique().setTable(nodeTable.multiplyBy(msg));
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
				redistributeEvidence(neighbor, node);
			}
		}
	}
	
	@Override
	public void removeAllEvidence() { 
		super.removeAllEvidence();
		m_softEvidence.clear();
	}
	
	@Override
	public ArrayList<BruseEvidence> getEvidence() {
		ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
		evidence.addAll(m_hEvidence);
		evidence.addAll(m_softEvidence);
		return evidence;
	}
	
	@Override
	public void addEvidence(BruseEvidence evidence) {
		if (evidence.getType() == BruseEvidence.EvidenceType.HARD) {
			// add to hard evidence
			m_hEvidence.add(evidence);
		}
		else {
			// add to soft evidence to private soft evidence container
			m_softEvidence.add(evidence);
		}
		
		// evidence has changed need to propagate
		m_isDirty = true;
	}
	
	private JunctionNode findRelNode(String se) {
		ArrayList<JunctionNode> nodes = this.m_junctionTree.getNodes();
		JunctionNode result = null;
		
		for (int i=0; i < nodes.size(); i++) {
			if (nodes.get(i).getClique().containsNode(se)) {
				result = nodes.get(i);
				break;
			}
		}
		
		return result;
	}
	
	private boolean converged() {
		BruseTable seTable, curTable;
		double curEntropy, diff;
		
		try {
		
			for (int i=0; i < m_softEvidence.size(); i++) {
				seTable = m_softEvidence.get(i).getTable(m_network);
				curTable = getMarginal(m_softEvidence.get(i).getNodeName());
			
				curEntropy = IPFP.getCrossEntropy(curTable, seTable);
				
				/*diff = Math.abs(curEntropy - m_prevEntropy[i]);
				
				m_prevEntropy[i] = curEntropy;
				
				// If entropy measure is within IPFP_DELTA threshold then we have converged
				if (diff > 0.0001) return false;*/
				if (curEntropy > IPFP_DELTA) return false;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public BruseTable getMarginal(String var) {
		BruseTable table = null;
		BruseNode node = null;
		
		try {
			node = m_network.getNode(var);
			table = new BruseTable(node);
			//TODO make sure this is correct
			table.setTableValues(node.getStateValues());
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return table;
	}
}
