package sc.bruse.engine;

import java.util.*;

import sc.bruse.api.*;

public class StateIterator {

	private BruseNode m_nodes[];
	private Map<String, Integer> m_curState; // TODO use array to keep track of current state
	private boolean m_finished;
	
	public StateIterator(BruseNode nodes[]) {
		m_nodes = nodes;
		
		initCurrentState();
	}
	
	private void initCurrentState() {
		m_curState = new HashMap<String, Integer>();
		
		for (int i=0; i < m_nodes.length; i++) {
			BruseNode node = m_nodes[i];
			m_curState.put(node.getName(), new Integer(0));
		}
		
		m_finished = false;
	}
	
	public void moveFirstState() {
		m_finished = false;
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
		
		for (int i=0; i < m_nodes.length; i++) {
			node = m_nodes[i];
			varName = node.getName(); //getVariableName(i);
			stateSize = node.getStates().size(); //getVariableStateSize(i);
			stateNum = m_curState.get(varName).intValue(); //getCurVariableStateNum(varName);
			
			if (stateNum < stateSize - 1) {
				stateNum++;
				m_curState.put(varName, new Integer(stateNum));
				//setCurVariableStateNum(varName, stateNum);
				break;
			}
			else {
				m_curState.put(varName, new Integer(0));
				//setCurVariableStateNum(varName, 0);
				
				if (i == m_nodes.length - 1) m_finished = true;
			}
		}
		
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
