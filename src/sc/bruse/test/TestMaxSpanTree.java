package sc.bruse.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.api.BruseNetwork;
import sc.bruse.api.BruseNetworkFactory;
import sc.bruse.engine.Graph;
import sc.bruse.engine.GraphNode;
import sc.bruse.engine.MaxSpanTree;

public class TestMaxSpanTree {
	Graph m_graph;
	
	@Before
	public void setUp() throws Exception {
		m_graph = createTestGraph();
	}
	
	private Graph createTestGraph() {
		Graph graph = new Graph();
		
		// create 10 nodes in test graph
		for (int i=0; i < 10; i++) {
			graph.addNode(new GraphNode(Integer.toString(i)));
		}
		
		// make fully connected graph
		for (int i=0; i < graph.getNodes().size(); i++) {
			GraphNode node = graph.getNodes().get(i);
			
			for (int j=0; j < graph.getNodes().size(); j++) {
				if (i != j) {
					GraphNode neighbor = graph.getNodes().get(j);
					node.addNeighbor(neighbor);
					// set the edge weight to the sum of node index and neighbor index
					node.addEdgeWeight(neighbor, (i+j));
				}
			}
		}
		
		return graph;
	}

	@Test
	public void testMaxSpanTree() {		
		try {
			MaxSpanTree tree = new MaxSpanTree(m_graph);
		
			Graph graph = tree.getTree();
		
			Graph.dumpGraph(graph);
		
			for (int i=0; i < graph.getNodes().size(); i++) {
				GraphNode node = graph.getNodes().get(i);
			
				if ("9".compareTo(node.getName()) == 0) {
					assertTrue(node.getNeighbors().size() == 9);
				}
				else {
					assertTrue(node.getNeighbors().size() == 1);
					assertTrue("9".compareTo(node.getNeighbors().get(0).getName()) == 0);
				}
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
