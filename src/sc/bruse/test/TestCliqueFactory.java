package sc.bruse.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.api.*;
import sc.bruse.engine.Clique;
import sc.bruse.engine.CliqueFactory;
import sc.bruse.engine.MoralGraph;
import sc.bruse.engine.Triangulation;

public class TestCliqueFactory {
	BruseNetwork m_network;
	MoralGraph m_moralGraph;
	private ArrayList<Clique> m_cliques;
	
	@Before
	public void setUp() throws Exception {
		m_network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
		
		// gen moral graph
		m_moralGraph = new MoralGraph(m_network);
		
		// triangulate graph
		Triangulation.triangulate(m_moralGraph);
	}

	@Test
	public void testCreateCliques() {
		// gen cliques
		m_cliques = CliqueFactory.createCliques(m_moralGraph);
		
		for (int i=0; i < m_cliques.size(); i++) {
			Clique clique = m_cliques.get(i);
			
			System.out.println("Clique " + Integer.toString(i));
			System.out.print("Members: ");
			
			for (int j=0; j < clique.getMembers().size(); j++) {
				BruseNode node = clique.getMembers().get(j);
				System.out.print(node.getName() + ", ");
			}
			System.out.println("\n");
		}
		fail("Not yet implemented");
	}

}
