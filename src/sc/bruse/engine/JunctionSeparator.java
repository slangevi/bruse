package sc.bruse.engine;

import sc.bruse.network.*;

import java.util.*;

public class JunctionSeparator {
	private ArrayList<BruseTable> m_innerMsg;
	private ArrayList<BruseTable> m_outerMsg;
	private ArrayList<BruseNode> m_variables;
	private Hashtable<String, BruseNode> m_index;
	
	public JunctionSeparator(JunctionNode n1, JunctionNode n2) {
		// Create a separator for these two nodes
		m_index = new Hashtable<String, BruseNode>();
		m_variables = new ArrayList<BruseNode>();
		setDomain(n1, n2);
		m_innerMsg = new ArrayList<BruseTable>();
		m_outerMsg = new ArrayList<BruseTable>();
	}
	
	private void setDomain(JunctionNode n1, JunctionNode n2) {
		// set the domain to the intersection of n1 and n2
		Clique c1 = n1.getClique();
		Clique c2 = n2.getClique();
		ArrayList<BruseNode> members = c1.getMembers();
		
		for (int i=0; i < members.size(); i++) {
			BruseNode member = members.get(i);
			if (c2.getMembers().contains(member)) {
				addVariable(member);
			}
		}
	}
	
	public boolean containsVariable(String name) {
		return m_index.containsKey(name);
	}
	
	public boolean containsVariables(ArrayList<String> names) {
		for (int i=0; i < names.size(); i++) {
			if (m_index.containsKey(names.get(i)) == false) return false;
		}
		return true;
	}
	
	private void addVariable(BruseNode var) {
		m_variables.add(var);
		m_index.put(var.getName(), var);
	}
	
	public void setInnerMsg(BruseTable msg) {
		m_innerMsg.add(msg);
	}
	
	public void setInnerMsg(ArrayList<BruseTable> msg) {
		m_innerMsg.addAll(msg);
	}
	
	public BruseTable getInnerMsg() {
		if (m_innerMsg.size() == 0) return null;
		
		return m_innerMsg.get(0);
	}
	
	public ArrayList<BruseTable> getInnerMsgList() {
		return m_innerMsg;
	}
	
	public void setOuterMsg(BruseTable msg) {
		m_outerMsg.add(msg);
	}
	
	public void setOuterMsg(ArrayList<BruseTable> msg) {
		m_outerMsg.addAll(msg);
	}
	
	public BruseTable getOuterMsg() {
		if (m_outerMsg.size() == 0) return null;
		
		return m_outerMsg.get(0);
	}
	
	public ArrayList<BruseTable> getOuterMsgList() {
		return m_outerMsg;
	}
	
	public ArrayList<BruseNode> getVariables() {
		return m_variables;
	}
	
	public void clearInnerMsg() {
		m_innerMsg.clear();
	}
	
	public void clearOuterMsg() {
		m_outerMsg.clear();
	}
}
