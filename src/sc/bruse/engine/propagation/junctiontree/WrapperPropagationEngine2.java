package sc.bruse.engine.propagation.junctiontree;

import java.util.*;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.Clique;
import sc.bruse.engine.IPFP;
import sc.bruse.engine.StateIterator;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseTable;

public class WrapperPropagationEngine2 extends HuginPropagationEngine {

	private ArrayList<BruseEvidence> m_softEvidence;
	private double m_prevEntropy[];
	
	public WrapperPropagationEngine2() {
		super();
		m_softEvidence = new ArrayList<BruseEvidence>();
	}
	
	public void init() {
		super.init();
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
	
	private void removeNode(BruseNode node) {
		// cleanup by removing vnode
		m_network.removeNode(node);
		
		LinkedList<BruseNode> parents = node.getParents();
		Iterator<BruseNode> it = parents.iterator();
		
		while (it.hasNext()) {
			it.next().getChildren().remove(node);
		}
	}
	
	private BruseNode createVirtualNode(String name, BruseTable seTable) {
		try {
			// create virtual node
			BruseNode vnode = new BruseNode(name);
			vnode.addState("false", 0);
			vnode.addState("true", 0);
			
			// create the domain for the table
			ArrayList<BruseNode> domain = new ArrayList<BruseNode>();
			
			// add soft evidence node as a parent
			for (int j=0; j < seTable.getVariables().length; j++) {
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
				if (curEntropy > 0.0001) return false;
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public void propagate() {
		long StartTime = System.currentTimeMillis();
		
		m_prevEntropy = new double[m_softEvidence.size()];
		
		// if there is no soft evidence then do a normal propagation and return
		if (m_softEvidence.size() == 0) {
			super.propagate();
			return;
		}
		
		int k = 1, i = 0, j = 0, m = m_softEvidence.size();
		
		String sename;
		BruseNode vnode;
		BruseEvidence se;
		BruseTable seTable, curTable, vTable;
		
		while ( true ) {
			super.propagate();
			
			if (converged()) break;
			
			i = (1 + (k - 1)) % m;
			j = 1 + (int)Math.floor((k - 1) / m);
		
			se = m_softEvidence.get(i);
			seTable = se.getTable(m_network);
			curTable = getMarginal(se.getNodeName());
			
			vTable = seTable.divideBy(curTable);
			vTable.normalize();
			
			// TEST
			sename = "__VE_" + se.getNodeName();
			try {
				vnode = m_network.getNode(sename);
				
				vTable = vnode.getTable().multiplyBy(vTable);
				vnode.getTable().setTableValues(vTable.getTableValues());
			}
			catch (Exception e) {
				vnode = createVirtualNode(sename, vTable);
				
				// Add the vnode as a potential to a clique that contains the se node	
				for (int n=0; n < m_cliques.size(); n++) {
					Clique c = m_cliques.get(n);
					
					if (c.containsNode(se.getNodeName())) {
						c.resetPotentials();
						c.addNode(vnode); //test
						c.addPotential(vnode.getTable());
						c.setInitPotentials();
						c.rebuildTable();
						break;
					}
				}
				
				// Add hard evidence for the virtual node
				BruseEvidence ev = new BruseEvidence(sename);
				ev.setHardEvidence("true");
				this.addEvidence(ev);
			}
			
			// OLD
			/*sename = "__VE_" + se.getNodeName() + "_" + Integer.toString(j) + "__";
			vnode = createVirtualNode(sename, vTable);
			
			// Add the vnode as a potential to a clique that contains the se node	
			for (int n=0; n < m_cliques.size(); n++) {
				Clique c = m_cliques.get(n);
				
				if (c.containsNode(se.getNodeName())) {
					c.resetPotentials();
					c.addNode(vnode); //test
					c.addPotential(vnode.getTable());
					c.setInitPotentials();
					break;
				}
			}*/
			
			/*// Add hard evidence for the virtual node
			BruseEvidence ev = new BruseEvidence(sename);
			ev.setHardEvidence("true");
			this.addEvidence(ev);*/
			
			// set that the engine is now dirty
			m_isDirty = true;
			
			k++;
		}
		
		BookKeepingMgr.NumIPFPIterations = k - 1;
		
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimeApplySoftEvidence = (EndTime - StartTime);
		BookKeepingMgr.TimePropagation = (EndTime - StartTime);
		
		/*
		////////
		// calculate the P(S) where S is the soft evidence
		BruseTable margtable = null; //getJointTable();
		
			
		//BruseTable.dumpTable(margtable, false);
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
			
			// Add the vnode as a potential to a clique that contains the joint node		
			for (int i=0; i < m_cliques.size(); i++) {
				Clique c = m_cliques.get(i);
				
				if (c.containsNode("__JT__")) {
					
					// Remove the JT potential from clique - no longer needed
					for (int j=0; j < c.getPotentials().size(); j++) {
						if (c.getPotentials().get(j).containsVariable("__JT__")) {
							c.getPotentials().remove(c.getPotentials().get(j));
							c.setInitPotentials();
							break;
						}
					}
				//if (c.containsNode(m_softEvidence.get(0).getNodeName())) {
					c.resetPotentials();
					c.addNode(vnode); //test
					c.addPotential(vnode.getTable());
					c.setInitPotentials();
					break;
				}
			}
			
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
		}*/

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
