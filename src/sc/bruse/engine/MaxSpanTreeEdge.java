package sc.bruse.engine;

public class MaxSpanTreeEdge {
	GraphNode 	m_leftNode;
	GraphNode 	m_rightNode;
	int 		m_weight;
	
	public MaxSpanTreeEdge(GraphNode leftNode, GraphNode rightNode, int weight) {
		m_leftNode = leftNode;
		m_rightNode = rightNode;
		m_weight = weight;
	}
	
	public int getWeight() {
		return m_weight;
	}
	
	public GraphNode getLeftNode() {
		return m_leftNode;
	}
	
	public GraphNode getRightNode() {
		return m_rightNode;
	}
}
