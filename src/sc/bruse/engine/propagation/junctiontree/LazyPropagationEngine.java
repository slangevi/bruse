package sc.bruse.engine.propagation.junctiontree;

import sc.bruse.engine.*;
import sc.bruse.engine.bigclique.*;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.network.BruseAPIException;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseTable;

import java.util.*;

public class LazyPropagationEngine extends HuginPropagationEngine {
	
	private Hashtable<String, ArrayList<BruseNode>> m_dconnected;
	
	public LazyPropagationEngine() {
		super();
		
		m_dconnected = new Hashtable<String, ArrayList<BruseNode>>();
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
		m_cliques = BigCliqueFactory.createCliques(moralGraph, m_sEvidence);*/
		m_cliques = BigCliqueFactory.createCliques(m_network, m_sEvidence);
		long EndTime = System.currentTimeMillis();
		//EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimeCreateCliques = (EndTime - StartTime);
		
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
		// pass collections of potentials
		// exploit barren nodes
		//		Axiom: ·xP(X|pa(X) = 1
		//		rule:  If A is not in dom(C) and the only potential with A in dom is P(A|pa(A)) then A is discarded
		// exploit d-separation
		// domain reduction due to evidence
		
		// Don't propagate unless the dirty flag is set - evidence has changed or a propagate has not been done yet
		if (m_isDirty == false) return;
		
		long StartTime = System.currentTimeMillis();
		
		// dconnection must be recalculated because evidence has changed
		m_dconnected.clear();
		
		// need to rebuild the clique potential lists to initial state
		for (int i=0; i < m_cliques.size(); i++) {
			m_cliques.get(i).resetPotentials();
		}
		
		// Reset the separators - remove previous propagation values from separators
		m_junctionTree.resetSeparators();
		
		// first apply hard evidence
		applyHardEvidence();
		
		// next collect evidence to root
		collectEvidence(m_junctionTree.getRoot(), null);
	
		//JunctionTree.dumpJunctionTree(m_junctionTree);
		
		// apply the soft evidence to the big clique
		applySoftEvidence();
		
		// next distribute evidence from root
		distributeEvidence(m_junctionTree.getRoot(), null);
		
		//JunctionTree.dumpJunctionTree(m_junctionTree);
		
		// calculate the posterior marginals for each variable in the BN
		calculateMarginals();
		
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimePropagation = (EndTime - StartTime);
		
		// no longer dirty
		m_isDirty = false;
	}
	
	protected void applyHardEvidence() {
		BruseEvidence finding = null;
		Clique clique = null;
		ArrayList<BruseTable> potentials = null;
		BruseTable potential = null;
		BruseNode node = null;
		
		// associate hard evidence with every clique that contains var
		for (int i=0; i < m_cliques.size(); i++) {
			clique = m_cliques.get(i);
			potentials = clique.getPotentials();
			
			for (int j=0; j < m_hEvidence.size(); j++) {
				finding = m_hEvidence.get(j);
				
				if (clique.containsNode(finding.getNodeName())) {
					//TODO this should reduce the domain of each potential by the evidence
					clique.addPotential(finding.getTable(m_network));
					//clique.reducePotentialDomain(finding.getTable(m_network));
				}
			}
		}
	}
	
	protected void applySoftEvidence() {
		// Use IPFP to absorb soft evidence
		
		// if there is no soft evidence then skip this
		if (m_sEvidence.size() == 0) return;
		
		// first combine the potentials in the big clique so we can perform IPFP 
		m_junctionTree.getRoot().getClique().combinePotentials();
		
		// apply the soft evidence (same as Hugin)
		super.applySoftEvidence();
		
		// clear the set of potentials in the big clique since we now have an updated combined potential
		m_junctionTree.getRoot().getClique().clearPotentials();
		BruseTable table = m_junctionTree.getRoot().getClique().getTable();
		
		// add the updated potential to the list of potentials for the big clique
		m_junctionTree.getRoot().getClique().addPotential(table);
	}
	
	protected void collectEvidence(JunctionNode node, JunctionNode caller) {
		JunctionSeparator separator;
		ArrayList<BruseTable> msg = null, pot = node.getClique().getPotentials();
		
		for (int i=0; i < node.getNeighbors().size(); i++) {
			JunctionNode neighbor = (JunctionNode)node.getNeighbors().get(i);
			
			// call collectEvidence on all neighbors except caller
			if (neighbor != caller) {
				// collect evidence from neighbor
				collectEvidence(neighbor, node);
			
				// absorb innerMsg of separator with neighbor
				msg = node.getSeparator(neighbor).getInnerMsgList();
				node.getClique().addPotentials(msg);
			}
		}
		
		// after all neighbors have returned evidence send message to separator

		if (caller != null) {
			separator = node.getSeparator(caller);
			// Set the message to the list of relevant potentials
			msg = findRelPots(pot, separator.getVariables());
		
			// set separator inner message
			separator.setInnerMsg(msg);
		}
	}
	
