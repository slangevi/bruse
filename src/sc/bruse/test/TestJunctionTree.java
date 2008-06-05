package sc.bruse.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.api.*;
import sc.bruse.engine.Clique;
import sc.bruse.engine.CliqueFactory;
import sc.bruse.engine.JunctionNode;
import sc.bruse.engine.JunctionTree;
import sc.bruse.engine.MoralGraph;
import sc.bruse.engine.Triangulation;

public class TestJunctionTree {
	private ArrayList<Clique> m_cliques;
	JunctionTree m_junctionTree;
	
	@Before
	public void setUp() throws Exception {
		BruseNetwork network = BruseNetworkFactory.create("test/studfarm.net");
		
		// gen moral graph
		MoralGraph moralGraph = new MoralGraph(network);
		
		// triangulate graph
		Triangulation.triangulate(moralGraph);
		
		// gen cliques
		m_cliques = CliqueFactory.createCliques(moralGraph);
		
		// init the cliques for use
		for (int i=0; i < m_cliques.size(); i++) {
			Clique clique = m_cliques.get(i);
			clique.combinePotentials();
		}
		
		
	}
	
	static public void dumpJunctionTree(JunctionTree tree) {
		
		for (int i=0; i < tree.getNodes().size(); i++) {
			JunctionNode node = tree.getNodes().get(i);
			System.out.println("Junction Node " + node.getName());
			System.out.print("Member variables: ");
			
			for (int j=0; j < node.getClique().getMembers().size(); j++) {
				BruseNode n = node.getClique().getMembers().get(j);
				System.out.print(n.getName() + ", ");
			}
			
			System.out.println();
			System.out.print("Neighbors: ");
			
			for (int j=0; j < node.getNeighbors().size(); j++) {
				JunctionNode neighbor = (JunctionNode)node.getNeighbors().get(j);
				System.out.print(neighbor.getName() + ", ");
			}
			
			System.out.println();
			System.out.println();
		}
		
		
	}

	@Test
	public void testJunctionTree() {
		// create junction tree
		m_junctionTree = new JunctionTree(m_cliques);
		
		m_junctionTree.setRoot(0);
		
		dumpJunctionTree(m_junctionTree);
	}

	@Test
	public void testSetRoot() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRoot() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNodes() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSeparators() {
		fail("Not yet implemented");
	}

}
