package sc.bruse.engine.propagation;

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

import sc.bruse.engine.Entropy;
import sc.bruse.network.*;

/***
 * The propagation engine abstract class.  
 * This class implements the IPropagationEngine interface and is typically extended by
 * specific propagation engines such as Hugin or Lazy.  The class provides common functionality,
 * but leaves implementing initialization and propagation to each specific propagation engine.
 * 
 * @author langevin
 *
 */
public abstract class PropagationEngine implements IPropagationEngine {
	protected BruseNetwork m_network;
	protected ArrayList<BruseEvidence> m_hEvidence;	// hard evidence
	protected ArrayList<BruseEvidence> m_sEvidence;	// soft evidence
	protected boolean m_isDirty = true;				// flag whether propagation is needed, initially true

	/***
	 * The propagation engine constructor
	 *
	 */
	public PropagationEngine() {
		// create the evidence containers
		m_sEvidence = new ArrayList<BruseEvidence>();
		m_hEvidence = new ArrayList<BruseEvidence>();
	}
	
	/***
	 * Add evidence to the propagation engine
	 * 
	 * @param evidence to add
	 */
	public void addEvidence(BruseEvidence evidence) {
		if (evidence.getType() == BruseEvidence.EvidenceType.HARD) {
			// add to hard evidence
			//TODO should create the hard evidence table and add that to collection instead?
			m_hEvidence.add(evidence);
		}
		else {
			// add to soft evidence
			
			//TODO for BigClique the soft evidence must be defined before init()
			// the soft evidence can be updated anytime after however but 
			// it must be the same soft evidence nodes defined initially
			// otherwise the junction tree must be rebuilt
			// we should check if the nodes are the same and if so just update
			// the soft evidence entered, otherwise rebuild the whole junction tree
			//TODO should create the soft evidence table and add that to collection instead
			m_sEvidence.add(evidence);
		}
		
		// evidence has changed need to propagate
		m_isDirty = true;
	}

	/***
	 * Add the list of evidence to the propagation engine
	 * 
	 * @param evidence is a list of evidence to add
	 */
	public void addEvidence(ArrayList<BruseEvidence> evidence) {
		BruseEvidence finding = null;
		
		for (int i=0; i < evidence.size(); i++) {
			finding = evidence.get(i);
			
			addEvidence(finding);
		}
	}

	/***
	 * Get a list of all the evidence associated with the propagation engine
	 * 
	 * @return list of evidence
	 */
	public ArrayList<BruseEvidence> getEvidence() {
		ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
		
		// Add the hard and soft evidence to the collection
		evidence.addAll(m_hEvidence);
		evidence.addAll(m_sEvidence);
		
		// return list of all evidence entered
		return evidence;
	}

	/***
	 * Get the Bruse Network associated with this propagation engine
	 * 
	 * @return the Bruse Network
	 */
	public BruseNetwork getNetwork() {
		return m_network;
	}

	/***
	 * Get the value of information of the test node on the target node
	 * 
	 * @param targetNodeName is the name of the target node
	 * @param testNodeName is the name of the test node
	 * 
	 * @return the value of information
	 */
	public double getVOI(String targetNodeName, String testNodeName) {
		double voi = 0;
		double entropy, expentropy;
		BruseNode targetNode, testNode;
		
		try {
			// find the nodes
			targetNode = m_network.getNode(targetNodeName);
			testNode = m_network.getNode(testNodeName);
		}
		catch (BruseAPIException e) {
			System.err.println("TargetNode or TestNode for VOI calculation does not exist.");
			e.printStackTrace();
			return -1;
		}
		
		// Calculate entropy
		entropy = Entropy.getEntropy(targetNode);
		expentropy = Entropy.getExpectedEntropy(this, targetNode, testNode);
		
		// VOI formula:  -(ExpectedEntropy(V | T) - H(V))
		voi = entropy - expentropy;
		
		return voi;
	}

	/***
	 * Remove all evidence associated with the propagation engine
	 * 
	 */
	public void removeAllEvidence() {
		m_hEvidence.clear();
		m_sEvidence.clear();
		
		// Evidence has changed - need to propagate
		m_isDirty = true;
	}

	/***
	 * Set the BruseNetwork that the propagation engine is associated with
	 * 
	 * @param network the BruseNetwork
	 */
	public void setNetwork(BruseNetwork network) {
		// TODO If the network changes we need to force an init() because the junction tree needs to be rebuilt!
		m_network = network;
	}
	
	////////
	// The routines must be defined by each propagation engine
	////////
	
	/***
	 * abstract method that each propagation engine must implement
	 */
	public abstract void init(); 

	/***
	 * abstract method that each propagation engine must implement
	 */
	public abstract void propagate();
	
}
