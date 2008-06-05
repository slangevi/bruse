package sc.bruse.network;

/***
 * This class represents the soft evidence belief for a particular state of a node. 
 * 
 * @author langevin
 */
public class BruseSoftEvidenceState {

	private String m_stateName;
	private double m_belief;
	
	/***
	 * BruseSoftEvidenceState constructor
	 * 
	 * @param stateName is the name of the state this belief is for
	 * @param belief is the subjective belief of this state being true
	 */
	public BruseSoftEvidenceState(String stateName, double belief) {
		m_stateName = stateName;
		m_belief = belief;
	}
	
	/***
	 * Get the name of the state
	 * 
	 * @return the name of the state this belief is for
	 */
	public String getStateName() {
		return m_stateName;
	}
	
	/***
	 * Get the belief in the state
	 * 
	 * @return the subjective belief of this state being true
	 */
	public double getBelief() {
		return m_belief;
	}
}
