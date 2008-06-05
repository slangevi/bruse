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
