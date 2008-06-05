package sc.bruse.api;

import java.util.*;

/***
 * BruseEvidence is used to specify the evidence on a node.  This class is used
 * to specify both Hard and Soft evidence.
 * 
 * @author langevin
 */
public class BruseEvidence {
	public enum EvidenceType {HARD, SOFT};
	
	private ArrayList<BruseSoftEvidenceState> m_beliefs;
	private String m_stateName;
	private String m_nodeName;
	private EvidenceType m_type;
	private BruseTable m_table;
	
	/***
	 * BruseEvidence Constructor
	 * 
	 * @param nodeName is the name of the node that has evidence
	 */
	public BruseEvidence(String nodeName) {		
		m_nodeName = nodeName;
	}
	
	/***
	 * BruseEvidence Constructor
	 * 
	 * @param node is the node that has evidence
	 */
	public BruseEvidence(BruseNode node) {
		m_nodeName = node.getName();
	}
	
	/***
	 * Set soft evidence on the node
	 * 
	 * @param softEvidence is the list of beliefs for each state of the node
	 */
	public void setSoftEvidence(ArrayList<BruseSoftEvidenceState> softEvidence) {
		// Set the type to soft evidence
		m_type = EvidenceType.SOFT;
		m_beliefs = new ArrayList<BruseSoftEvidenceState>();
		m_beliefs.addAll(softEvidence);
	}
	
	/***
	 * Set hard evidence on the node
	 * 
	 * @param stateName is the name of the state representing the hard evidence
	 */
	public void setHardEvidence(String stateName) {
		// Set the type to hard evidence
		m_type = EvidenceType.HARD;
		m_stateName = stateName;
	}
	
	/***
	 * Get the name of the sate representing the hard evidence
	 * 
	 * @return the state name
	 */
	public String getStateName() {
		return m_stateName;
	}
	
	/***
	 * Get the soft evidence for the node
	 * 
	 * @return a ArrayList of soft evidence
	 */
	public ArrayList<BruseSoftEvidenceState> getSoftEvidence() {
		return m_beliefs;
	}
	
	/***
	 * Get the name of the node this evidence is associated with
	 * 
	 * @return the node name
	 */
	public String getNodeName() {
		return m_nodeName;
	}
	
	/***
	 * Get the type of evidence (Hard or Soft)
	 * 
	 * @return the type of evidence
	 */
	public EvidenceType getType() {
		return m_type;
	}
	
	/***
	 * Get a probability table representing this evidence
	 * 
	 * @param network is the bruse network this evidence is associated with
	 * 
	 * @return the probability table
	 */
	public BruseTable getTable(BruseNetwork network) {
		if (m_table == null) {
			m_table = createTable(network);
		}
		return m_table;
	}
	
	private BruseTable createTable(BruseNetwork network) {
		// Needs a network so it can find the node and retrieve its state information
		BruseTable table = null;
	
		try {
			BruseNode node = network.getNode(m_nodeName);
			
			if (m_type == EvidenceType.HARD) {
				table = createHardEvidenceTable(node);
			}
			else {
				// Must be soft evidence
				table = createSoftEvidenceTable(node);
			}
		}
		catch (BruseAPIException e) {
			// TODO
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		return table;
	}
	
	private BruseTable createHardEvidenceTable(BruseNode node) throws BruseAPIException {
		BruseTable table = new BruseTable(node);
		table.makeZerod();
		Hashtable<String, Integer> state = new Hashtable<String, Integer>();
		state.put(m_nodeName, node.getStateId(m_stateName));
		table.setValue(state, 1);
		return table;
	}
	
	private BruseTable createSoftEvidenceTable(BruseNode node) throws BruseAPIException {
		BruseTable table = new BruseTable(node);
		BruseSoftEvidenceState soft = null;
		int stateid;
		Hashtable<String, Integer> state = null;
			
		for (int i=0; i < m_beliefs.size(); i++) {
			soft = m_beliefs.get(i);
			stateid = node.getStateId(soft.getStateName());
			state = new Hashtable<String, Integer>();
			state.put(node.getName(), stateid);
			table.setValue(state, soft.getBelief());
		}
		
		return table;
	}
 }
