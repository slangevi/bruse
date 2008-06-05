package sc.bruse.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.*;

import sc.bruse.api.BruseNetwork;
import sc.bruse.api.BruseNetworkFactory;
import sc.bruse.engine.Clique;
import sc.bruse.engine.CliqueFactory;
import sc.bruse.engine.MoralGraph;
import sc.bruse.engine.StateIterator;
import sc.bruse.engine.Triangulation;

public class TestStateIterator {

	ArrayList<Clique> m_cliques;
	
	@Before
	public void setUp() throws Exception {
		BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
		
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

	/*@Test
	public void testStateIterator() {
		StateIterator it = new StateIterator(m_cliques.get(0).getMembers());
		
		StateIterator.dumpStates(it);
		
	}*/

}
