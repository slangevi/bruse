package sc.bruse.engine;

import java.util.*;
import sc.bruse.api.*;

public class JunctionNode extends GraphNode {
	private Clique m_clique;
	private Hashtable<JunctionNode, JunctionSeparator> m_separators;
	
	public JunctionNode(String id, Clique clique) {
		super(id);
		m_clique = clique;
		m_separators = new Hashtable<JunctionNode, JunctionSeparator>();
	}
	
	@Override
	public void addNeighbor(GraphNode node) {
		super.addNeighbor(node);
		
		// calculate the weight
		int weight = sizeOfIntersection(node);
		
		// set weight for this edge
		addEdgeWeight(node, weight);
	}
	
	@Override
	public GraphNode clone() {
		JunctionNode clone = new JunctionNode(m_name, m_clique);
		
		return clone;
	}
	
	public Clique getClique() {
		return m_clique;
	}
	
	public void createSeparators() {
		// This is done as a separate step for performance reasons
		
		for (int i=0; i < m_neighbors.size(); i++) {
			JunctionNode neighbor = (JunctionNode)m_neighbors.get(i);
			
			// create a separator with this neighbor
			JunctionSeparator separator = new JunctionSeparator(this, neighbor);
			
			// add separator to this node and neighbor so they share the same separator
			addSeparator(neighbor, separator);
			neighbor.addSeparator(this, separator);
		}
	}
	
	public ArrayList<JunctionSeparator> getSeparators() {
		ArrayList<JunctionSeparator> temp = new ArrayList<JunctionSeparator>();
		temp.addAll(m_separators.values());
		return temp;
	}
	
	public JunctionSeparator getSeparator(JunctionNode neighbor) {
		return m_separators.get(neighbor);
	}
	
	public void addSeparator(JunctionNode neighbor, JunctionSeparator separator) {
		m_separators.put(neighbor, separator);
	}
	
	private int sizeOfIntersection(GraphNode neighbor) {
		int size = 0;
		
		Clique neighborClique = ((JunctionNode)neighbor).getClique();
		ArrayList<BruseNode> members = m_clique.getMembers();
		
		for (int i=0; i < members.size(); i++) {
			BruseNode member = members.get(i);
			if (neighborClique.getMembers().contains(member)) {
				size++;
			}
		}
		
		return size;
	}
}
