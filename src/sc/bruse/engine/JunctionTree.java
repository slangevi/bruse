package sc.bruse.engine;

import java.util.*;
import sc.bruse.api.*;

//TODO: Should this be a subclass of Graph?
public class JunctionTree {
	
	private ArrayList<Clique> m_cliques;
	private ArrayList<JunctionNode> m_nodes;
	private ArrayList<JunctionSeparator> m_separators;
	private JunctionNode m_root;
	
	// Should we not also have a list of separators?
	// We could hook the separators up between the JunctionNodes
	
	public JunctionTree(ArrayList<Clique> cliques) {
		m_cliques = cliques;
		
		try {
			// initialize a fully connected junction graph from cliques
			Graph juncGraph = createJunctionGraph(m_cliques);
			
			// create max spanning tree on clique graph
			MaxSpanTree spanTree = new MaxSpanTree(juncGraph);
			createJunctionTree(spanTree.getNodes());
		}
		catch (Exception e) {
			// TODO handle error
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void setRoot(int i) {
		m_root = m_nodes.get(i);
	}
	
	public JunctionNode getRoot() {
		return m_root;
	}
	
	public ArrayList<JunctionNode> getNodes() {
		return m_nodes;
	}
	
	public ArrayList<JunctionSeparator> getSeparators() {
		if (m_separators == null) {
			m_separators = new ArrayList<JunctionSeparator>();
			
			for (int i=0; i < m_nodes.size(); i++) {
				JunctionNode node = m_nodes.get(i);
				
				for (int j=0; j < node.getSeparators().size(); j++) {
					JunctionSeparator separator = node.getSeparators().get(j);
					if (m_separators.contains(separator) == false) {
						m_separators.add(separator);
					}
				}
			}
		}
		
		return m_separators;
	}
	
	private void createJunctionTree(ArrayList<GraphNode> nodes) {
		m_nodes = new ArrayList<JunctionNode>();
		
		for (int i=0; i < nodes.size(); i++) {
			m_nodes.add((JunctionNode)nodes.get(i));
		}
		
		createSeparators();
	}
	
	private void createSeparators() {
		ArrayList<JunctionNode> nodes = new ArrayList<JunctionNode>();
		nodes.addAll(m_nodes);
		
		while (nodes.size() > 0) {
			JunctionNode node = (JunctionNode)nodes.get(0);
			node.createSeparators();
			// create separators will process the neighbors and the node
			nodes.removeAll(node.getNeighbors());
			nodes.remove(node);
		}
	}
	
	private Graph createJunctionGraph(ArrayList<Clique> cliques) {
		// create the junction nodes
		Graph juncGraph = createJunctionNodes(cliques);
		JunctionNode node = null, neighbor = null;
		
		// Create a fully connected cluster graph
		for (int i=0; i < juncGraph.size(); i++) {
			node = (JunctionNode)juncGraph.getNodes().get(i);
			
			for (int j=0; j < juncGraph.size(); j++) {
				if (i != j) {
					neighbor = (JunctionNode)juncGraph.getNodes().get(j);
					node.addNeighbor(neighbor);
				}
			}
		}
		
		return juncGraph;
	}
	
	private Graph createJunctionNodes(ArrayList<Clique> cliques) {
		Graph juncGraph = new Graph();
		
		for (int i=0; i < cliques.size(); i++) {
			Clique clique = cliques.get(i);
			juncGraph.addNode(new JunctionNode(Integer.toString(i), clique));
		}
		return juncGraph;
	}
	
	static public void dumpJunctionTree(JunctionTree tree) {
		
		for (int i=0; i < tree.getNodes().size(); i++) {
			JunctionNode node = tree.getNodes().get(i);
			System.out.println("Junction Node " + node.getName());
			System.out.print("Node Members: ");
			
			for (int j=0; j < node.getClique().getMembers().size(); j++) {
				BruseNode n = node.getClique().getMembers().get(j);
				System.out.print(n.getName() + ", ");
			}
			System.out.println();

			BruseTable.dumpTable(node.getClique().getTable(), false);
			
			for (int j=0; j < node.getNeighbors().size(); j++) {
				JunctionNode neighbor = (JunctionNode)node.getNeighbors().get(j);
				System.out.println("Neighbor: " + neighbor.getName());
				JunctionSeparator sep = node.getSeparator(neighbor);
				
				System.out.print("Separator: ");
				for (int k=0; k < sep.getVariables().size(); k++) {
					BruseNode var = sep.getVariables().get(k);
					System.out.print(var.getName() + " ");
				}
				System.out.println();
				
				System.out.println("Separator InnerMsg:" );
				BruseTable.dumpTable(sep.getInnerMsg(), false);
				
				System.out.println("Separator OuterMsg:" );
				BruseTable.dumpTable(sep.getOuterMsg(), false);
				
				
				System.out.println();
			}
			
			System.out.println();
			System.out.println();
		}	
	}
	
	public void resetSeparators() {
		JunctionSeparator separator = null;
		
		if (m_separators == null) return;
		
		for (int i=0; i < m_separators.size(); i++) {
			separator = m_separators.get(i);
			separator.clearInnerMsg();
			separator.clearOuterMsg();
		}
	}
}
