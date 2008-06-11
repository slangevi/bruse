package sc.bruse.engine;

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

import sc.bruse.network.*;

public class StateIterator {

	private BruseNode m_nodes[];
	private Map<String, Integer> m_curState; // TODO use array to keep track of current state
	private int m_curStateNum;
	private int m_numStates;
	private boolean m_finished;
	
	public StateIterator(BruseNode nodes[]) {
		m_nodes = nodes;
		
		initCurrentState();
	}
	
	private void initCurrentState() {
		m_curState = new HashMap<String, Integer>();
		m_numStates = 1;
		
		for (int i=0; i < m_nodes.length; i++) {
			BruseNode node = m_nodes[i];
			m_curState.put(node.getName(), new Integer(0));
			m_numStates *= node.getStates().size();
		}
		m_curState.put(m_nodes[m_nodes.length-1].getName(), new Integer(-1));
		
		m_curStateNum = 1;
		m_finished = false;
	}
	
	public void moveFirstState() {
		m_finished = false;
		m_curStateNum = 1;
		initCurrentState();
	}
	
	public boolean hasMoreStates() {
		return (m_finished == false);
	}
	
	public Map<String, Integer> nextState() {	
		// cycle through the states
		int stateNum = 0;
		int stateSize = 0;
		String varName;
		BruseNode node = null;
		
		//HACK quick hack to get around problem when table has empty domain
		if (m_nodes.length == 0) m_finished = true;
		
		for (int i = m_nodes.length - 1; i >= 0; i--) {
		//for (int i=0; i < m_nodes.length; i++) {
			node = m_nodes[i];
			varName = node.getName(); //getVariableName(i);
			stateSize = node.getStates().size(); //getVariableStateSize(i);
			stateNum = m_curState.get(varName).intValue(); //getCurVariableStateNum(varName);
			
			if (stateNum < stateSize - 1) {
				stateNum++;
				m_curState.put(varName, new Integer(stateNum));
				
				if (m_curStateNum == m_numStates) m_finished = true;
				
				//setCurVariableStateNum(varName, stateNum);
				break;
			}
			else {
				m_curState.put(varName, new Integer(0));
				//setCurVariableStateNum(varName, 0);
				
				//if (m_curStateNum == m_numStates) m_finished = true;
				//if (i == m_nodes.length - 1) m_finished = true;
				//if ( (i == 0) && (stateNum == stateSize - 1)) m_finished = true;
			}
		}
		
		m_curStateNum++;
		return m_curState;
	}
	
	private int getCurVariableStateNum(String varName) {
		int stateNum = m_curState.get(varName).intValue();
		
		return stateNum;
	}
	
	private void setCurVariableStateNum(String varName, int stateNum) {
		m_curState.put(varName, new Integer(stateNum));
	}
	
	private int getVariableStateSize(int i) {
		int stateSize = m_nodes[i].getStates().size();
		
		return stateSize;
	}
	
	private String getVariableName(int i) {
		String varName = m_nodes[i].getName();
		
		return varName;
	}
	
	static public void dumpStates(StateIterator it) {
		int i = 0;
		
		while (it.hasMoreStates()) {
			Map<String, Integer> state = it.nextState();
			
			System.out.println("State " + i++);
			dumpState(state);
			System.out.println("\n");
		}
		
		// initialize back to first state when done
		it.moveFirstState();
	}
	
	static public void dumpState(Map<String, Integer> state) {
		Set<String> keys = state.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Integer s = state.get(key);
			
			System.out.print(key + ": " + s + ", ");
		}			
	}
}
