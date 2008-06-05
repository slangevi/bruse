package sc.bruse.network;

/***
 * This class represents the value of a particular state of a node. 
 * 
 * @author langevin
 */
public class BruseNodeState {

	private String m_stateName;
	private int m_stateId;
	private double m_value;
		
	/***
	 * BruseNodeState constructor
	 * 
	 * @param stateId is the id of the state
	 * @param stateName is the name of the state
	 * @param value is the value of the state
	 */
	public BruseNodeState(int stateId, String stateName, double value) {
		m_stateId = stateId;
		m_stateName = stateName;
		m_value = value;
	}
	
	/***
	 * Get the id of the state
	 * 
	 * @return the state id
	 */
	public int getStateId() {
		return m_stateId;
	}
	
	/***
	 * 
	 * @return the name of the state
	 */
	public String getStateName() {
		return m_stateName;
	}
		
	/***
	 * 
	 * @return the value of this state being true
	 */
	public double getValue() {
		return m_value;
	}
	
	/***
	 * Sets the value of the state being true.  
	 * This is an internal method and should not be used.
	 *
	 */
	public void setValue(double val) {
		m_value = val;
	}
}
