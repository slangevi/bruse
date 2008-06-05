package sc.bruse.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.engine.MoralGraph;
import sc.bruse.engine.MoralGraphNode;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;

public class TestMoralGraph {
	BruseNetwork m_network;

	@Before
	public void setUp() throws Exception {
		m_network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
	}

	@Test
	public void testMoralGraphBruseNetwork() {
		// gen moral graph
		MoralGraph moralGraph = new MoralGraph(m_network);
		
		for (int i=0; i < moralGraph.getNodes().size(); i++) {
			MoralGraphNode node = (MoralGraphNode)moralGraph.getNodes().get(i);
			System.out.println("Graph Node " + node.getName());
			System.out.print("Neighbors: ");
			
			for (int j=0; j < node.getNeighbors().size(); j++) {
				MoralGraphNode neighbor = (MoralGraphNode)node.getNeighbors().get(j);
				System.out.print(neighbor.getName() + ", ");
			}
			System.out.println("\n");
		}
		
		//TODO create a real unit test here
	}
}
