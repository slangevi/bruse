package sc.bruse.engine;

import sc.bruse.network.*;

public class MoralGraphNode extends GraphNode {
	
	private BruseNode m_node;
	
	public MoralGraphNode(BruseNode node) {
		super(node.getName());
		m_node = node;
	}
	
	public BruseNode getBruseNode() {
		return m_node;
	}
}
