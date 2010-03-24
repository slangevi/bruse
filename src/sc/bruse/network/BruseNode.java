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

import java.util.*;

/***
 * This class represents a node in the Bayesian network
 * 
 * @author langevin
 */
//TODO This should inherit GraphNode?
public class BruseNode {

	private Hashtable<String, String> 	m_attributes;
	private LinkedList<BruseNode>		m_parents;
	private LinkedList<BruseNode>		m_children;
	private LinkedList<BruseNodeState> 	m_states;
	private BruseTable 					m_table;
	private String 						m_name;
	private String						m_desc;
	
	/***
	 * BruseNode constructor
	 * 
	 * @param node is an internal BCNode
	 */
	public BruseNode(String name) {
		m_name = name;
		m_desc = new String();
		m_parents = new LinkedList<BruseNode>();
		m_children = new LinkedList<BruseNode>();
		m_states = new LinkedList<BruseNodeState>();
		m_attributes = new Hashtable<String, String>();
	}
	
	/***
	 * Sets the long description of this node
	 * 
	 * @param desc is the long description
	 */
	public void setDesc(String desc) {
		m_desc = desc;
	}
	
	/***
	 * Retrieves the long description of this node
	 * 
	 * @return the description of the node
	 */
	public String getDesc() {
		return m_desc;
	}
	
	/***
	 * Associates an attribute key-value pair with this BruseNode.
	 * If the attribute already exists the value is overwritten with the new value.
	 * 
	 * @param name is the attribute name
	 * @param value is the value of the attribute
	 */
	public void setAttribute(String name, String value) {
		m_attributes.put(name, value);
	}
	
	/***
	 * Retrieves the value of an attribute associated with this BruseNode.
	 * 
	 * @param name is the attribute name
	 * 
	 * @return value of attribute or null if no attribute found
	 */
	public String getAttribute(String name) {
		return m_attributes.get(name);
	}
	
	/***
	 * Removes all attributes and values associated with this BruseNode.
	 *
	 */
	public void clearAttributes() {
		m_attributes.clear();
	}
	
	/***
	 * Associates a BruseNode as a parent of this BruseNode.
	 * 
	 * @param parent is the BruseNode to make a parent
	 * 
	 * @throws BruseAPIException
	 */
	public void addParent(BruseNode parent) throws BruseAPIException {
		
		if (parent == this) throw new BruseAPIException("A node can't be a parent of itself!");
		
		// don't add parents more than once
		if (m_parents.contains(parent) == false) {
			m_parents.add(parent);
		}
	}
	
	/***
	 * Associates a BruseNode as a child of this BruseNode.
	 * 
	 * @param child is the BruseNode to make a child
	 * 
	 * @throws BruseAPIException
	 */
	public void addChild(BruseNode child) throws BruseAPIException {
		
		if (child == this) throw new BruseAPIException("A node can't be a child of itself!");
		
 		// don't add children more than once
		if (m_children.contains(child) == false) {
			m_children.add(child);
		}
	}
	
	/***
	 * Get the list of parent nodes of this BruseNode.
	 * 
	 * @return the list of parent nodes
	 */
	public LinkedList<BruseNode> getParents() {
		return m_parents;
	}
	
	/***
	 * Get the list of child nodes of this BruseNode.
	 * 
	 * @return the list of child nodes
	 */
	public LinkedList<BruseNode> getChildren() {
		return m_children;
	}
	
	/***
	 * Add a new outcome state to this BruseNode.
	 * 
	 * @param name is the outcome state name
	 * @param val is the probability associated with this state
	 */
	public void addState(String name, double val) {
		//TODO should check for duplicate states first
		m_states.add(new BruseNodeState(m_states.size(), name, val));
	}
	
	/***
	 * Add a new outcome state to this BruseNode.
	 * 
	 * @param name is the outcome state name
	 * @param desc is the long description for the state
	 * @param val is the probability associated with this state
	 */
	public void addState(String name, String desc, double val) {
		//TODO should check for duplicate states first
		m_states.add(new BruseNodeState(m_states.size(), name, desc, val));
	}
	
	/***
	 * Get the conditional probability table associated with this BruseNode.
	 * 
	 * @return the conditional probability table
	 */
	public BruseTable getTable() {
		return m_table;
	}
	
	/***
	 * Set the conditional probability table associated with this BruseNode.
	 * 
	 * @param table the conditional probability table
	 */
	public void setTable(BruseTable table) {
		m_table = table;
	}
	
	/***
	 * Update the outcome states using the provided probability table
	 * 
	 * @param table the probability table containing the posterior probabilities for each state
	 */
	public void updateStates(BruseTable table) {
		int i = 0;
		double val = 0;
		BruseNodeState state = null;
		Hashtable<String, Integer> stateItem = null;
		ListIterator<BruseNodeState> it = m_states.listIterator();
		
		// update the state values from the table
		while (it.hasNext()) {
			state = it.next();
			stateItem = new Hashtable<String, Integer>();
			stateItem.put(m_name, i);
			val = table.getValue(stateItem);
			state.setValue(val);
			i++;
		}
	}
	
	/***
	 * Gets the name of the node
	 * 
	 * @return the name of the node
	 */
	public String getName() {
		return m_name;
	}
	
	/***
	 * Gets the name of a state
	 * 
	 * @param stateId the id of the state
	 * @return the name of the state
	 */
	public String getStateName(int stateId) {
		return m_states.get(stateId).getStateName();
	}
	
	/***
	 * Gets the id of the state
	 * 
	 * @param stateName the name of the state
	 * @return the id of the state
	 * @throws BruseAPIException
	 */
	public int getStateId(String stateName) throws BruseAPIException {
		BruseNodeState state = null;
		ListIterator<BruseNodeState> it = m_states.listIterator();
		
		while (it.hasNext()) {
			state = it.next();
			
			if (state.getStateName().compareTo(stateName) == 0) return state.getStateId();
		}
		
		// Not found so throw an exception
		throw new BruseAPIException("State: " + stateName + " does not exists."); 
	}
	
	/***
	 * Gets all the states associated with this node.
	 * 
	 * @return a list of BruseNodeStates
	 */
	public LinkedList<BruseNodeState> getStates() {			
		return m_states;
	}
	
	/***
	 * Gets an array of the state values for this node ordered by state id
	 * 
	 * @return a float array of state values
	 */
	public double[] getStateValues() {
		int i = 0;
		double vals[] = new double[m_states.size()];
		ListIterator<BruseNodeState> it = m_states.listIterator();
		
		while (it.hasNext()) {
			vals[i] = it.next().getValue();
			i++;
		}
		
		return vals;
	}
}
