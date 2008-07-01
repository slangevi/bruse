package sc.bruse.engine.propagation.wrapper;

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
import java.util.Iterator;
import java.util.LinkedList;

import sc.bruse.engine.*;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.engine.propagation.hugin.HuginPropagationEngine;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseTable;

public class WrapperPropagationEngine4 extends HuginPropagationEngine {
	private ArrayList<BruseEvidence> m_softEvidence;
	private double m_prevEntropy[];
	
	public WrapperPropagationEngine4() {
		super();
		m_softEvidence = new ArrayList<BruseEvidence>();
	}
	
	public void init() {
		// Ensure all soft evidence nodes are put in one clique so we can calculate their joint
		// This relies on the Big Clique implementation for the HuginPropagationEngine
		m_sEvidence.addAll(m_softEvidence);
		super.init();
		m_sEvidence.clear();  // Remove the soft evidence so the HuginPropagationEngine does not use Big Clique algo
	}
	
	private void removeNode(BruseNode node) {
		// cleanup by removing vnode
		m_network.removeNode(node);
		
		LinkedList<BruseNode> parents = node.getParents();
		Iterator<BruseNode> it = parents.iterator();
		
		while (it.hasNext()) {
			it.next().getChildren().remove(node);
		}
	}
	
	private BruseNode createVirtualNode(BruseTable seTable) {
		try {
			// create virtual node
			BruseNode vnode = new BruseNode("__VE__");
			vnode.addState("false", 0);
			vnode.addState("true", 0);
			
			// create the domain for the table
			ArrayList<BruseNode> domain = new ArrayList<BruseNode>();
			
			// add each soft evidence node as a parent			
			for (int j=0; j < seTable.getVariables().length; j++) {
				BruseEvidence ev = m_softEvidence.get(j);
				BruseNode parent = seTable.getVariables()[j];
				vnode.addParent(parent);
				parent.addChild(vnode);
				domain.add(parent);
			}
			
			// Add the vnode as the last member of domain
			domain.add(vnode);
			
			BruseTable vtable = new BruseTable(domain);
			double vals[] = new double[seTable.getTableValues().length*2];
			
			for (int j=0; j < vals.length; j+=2) {
				double val = seTable.getTableValues()[j/2];
				vals[j] = (1-val);
				vals[j+1] = val;
			}
			vtable.setTableValues(vals);
			
			// set the virtual evidence node table
			vnode.setTable(vtable);
			
			// Add the vnode to the network
			m_network.addNode(vnode);
			
			return vnode;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private ArrayList<String> getSoftEvidenceNames() {
		ArrayList<String> senames = new ArrayList<String>();
		
		for (int i=0; i < m_softEvidence.size(); i++) {
			senames.add(m_softEvidence.get(i).getNodeName());
		}
		
		return senames;
	}
	
	private BruseTable getJointTable() {
		BruseTable joint = null;
			
		joint = m_cliques.get(0).getTable();
		joint = joint.getMarginal(getSoftEvidenceNames());
		
		return joint;
	}

	@Override
	public void propagate() {
		long StartTime = System.currentTimeMillis();
		
		// Propagate normally on hard evidence
		super.propagate();
		
		// if there is no soft evidence then do a normal propagate and return
		if (m_softEvidence.size() == 0) return;
		
		// calculate the P(S) where S is the soft evidence
		BruseTable margtable = getJointTable();
			
		BruseEvidence finding = null;
		BruseTable softFinding = null;
		ArrayList<BruseTable> evidence = new ArrayList<BruseTable>();
		
		for (int i=0; i < m_softEvidence.size(); i++) {
			finding = m_softEvidence.get(i);
			softFinding = finding.getTable(m_network);
			evidence.add(softFinding);
		}
		
		try {
			long StartTime1 = System.currentTimeMillis();
			
			BruseTable setable = IPFP.absorb(margtable, evidence);
			
			long EndTime = System.currentTimeMillis();
			BookKeepingMgr.TimeApplySoftEvidence = (EndTime - StartTime1);
			
			// create virtual evidence table
			setable = setable.divideBy(margtable);
			setable.normalize();
			
			// create virtual evidence node
			BruseNode vnode = createVirtualNode(setable);
			
			// Add the vnode as a potential to a clique that contains an se node	
			Clique c = m_cliques.get(0);
			c.resetPotentials();
			c.addNode(vnode); //test
			c.addPotential(vnode.getTable());
			c.setInitPotentials();
			c.rebuildTable();
			
			// Add hard evidence for the virtual node
			BruseEvidence ev = new BruseEvidence(vnode);
			ev.setHardEvidence("true");
			this.addEvidence(ev);
			
			// set that the engine is now dirty
			m_isDirty = true;
			
			// re-propagate the virtual evidence
			super.propagate();
			
			EndTime = System.currentTimeMillis();
			BookKeepingMgr.TimePropagation = (EndTime - StartTime);
			
			// cleanup by removing vnode
			removeNode(vnode);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
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
}
