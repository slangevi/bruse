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

public class WrapperPropagationEngine extends HuginPropagationEngine {

	private BruseNode m_jnode;
	private ArrayList<BruseEvidence> m_softEvidence;
	
	public WrapperPropagationEngine() {
		super();
		m_softEvidence = new ArrayList<BruseEvidence>();
	}
	
	public void init() {
		m_jnode = createJointNode();
		super.init();
	}
	
	private BruseNode createJointNode() {
		try {
			BruseEvidence finding = null;
			BruseNode parent = null;

			// create joint  node
			BruseNode vnode = new BruseNode("__JT__");
		
			// create the domain for the table
			ArrayList<BruseNode> domain = new ArrayList<BruseNode>();
			int stateSize = 1;
			
			// add each soft evidence node as a parent
			for (int i=0; i < m_softEvidence.size(); i++) {
				finding = m_softEvidence.get(i);
				parent = m_network.getNode(finding.getNodeName());
				vnode.addParent(parent);
				parent.addChild(vnode);
				domain.add(parent);
				stateSize = stateSize * parent.getStates().size();
			}
		
			// Add the vnode as the last member of domain
			domain.add(vnode);
			
			double vals[] = new double[stateSize*stateSize];
			
			for (int i=0; i < stateSize; i++) {
				vnode.addState(Integer.toString(i), 0);
				vals[i*stateSize + i] = 1;
			}
			BruseTable vtable = new BruseTable(domain);
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
	
	private void removeNode(BruseNode node) {
		// cleanup by removing vnode
		m_network.removeNode(node);
		
		LinkedList<BruseNode> parents = node.getParents();
		Iterator<BruseNode> it = parents.iterator();
		
		while (it.hasNext()) {
			it.next().getChildren().remove(node);
		}
	}
	
	private BruseTable getJointTable() {
		try {
			BruseEvidence finding = null;
			BruseNode node;
			
			// create the domain for the table
			ArrayList<BruseNode> domain = new ArrayList<BruseNode>();
			
			// add each soft evidence node as a member of domain
			for (int i=0; i < m_softEvidence.size(); i++) {
				finding = m_softEvidence.get(i);
				node = m_network.getNode(finding.getNodeName());
				domain.add(node);
			}
			BruseTable jtable = new BruseTable(domain);
			
			for (int i=0; i < m_jnode.getStateValues().length; i++) {
				jtable.getTableValues()[i] = m_jnode.getStateValues()[i];
			}
			/*StateIterator it = new StateIterator(domain);
			
			int i = 0;
			while (it.hasMoreStates()) {
				Hashtable<String, Integer> state = it.nextState();
				state.put(Integer.toString(i), i);
				double val = jtable.getValue(state);
				tmp.getTableValues()[i] = val;
				i++;
			}*/
			
			return jtable;
		}
		catch (Exception e) {
			return null;
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

	@Override
	public void propagate() {
		long StartTime = System.currentTimeMillis();
		
		// Propagate normally on hard evidence
		super.propagate();
		
		// if there is no soft evidence then do a normal propagate and return
		if (m_softEvidence.size() == 0) {
			super.propagate();
			return;
		}
		
		// calculate the P(S) where S is the soft evidence
		BruseTable margtable = getJointTable();

		// done with joint node - remove from network
		removeNode(m_jnode);
			
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
		}

	}
	
	/*
	public void propagate() {
		long StartTime = System.currentTimeMillis();
		
		// Propagate normally on hard evidence
		super.propagate();
		
		// if there is no soft evidence then we are done
		if (m_softEvidence.size() == 0) return;
		
		// calculate the P(S) where S is the soft evidence
		BruseTable margtable = getMarginal(getEvidenceVarNames(m_softEvidence));
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
			
			// create virtual evidence node
			BruseNode vnode = new BruseNode("__VE__");
			vnode.addState("false", 0);
			vnode.addState("true", 0);
			
			// create the domain for the table
			ArrayList<BruseNode> domain = new ArrayList<BruseNode>();
			
			// add each soft evidence node as a parent
			for (int i=0; i < m_softEvidence.size(); i++) {
				BruseEvidence ev = m_softEvidence.get(i);
				BruseNode parent = m_network.getNode(ev.getNodeName());
				vnode.addParent(parent);
				parent.addChild(vnode);
				domain.add(parent);
			}
			
			// Add the vnode as the last member of domain
			domain.add(vnode);
			
			// create virtual evidence table
			setable = setable.divideBy(margtable);
			setable.normalize();
			
			BruseTable vtable = new BruseTable(domain);
			double vals[] = new double[setable.getTableValues().length*2];
			
			for (int i=0; i < vals.length; i+=2) {
				double val = setable.getTableValues()[i/2];
				vals[i] = (1-val);
				vals[i+1] = val;
			}
			vtable.setTableValues(vals);
			
			// set the virtual evidence node table
			vnode.setTable(vtable);
			
			// Add the vnode to the network
			m_network.addNode(vnode);
			
			// Add the vnode as a potential to a clique that contains one of the soft evidence nodes			
			for (int i=0; i < m_cliques.size(); i++) {
				Clique c = m_cliques.get(i);
				
				if (c.containsNode(m_softEvidence.get(0).getNodeName())) {
					c.resetPotentials();
					c.addNode(vnode); //test
					c.addPotential(vtable);
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
			m_network.removeNode(vnode);
			
			// remove vnode as child from soft evidence nodes
			for (int i=0; i < m_softEvidence.size(); i++) {
				ev = m_softEvidence.get(i);
				BruseNode parent = m_network.getNode(ev.getNodeName());
				parent.getChildren().remove(vnode);
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}*/
	
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