	private ArrayList<BruseNode> getDConnected(ArrayList<String> query) {
		ArrayList<String> reqQuery = new ArrayList<String>();
		reqQuery.addAll(query);
		ArrayList<BruseNode> result = new ArrayList<BruseNode>();
		ArrayList<BruseNode> dsep = null;
		
		// if dconnection was already determined for a variable do not recalculate
		for (int i=reqQuery.size() - 1; i >= 0; i--) {
			dsep = m_dconnected.get(reqQuery.get(i));
			if (dsep != null) {
				for (int j=0; j < dsep.size(); j++) {		
					if (result.contains(dsep.get(j)) == false) {
						result.add(dsep.get(j));
					}
				}
				reqQuery.remove(i);
			}
		}
		
		if (reqQuery.size() > 0) {
			// Create a dseparation analyzer - it can calculate dconnection
			DSeparationAnalyzer dsepAnalyzer = new DSeparationAnalyzer(m_network);
		
			// create list of all evidence
			ArrayList<String> evidence = new ArrayList<String>();
			evidence.addAll(getEvidenceVarNames(m_hEvidence));
			evidence.addAll(getEvidenceVarNames(m_sEvidence));
			
			String qName = null;
			ArrayList<String> q = null;
			ArrayList<BruseNode> nodes = null;
			
			// calculate dconnection for rest of query that could not be looked up
			for (int i=0; i < reqQuery.size(); i++) {
				qName = reqQuery.get(i);
				q = new ArrayList<String>();
				q.add(qName);
				
				//nodes = dsepAnalyzer.getDseparation(q, evidence);
				//System.out.println(getVarNames(nodes));
				
				nodes = dsepAnalyzer.getDconnection(q, evidence);
				//System.out.println(getVarNames(nodes));
				
				
				m_dconnected.put(qName, nodes); // index nodes for future use
				
				// add unique
				for (int j=0; j < nodes.size(); j++) {		
					if (result.contains(nodes.get(j)) == false) {
						result.add(nodes.get(j));
					}
				}
			}
		}
		
		return result;
	}
	
	private ArrayList<BruseTable> removeReceivedPots(ArrayList<BruseTable> potentials, ArrayList<BruseTable> received) {
		BruseTable pot = null;
		ArrayList<BruseTable> relPots = new ArrayList<BruseTable>();
		
		// remove any potential in received list
		for (int i=0; i < potentials.size(); i++) {
			pot = potentials.get(i);
			if (!received.contains(pot)) {
				relPots.add(pot);
			}
		}
		
		return relPots;
	}
	
	private ArrayList<BruseTable> findRelPots(ArrayList<BruseTable> potentials, BruseNode node) {
		ArrayList<BruseNode> nodes = new ArrayList<BruseNode>(1);
		nodes.add(node);
		return findRelPots(potentials, nodes);
	}
	
	private ArrayList<BruseTable> findRelPots(ArrayList<BruseTable> potentials, ArrayList<BruseNode> nodes) {
		// find potentials that are d-connected to nodes
		// eliminate barren head variables
		// marginalize out any variables not in nodes
		
		ArrayList<BruseTable> relPots = new ArrayList<BruseTable>();
		
		relPots = findConnectedPots(potentials, nodes);
		relPots = removeBarrenVars(potentials, nodes);
		relPots = getMarginal(potentials, nodes);
		
		return relPots;
	}

	private ArrayList<BruseTable> findConnectedPots(ArrayList<BruseTable> potentials, ArrayList<BruseNode> nodes) {
		BruseTable pot = null;
		ArrayList<BruseTable> relPots = new ArrayList<BruseTable>();
		ArrayList<String> varNames = getVarNames(nodes);
		ArrayList<BruseNode> conVarNames = getDConnected(varNames);
		
		// for each potential check if it contains at least one connected variable
		// if it does add to the rel potentials list, also any potential that is a normalization constant (domain of 0)
		for (int i=0; i < potentials.size(); i++) {
			pot = potentials.get(i);
			for (int j=0; j < conVarNames.size(); j++) {
				if ( pot.containsVariable( conVarNames.get(j).getName() ) || pot.getVariables().length == 0) {
					relPots.add(pot);
					break;
				}
			}
		}
		
		return relPots;
	}
	
	private ArrayList<BruseTable> removeBarrenVars(ArrayList<BruseTable> potentials, ArrayList<BruseNode> nodes) {
		BruseTable pot = null;
		String head = null;
		ArrayList<String> varNames = getVarNames(nodes);
		ArrayList<BruseTable> relPots = new ArrayList<BruseTable>();
		
		for (int i=0; i < potentials.size(); i++) {
			pot = potentials.get(i);
			if (pot.getType() == BruseTable.PotentialType.Conditional) {
				head = pot.getVariables()[pot.getVariables().length-1].getName();
				if (!varNames.contains(head)) {
					for (int j=0; j < potentials.size(); j++) {
						if ( (i != j) && potentials.get(j).containsVariable(head) ) {
							relPots.add(pot);
							break;
						}
					}
				}
			}
		}
		
		return relPots;
	}
	
