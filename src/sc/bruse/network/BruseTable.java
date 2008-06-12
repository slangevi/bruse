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

import sc.bruse.engine.*;

import java.text.*;

/***
 * This class represents a conditional probability table.
 * 
 * @author langevin
 *
 */
public class BruseTable {
	
	private int 							m_offsets[];
	private Map<String, BruseNode> 			m_index;    // TODO switch the variable list to use ordered hash so we don't have two duplicate collections
	private BruseNode						m_variables[];
	private double 							m_values[];
	private PotentialType					m_type = PotentialType.Conditional;  // default to conditional
	
	public enum PotentialType { Conditional, Joint };
	

	/***
	 * The probability table constructor
	 * 
	 * @param node is the node this table is associated with
	 */
	public BruseTable(BruseNode node) {
		// create the index
		m_index = new HashMap<String, BruseNode>();
		
		// create a bruse table for a single node
		addVariable(node);
		
		initOffsets();
		initValuesArray();
	}
	
	/***
	 * The probability table constructor
	 * 
	 * @param nodes is the list of nodes this table is associated with
	 */
	public BruseTable(ArrayList<BruseNode> nodes) {
		// create the index
		m_index = new HashMap<String, BruseNode>();
		
		// create a bruse table for the nodes
		addVariables(nodes);
		
		initOffsets();
		initValuesArray();
	}
	
	/***
	 * Get the list of names of variables (nodes) this probability table is associated with
	 * 
	 * @return list of strings representing the names of the variables
	 */
	public ArrayList<String> getVariableNames() {
		ArrayList<String> names = new ArrayList<String>();

		// TODO this should be cached
		for (int i=0; i < m_variables.length; i++) {
			names.add(m_variables[i].getName());
		}
		return names;
	}
	
	/***
	 * Set the values of the cells in the probablity table
	 * 
	 * @param values is an array of probability values
	 */
	public void setTableValues(double[] values ) {
		m_values = values;
	}
	
	/***
	 * Get the values of the cells in the probability table
	 * 
	 * @return an array of probability values
	 */
	public double[] getTableValues() {
		return m_values;
	}
	
	/***
	 * Determine whether a variable (node) is associated with this probability table
	 * 
	 * @param name is the name of the variable
	 * @return true if the variable is associated with this probability table, false otherwise
	 */
	public boolean containsVariable(String name) {
		return m_index.containsKey(name);
	}
	
	/***
	 * Determine whether a list of variables (nodes) is associated with this probability table
	 * 
	 * @param names is a list of the names of the variables
	 * @return true if all variables are associated with this probability table, false otherwise
	 */
	public boolean containsVariables(ArrayList<String> names) {
		// if the names list is empty return false
		if (names.size() == 0) return false;
		
		for (int i=0; i < names.size(); i++) {
			if (m_index.containsKey(names.get(i)) == false) return false;
		}
		return true;
	}
	
	private void addVariables(ArrayList<BruseNode> vars) {
		BruseNode var = null;
		
		m_variables = new BruseNode[vars.size()];
		
		for (int i=0; i < vars.size(); i++) {
			var = vars.get(i);
			m_variables[i] = var;
			//m_variables.add(var);
			m_index.put(var.getName(), var);
		}
	}
	
	private void addVariable(BruseNode var) {
		m_variables = new BruseNode[1];
		m_variables[0] = var;
		m_index.put(var.getName(), var);
	}
	
	private void initOffsets() {
		m_offsets = new int[m_variables.length];
		
		// Calculate and save the offsets for each variable in the value array
		int num = m_variables.length;
		int offset = 1;
	
		for (int i = num - 1; i >= 0; i--) {
			BruseNode node = m_variables[i];
			int numStates = node.getStates().size();
			m_offsets[i] = offset;
			offset *= numStates;
		}
	}
	
	private void initValuesArray() {
		int tableSize = 1;
		
		// calculate the size of the table
		for (int i=0; i < m_variables.length; i++) {
			BruseNode var = m_variables[i];
			tableSize *= var.getStates().size();
		}
		
		m_values = new double[tableSize];
	}
	
	/***
	 * Get the value in the probability table associated with a tuple of variable states.
	 * 
	 * Example:  
	 * If we have a table with variables {A, B} and both variables have states {T, F}
	 * our table is the cartesian product of the variable states and a value is associated with each:
	 * ({A=T, B=T}, 0.2), ({A=F, B=T}, 0.8), ({A=T, B=F}, 0.6), ({A=F, B=F}, 0.4)
	 * We can request the value for a particular tuple of variable states such as:  {A=F, B=T} 
	 * which will return back the value 0.8
	 * 
	 * @param state is a hashtable where the keys are the variable names and the values are the state id's
	 * @return the value associated with this tuple of variable states
	 */
	public double getValue(Map<String, Integer> state) {
		int index = getIndex(state);
		
		return m_values[index];
	}
	
