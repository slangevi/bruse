package sc.bruse.parser;

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

public class Variable {

	private String m_name;
	private String m_desc;
	private LinkedList<String> m_states;
	private Hashtable<String, String> m_stateDesc;
	private LinkedList<Variable> m_parents;
	private double m_table[];
	private int m_rowSize = 1;
	
	public Variable(String name) {
		m_name = name;
		m_desc = new String();
		m_states = new LinkedList<String>();
		m_stateDesc = new Hashtable<String, String>();
		m_parents = new LinkedList<Variable>();
	}
	
	public void setDesc(String desc) {
		m_desc = desc;
	}
	
	public String getDesc() {
		return m_desc;
	}
	
	public String getName() {
		return m_name;
	}
	
	public LinkedList<String> getStates() {
		return m_states;
	}
	
	public LinkedList<Variable> getParents() {
		return m_parents;
	}
	
	public void addParent(Variable parent) {
		m_parents.add(parent);
	}
	
	public void addState(String state) {
		m_states.add(state);
	}
	
	public void addStateDesc(String state, String desc) {
		m_stateDesc.put(state, desc);
	}
	
	public String getStateDesc(String state) {
		return m_stateDesc.get(state);
	}
	
	public void createTable() {
		int size = m_states.size();
		int dim = 0;
		ListIterator<Variable> it = m_parents.listIterator();
		
		// calculate table dimension (product of num states in variable and parents)
		while (it.hasNext()) {
			dim = it.next().getStates().size();
			size *= dim;
			m_rowSize *= dim;  // keep track of the row size
		}
		m_table = new double[size];
	}
	
	public void putTableEntry(int index, double value) {
		m_table[index] = value;
	}
	
	public double[] getTable() {
		return m_table;
	}
	
	public int getRowSize() {
		return m_rowSize;
	}
	
	public void normalizeTable() {
		// Make sure each column in table adds to 1
		int numCols = m_table.length/m_states.size();
		double[] colSum = new double[m_rowSize];
		
		// Sum each column
		for (int i=0; i < numCols; i++) {
			for (int j=0; j < m_states.size(); j++) {
				colSum[i] += m_table[i*m_states.size() + j];
			}
		}
		
		// normalize each column
		for (int i=0; i < numCols; i++) {
			for (int j=0; j < m_states.size(); j++) {
				m_table[i*m_states.size() +j] /= colSum[i];
			}
		}
	}
}
