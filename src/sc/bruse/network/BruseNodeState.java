package sc.bruse.network;

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

/***
 * This class represents the value of a particular state of a node. 
 * 
 * @author langevin
 */
public class BruseNodeState {

	private String m_stateName;
	private String m_stateDesc;
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
		m_stateDesc = new String();
		m_stateName = stateName;
		m_value = value;
	}
	
	/***
	 * BruseNodeState constructor
	 * 
	 * @param stateId is the id of the state
	 * @param stateName is the name of the state
	 * @param stateDesc is the long description of the state
	 * @param value is the value of the state
	 */
	public BruseNodeState(int stateId, String stateName, String stateDesc, double value) {
		m_stateId = stateId;
		m_stateDesc = stateDesc;
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
	 * @return the long description of the state
	 */
	public String getStateDesc() {
		return m_stateDesc;
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