	/***
	 * Set the value in the probability table associated with a tuple of variable sates.
	 * 
	 * Example:  
	 * If we have a table with variables {A, B} and both variables have states {T, F}
	 * our table is the cartesian product of the variable states and a value is associated with each:
	 * ({A=T, B=T}, 0.2), ({A=F, B=T}, 0.8), ({A=T, B=F}, 0.6), ({A=F, B=F}, 0.4)
	 * We can set the value for a particular tuple of variable states such as:  {A=F, B=T} = 0.8
	 * 
	 * @param state is a hashtable where the keys are the variable names and the values are the state id's
	 * @param value is the value associated with this tuple of variable states
	 */
	public void setValue(Map<String, Integer> state, double value) {
		int index = getIndex(state);
		
		m_values[index] = value;
	}
	
	public int getColWidth() {
		return m_offsets[m_variables.length-1];
	}
	
	public int getOffset(int i) {
		if (i >= m_offsets.length) return 0;
		
		return m_offsets[i];
	}
	
	public int getIndex(Map<String, Integer> state) {
		// compute the index in the values array
		int index = 0;
		int offset = 1;
		String name = null;
		
		for (int i=0; i < m_variables.length; i++) {
			name = m_variables[i].getName();
			offset = m_offsets[i];
			index += offset * state.get(name);
		}
		
		return index;
	}
	
	public BruseTable absorb(BruseTable table) {
		// multiply this table by param table and return result
		Map<String, Integer> state = null;
		double value = 0;
		
		// TODO Should this routine be removed?
		
		// TODO improve performance (like multiply)

		// TODO verify that table is a subset of this table
		
		StateIterator it = new StateIterator(this.getVariables());
		
		while (it.hasMoreStates()) {
			state = it.nextState();
			int idx = this.getIndex(state);
			value = m_values[idx] * table.getValue(state);
			m_values[idx] = value;
		}
		
		// set the resulting table as a joint table type
		this.setType(PotentialType.Joint);
		
		BookKeepingMgr.NumTableMults += this.getTableValues().length;
		BookKeepingMgr.NumTableAbsoptions++;
		
		return this;
	}
	
	public BruseTable div(BruseTable table) {
		// divide this table by param table and return result
		Map<String, Integer> state = null;
		double denom = 0;
		double value = 0;

		//TODO verify that table is a subset of this table
		
		//TODO make this more efficient (like multiply)
		
		StateIterator it = new StateIterator(this.getVariables());
		
		while (it.hasMoreStates()) {
			state = it.nextState();
			int idx = this.getIndex(state);
			value = m_values[idx] * table.getValue(state);
			
			denom = table.getValue(state);
			value = 0;

			// can't divide by 0! If the denom is 0 then the value should be set to 0
			if (denom != 0) { 
				value = m_values[idx] / denom;
			}
			
			m_values[idx] = value;
		}
		
		// set the resulting table as a joint table type
		this.setType(PotentialType.Joint);
		
		BookKeepingMgr.NumTableMults += this.getTableValues().length;
		BookKeepingMgr.NumTableAbsoptions++;
		
		return this;
	}
	
	public BruseTable multiplyBy(BruseTable table) {		
		ArrayList<BruseNode> variables = new ArrayList<BruseNode>();
		
		// Add all the variables for this table
		for (int i=0; i < m_variables.length; i++) {
			variables.add(m_variables[i]);
		}
		// Add any variables necessary from arg table
		BruseNode vars[] = table.getVariables();
		for (int i=0; i < vars.length; i++) {
			if (variables.contains(vars[i]) == false) {
				variables.add(vars[i]);
			}
		}
		// Calculate offsets for arg table
		int offsets[] = new int[variables.size()];
		int idx = 0;
		for (int i=0; i < table.getVariables().length; i++) {
			idx = variables.indexOf(table.getVariables()[i]);
			if (idx >= 0) offsets[idx] = table.getOffset(i);
		}
		
		BruseTable result = new BruseTable(variables);
		
		// Key idea is to run through result table values and for each value:
		// determine the state corresponding to this value for each variable
		// calculate the index in the this table based on the state configuration of this value
		// calculate the index in the arg table based on the state configuration of this value
		// stored the resulting value in the result table at calculated index
		
		int idx1 = 0, idx2 = 0;
		int offset = 0;
		BruseNode node = null;
		
		for (int i=0; i < result.getTableValues().length; i++) {
			idx1 = 0;
			idx2 = 0;
			for (int j=0; j < result.getVariables().length; j++) {
				node = result.getVariables()[j];
				offset = (i/result.getOffset(j) % node.getStates().size());
				idx1 += offset * this.getOffset(j);
				idx2 += offset * offsets[j]; //table.getOffset(j);  //TODO: fix this offset
			}
			
			result.getTableValues()[i] = m_values[idx1] * table.getTableValues()[idx2];
		}
		
		// set the resulting table as a joint table type
		result.setType(PotentialType.Joint);
		
		BookKeepingMgr.NumTableMults += result.getTableValues().length;
		BookKeepingMgr.NumTableAbsoptions++;
		
		return result;
	}
	