	private ArrayList<BruseTable> getMarginal(ArrayList<BruseTable> potentials, ArrayList<BruseNode> nodes) {
		BruseTable pot = null, marg = null;
		ArrayList<String> varNames = getVarNames(nodes), domain = null;
		ArrayList<BruseTable> relPots = new ArrayList<BruseTable>();
		
		// for each potential that contains variables not in nodes combine those potentials
		for (int i=0; i < potentials.size(); i++) {
			pot = potentials.get(i);
			domain = pot.getVariableNames();
			
			if (!varNames.containsAll(domain) && pot.getVariables().length > 0) {
				if (marg == null) {
					marg = pot;
				}
				else {
					marg = marg.multiplyBy(pot);
				}
			}
			else {
				relPots.add(pot);
			}
		}
		
		// if we have combined potentials then marginalize
		if (marg != null) relPots.add(marg.getMarginal(varNames));
		
		return relPots;
	}
		
	protected void distributeEvidence(JunctionNode node, JunctionNode caller) {
		JunctionSeparator separator;
		ArrayList<BruseTable> pot = node.getClique().getPotentials();
		ArrayList<BruseTable> msg = new ArrayList<BruseTable>();;
		
		// if this is not the root then absorb the outer msg of separator with the caller
		if (caller != null) {
			separator = node.getSeparator(caller);
			node.getClique().addPotentials(separator.getOuterMsgList());
		}
		
		for (int i=0; i < node.getNeighbors().size(); i++) {
			JunctionNode neighbor = (JunctionNode)node.getNeighbors().get(i);
			
			if (neighbor != caller) {			
				separator = node.getSeparator(neighbor);
				
				if (m_sEvidence.size() > 0 && caller == null) {
					BruseTable table = node.getClique().getTable().getMarginal(getVarNames(separator.getVariables()));
					// This is the Big Clique must divide message by each potential in neighboring separator
					// this avoids passing information receive back to neighbor
					for (int j=0; j < separator.getInnerMsgList().size(); j++) {
						table = table.divideBy(separator.getInnerMsgList().get(j));
					}
					
					// create new message
					msg.clear();
					// add table to message
					msg.add(table);
				}
				else {
					// For each potential associated with node, find potentials that are relevant to separator
					// Relevant potentials are not d-separated or unit potentials
					msg = findRelPots( removeReceivedPots(pot, separator.getInnerMsgList()), 
										separator.getVariables() );
				}
				// set the outer message
				separator.setOuterMsg(msg);
				
				// distribute neighbors evidence
				distributeEvidence(neighbor, node);
			}
		}
	}
	
	//TODO this needs rewriting - needs to take advantage of serparators
	protected BruseTable calculateMarginal(BruseNode node) {
		ArrayList<JunctionSeparator> separators = m_junctionTree.getSeparators();
		ArrayList<BruseTable> potentials = null, relPots = null;
		int minDom = Integer.MAX_VALUE;
		
		/*
		for (int i = 0; i < separators.size(); i++) {
			JunctionSeparator separator = separators.get(i);
			
			if (separator.getVariables().contains(node)) {
				if (potentials == null) {
					minDom = separator.getVariables().size();
					potentials = separator.getOuterMsgList();
					potentials.addAll(separator.getInnerMsgList());
				}
				else if (separator.getVariables().size() < minDom){
					minDom = separator.getVariables().size();
					potentials = separator.getOuterMsgList();
					potentials.addAll(separator.getInnerMsgList());
				}
			}
			
			// if the tables domain is size 1 then break since there is no smaller domain
			if (minDom == 1) break;
		}*/
		
		if (potentials == null) {
			for (int i=0; i < m_cliques.size(); i++) {
				Clique clique = m_cliques.get(i);
				
				if (clique.getMembers().contains(node)) {
					if (potentials == null) {
						minDom = clique.getMembers().size();
						potentials = clique.getPotentials();
					}
					else if (clique.getMembers().size() < minDom){
						minDom = clique.getMembers().size();
						potentials = clique.getPotentials();
					}
				}
			}
		}
		
		// find the relevant potentials
		relPots = findRelPots(potentials, node);
		BruseTable result = relPots.get(0);
		
		// combine the potentials
		for (int i = 1; i < relPots.size(); i++) {
			BruseTable pot = relPots.get(i);
			result = result.multiplyBy(pot);
		}
		
		// normalize the marginal table
		result.normalize();
		
		// update the variables states
		node.updateStates(result);
		
		return result;
	}
}

