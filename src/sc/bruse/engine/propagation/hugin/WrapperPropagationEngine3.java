package sc.bruse.engine.propagation.hugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import sc.bruse.api.BruseEvidence;
import sc.bruse.api.BruseNode;
import sc.bruse.api.BruseTable;
import sc.bruse.engine.*;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;

public class WrapperPropagationEngine3 extends HuginPropagationEngine {

	private ArrayList<BruseEvidence> m_softEvidence;
	private double m_prevEntropy[];
	
	public WrapperPropagationEngine3() {
		super();
		m_softEvidence = new ArrayList<BruseEvidence>();
	}
	
	public void init() {
		super.init();
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
			for (int j=0; j < m_softEvidence.size(); j++) {
				BruseEvidence ev = m_softEvidence.get(j);
				BruseNode parent = m_network.getNode(ev.getNodeName());
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
		// Find a clique containing a SE node, it will contain the Joint(SE) since we did not marginalize any SE
		
		// TODO should also check separators
		for (int i=0; i < m_cliques.size(); i++) {
			Clique clique = m_cliques.get(i);
			
			for (int j=0; j < m_softEvidence.size(); j++) {
				if (clique.containsNode(m_softEvidence.get(j).getNodeName())) {
					joint = clique.getTable().getMarginal(getSoftEvidenceNames());
				}
			}
		}
		
		return joint;
	}
	
	private void addSEtoSeparators() {
		ArrayList<JunctionSeparator> seps = m_junctionTree.getSeparators();
		
		if (m_softEvidence.size() == 0) return;
		
		try {
			for (int i=0; i < seps.size(); i++) {
				JunctionSeparator sep = seps.get(i);
				for (int j=0; j < m_softEvidence.size(); j++) {
					String se = m_softEvidence.get(j).getNodeName();
					if (!sep.containsVariable(se)) {
						sep.getVariables().add(m_network.getNode(se));
					}
				}
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void propagate() {
		long StartTime = System.currentTimeMillis();
		
		// Prevent marginalizations of SE vars during propagation so we can readily calculate Joint(SE)
		addSEtoSeparators();
		
		// Propagate normally on hard evidence
		super.propagate();
		
		// if there is no soft evidence then do a normal propagate and return
		if (m_softEvidence.size() == 0) return;
		
		// calculate the P(S) where S is the soft evidence
		BruseTable margtable = getJointTable();
			
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
			
			// Add the vnode as a potential to a clique that contains an se node		
			for (int i=0; i < m_cliques.size(); i++) {
				Clique c = m_cliques.get(i);
				
				if (c.containsNode(m_softEvidence.get(0).getNodeName())) {
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