	/* Old method of multiplying - too slow
	public BruseTable multiplyBy(BruseTable table) {
		// multiply this table by param table and return result
		Map<String, Integer> state = null;
		double value = 0;
		
		BruseTable result = new BruseTable(mergeTableVariables(table));
		
		StateIterator it = new StateIterator(result.getVariables());
		
		while (it.hasMoreStates()) {
			state = it.nextState();
			int idx = this.getIndex(state);
			value = m_values[idx] * table.getValue(state);
			result.setValue(state, value);
		}
		
		// set the resulting table as a joint table type
		result.setType(PotentialType.Joint);
		
		BookKeepingMgr.NumTableMults += result.getTableValues().length;
		BookKeepingMgr.NumTableAbsoptions++;
		return result;
	}*/
	
	private ArrayList<BruseNode> mergeTableVariables(BruseTable table) {
		// First add this tables variables
		ArrayList<BruseNode> variables = new ArrayList<BruseNode>();
		
		// Add all the variables for this table
		for (int i=0; i < m_variables.length; i++) {
			variables.add(m_variables[i]);
		}
		
		// Add any variables necessary from table
		BruseNode vars[] = table.getVariables();
		for (int i=0; i < vars.length; i++) {
			if (variables.contains(vars[i]) == false) {
				variables.add(vars[i]);
			}
		}
		
		return variables;
	}
	
	/***
	 * Get the list of variables (nodes) associated with this BruseTable
	 * 
	 * @return the list of variables
	 */
	public BruseNode[] getVariables() {
		return m_variables;
	}
	
	/***
	 * Divide this BruseTable by the given BruseTable
	 * 
	 * @param table is the BruseTable to divide by
	 * @return the resulting table
	 */
	public BruseTable divideBy(BruseTable table) {
		// divide this table by param table
		Map<String, Integer> state = null;
		double denom = 0;
		double value = 0;
		
		//TODO Rewrite to be more efficient (like multiply)
		
		//TODO log number of divisions
		
		BruseTable result = new BruseTable(mergeTableVariables(table));
		
		StateIterator it = new StateIterator(result.getVariables());
		
		while (it.hasMoreStates()) {
			state = it.nextState();
			
			denom = table.getValue(state);
			value = 0;

			// can't divide by 0! If the denom is 0 then the value should be set to 0
			if (denom != 0) { 
				value = this.getValue(state) / denom;
			}

			result.setValue(state, value);
		}
		
		// set the resulting table as a joint table type
		result.setType(PotentialType.Joint);
		
		return result;
	}
	
	/***
	 * Normalize the values in the table so they all sum to 1.0
	 *
	 */
	public void normalize() {
		// Normalize the values in the table so they all sum to 1.0
		double sum = 0;
		
		for (int i=0; i < m_values.length; i++) {
			sum += m_values[i];
		}
		
		for (int j=0; j < m_values.length; j++) {
			m_values[j] /= sum;
		}
	}
	
	/***
	 * Get the marginal probability table for the provided variable (node)
	 * 
	 * @param varName is the name of the variable to marginalize on
	 * @return the resulting marginal table
	 */
	public BruseTable getMarginal(String varName) {
		ArrayList<String> varNames = new ArrayList<String>();
		varNames.add(varName);
		
		return getMarginal(varNames);
	}
	
