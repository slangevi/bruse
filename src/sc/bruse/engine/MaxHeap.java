package sc.bruse.engine;

import java.util.*;

import sc.bruse.network.*;

public class MaxHeap {

	private int m_size;
	private MaxSpanTreeEdge m_edges[];
	
	public MaxHeap(int maxSize) {
		m_size = 0;
		m_edges = new MaxSpanTreeEdge[maxSize];
	}
	
	public void addNodeEdges(GraphNode node) throws BruseAPIException {
		// make the node passed in the left node of the edge
		for (int i=0; i < node.getNeighbors().size(); i++) {
			GraphNode neighbor = node.getNeighbors().get(i);
			int weight = node.getEdgeWeight(neighbor);
			addEdge(new MaxSpanTreeEdge(node, neighbor, weight));
		}
	}
	
	public int getSize() {
		return m_size;
	}
	
	private void bubbleUp() {
		int j = 0, i = m_size - 1;
		MaxSpanTreeEdge parent = null;
		MaxSpanTreeEdge node = m_edges[i];
		
		while (i > 0) {
			j = (int)Math.floor((i-1)/2);
			parent = m_edges[j];
			if (parent.getWeight() >= node.getWeight()) break;
			//swap
			m_edges[i] = parent;
			m_edges[j] = node;
			i = j;
		}
	}
	
	private int getMax(int i, int j) {
		if (i >= m_size || j >= m_size) return -1;
		if (m_edges[i] == null && m_edges[j] == null) return -1;
		
		if (m_edges[i] == null) {
			return j;
		}
		else if (m_edges[j] == null) {
			return i;
		}
		else if (m_edges[i].getWeight() >= m_edges[j].getWeight()) {
			return i;
		}
		else {
			return j;
		}
	}
	
	private void bubbleDown() {
		int i = 0, l = 0, r = 0, j = 0;
		MaxSpanTreeEdge child = null;
		MaxSpanTreeEdge node = m_edges[i];
	
		while (i < m_size) {
			l = i*2+1;
			r = l+1;
			j = getMax(l, r);
			
			if (j == -1) return;
			
			child = m_edges[j];
			
			if (node.getWeight() > child.getWeight()) return;
			
			//swap
			m_edges[j] = node;
			m_edges[i] = child;
			i = j;
		}
	}
	
	private void addEdge(MaxSpanTreeEdge edge) throws BruseAPIException {
		if (m_size == m_edges.length) throw new BruseAPIException("Max heap size reached. No more edges can be added.");
		
		// add new edge to end of heap and then bubble up
		m_edges[m_size] = edge;
		
		// increase size of heap
		m_size++;
		
		bubbleUp();
	}
	
	public MaxSpanTreeEdge popMaxEdge() {
		// if heap is empty return null
		if (m_size == 0) return null;
		
		// pop and return the first element of the heap
		MaxSpanTreeEdge maxEdge = m_edges[0];
		
		// Move the last element to the top of heap
		m_edges[0] = m_edges[m_size-1];
		m_edges[m_size-1] = null;
		
		// decrease size of heap
		m_size--;
		
		// bubble the top heap element down
		bubbleDown();
		
		return maxEdge;
	}
}
