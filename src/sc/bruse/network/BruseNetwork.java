package sc.bruse.network;

import java.util.*;

import sc.bruse.engine.GraphNode;

/**
 * This class represents the Bayesian network in the BRUSE API.  There are methods
 * to manipulate and query the network.
 * <p>
 * This class be instantiated manually or the BruseNetworkFactory class can be used
 * to generate an instance.  See the BruseNetworkFactory class for details.
 * 
 * @author langevin
 */
//TODO this should inherit Graph
public class BruseNetwork {
	
	// Keeps a name index of nodes in the Graph
	protected Hashtable<String, BruseNode> 	m_index;
	protected ArrayList<BruseNode> 			m_nodes;
	
	/***
	 * BruseNetwork constructor
	 * 
	 */
	public BruseNetwork() {
		// init anything here
		m_nodes = new ArrayList<BruseNode>();
		m_index = new Hashtable<String, BruseNode>();
	}
	
	/***
	 * Add a node to the Bayesian network
	 * 
	 * @param node is the node to be added
	 */
	public void addNode(BruseNode node) {
		m_nodes.add(node);
		indexNode(node);
	}
	
	/***
	 * Remove a node from the Bayesian network
	 * 
	 * @param node is the node to be removed
	 */
	public void removeNode(BruseNode node) {
		m_nodes.remove(node);
		unindexNode(node);
	}
	
	/***
	 * Get a vector of all the nodes in the Bayesian network
	 * 
	 * @return a typed vector of BruseNodes
	 */
	public ArrayList<BruseNode> getAllNodes() {
		return m_nodes;
	}
	
	/***
	 * Returns a node by the node name
	 * 
	 * @param nodeName is the name of the node to be retrieved
	 * @return the node with name
	 * @throws BruseAPIException
	 */
	public BruseNode getNode(String nodeName) throws BruseAPIException {
		// Find the node with nodeName
		BruseNode node = m_index.get(nodeName);
		
		// Not found so raise an error
		//if (node == null) throw new BruseAPIException("Node: " + nodeName + " not found.");
			
		return node;
	}
	
	private void indexNode(BruseNode node) {
		// Index the node by the node name
		String name = node.getName();
		m_index.put(name, node);
	}
	
	private void unindexNode(BruseNode node) {
		// Remove the node from the index
		String name = node.getName();
		m_index.remove(name);
	}
}