	/***
	 * Get the marginal probability table for the provided variables (nodes)
	 * 
	 * @param varNames is the list of names of the variables to marginalize on
	 * @return the resulting marginal table
	 */
	public BruseTable getMarginal(ArrayList<String> varNames) {
		BruseNode node = null;
		BruseTable result = null;
		ArrayList<BruseNode> vars = new ArrayList<BruseNode>();
		int offsets[] = new int[varNames.size()];
		int idx = 0;
		
		// create a list of variables based on marginal varNames applicable to this table
		for (int i=0; i < m_variables.length; i++) {
			node = m_variables[i];
			
			if (varNames.contains(node.getName())) {
				vars.add(node);
				offsets[idx++] = m_offsets[i];
			}
		}
		
		// if the marginal is the same domain of this table then
		// there is nothing to do, return a clone of this table.
		if (m_variables.length == vars.size()) return this.clone();
		
		result = new BruseTable(vars);
		
		// Key idea is to run through this tables values and for each value:
		// determine the state corresponding to this value for each variable
		// calculate the index in the result table based on the state configuration of this value
		// additively stored the value in the result table at calculated index
		
		for (int i=0; i < m_values.length; i++) {
			idx = 0;
			for (int j=0; j < vars.size(); j++) {
				node = vars.get(j);
				idx += (i/offsets[j] % node.getStates().size()) * result.getOffset(j);
			}
			BookKeepingMgr.NumTableAdds++;
			result.getTableValues()[idx] += m_values[i];
		}
		
		// set the resulting table to the same type as this table
		result.setType(m_type);  // Is this correct??
		
		BookKeepingMgr.NumTableMarginals++;
		return result;
	}
	
	/***
	 * Makes a copy of this BruseTable
	 * 
	 * @return the copy of the BruseTable
	 */
	public BruseTable clone() {
		// return a copy of this table
		ArrayList<BruseNode> vars = new ArrayList<BruseNode>(m_variables.length); //(ArrayList<BruseNode>)m_variables.clone();
		for (int i=0; i < m_variables.length; i++) {
			vars.add(m_variables[i]);
		}
		
		BruseTable clone = new BruseTable(vars);
		
		clone.setTableValues(m_values.clone());
		
		// set the clone table to the same type as this table
		clone.setType(m_type);
		
		return clone;
	}
	
	/***
	 * Make this BruseTable into a unit table by setting each table value to 1.0
	 *
	 */
	public void makeUnit() {
		// Set each table entry to 1
		for (int i=0; i < m_values.length; i++) {
			m_values[i] = 1;
		}
	}
	
	/***
	 * Make this BruseTable into a zero'd table by setting each table value to 0.0
	 *
	 */
	public void makeZerod() {
		// Set each table entry to 0
		for (int i=0; i < m_values.length; i++) {
			m_values[i] = 0;
		}
	}
	
	/***
	 * Return whether this BruseTable and the provided BruseTable are equal
	 * Two tables are considered equal if they have the same dimensions and 
	 * every table value is within the provided threshold
	 * 
	 * @param table is the BruseTable to compare with
	 * @param threshold determines the precision of the comparison
	 * @return true if the provided table is equal to this table, otherwise false
	 */
	public boolean equals(BruseTable table, double threshold) {
		double[] values = table.getTableValues();
		double diff = 0;
		
		//TODO Should this also check if the variables are the same??
		
		// tables must have the same dimensions to be equal
		if (values.length != m_values.length) return false;
		
		// check if every table value is within threshold
		for (int i=0; i < m_values.length; i++) {
			diff = Math.abs(values[i] - m_values[i]);
			if (diff > threshold) return false;
		}
		
		return true;
	}
	
	/***
	 * Set whether this table is a conditional or joint probability table
	 * 
	 * @param type is the type of table
	 */
	public void setType(PotentialType type) {
		m_type = type;
	}
	
	/***
	 * Return whether this table is a conditional or joint probability table
	 * 
	 * @return the type of table
	 */
	public PotentialType getType() {
		return m_type;
	}
	
	/***
	 * Debug routine to print out the values of the BruseTable to standard output
	 * 
	 * @param table is the BruseTable to print
	 * @param showStates determines whether to also printout the tables states
	 */
	static public void dumpTable(BruseTable table, boolean showStates) {
		
		if (table == null) return;
		
		BruseNode vars[] = table.getVariables();
		DecimalFormat formatter = new DecimalFormat("#0.000000");
		
		// iterate through all the states of this table
		StateIterator it = new StateIterator(vars);
		System.out.print("Table Vars: ");
		
		for (int i=0; i < vars.length; i++) {
			System.out.print(vars[i].getName() + ", ");
		}
		
		System.out.println("\nSerialized table values (row major order):");
		System.out.print("[");
		for (int i=0; i < table.m_values.length; i++) {
			System.out.print(formatter.format(table.m_values[i]) + ", ");
		}
		System.out.println("]\n");
		
		if (showStates) {
			System.out.println("Table states and corresponding values:");
			while (it.hasMoreStates()) {
				Map<String, Integer> state = it.nextState();
				System.out.print("[");
				StateIterator.dumpState(state);
				System.out.print("]");
				double value = table.getValue(state);
				System.out.println(": " + formatter.format(value));
			}
			System.out.println("\n");
		}
	}
}
