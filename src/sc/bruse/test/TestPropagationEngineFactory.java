package sc.bruse.test;

import static org.junit.Assert.*;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;
import sc.bruse.api.*;
import sc.bruse.engine.*;
import sc.bruse.engine.propagation.*;
import sc.bruse.engine.propagation.PropagationEngineFactory.PropagationType;

import java.util.*;

public class TestPropagationEngineFactory {

	double epsilon = 0.00001;
	
	@Before
	public void setUp() throws Exception {
	}

	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	@Test
	public void testCreate() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("/Users/scott/studfarm.net");
			//BruseNetwork network = BruseNetworkFactory.create("/Users/scott/test.net");
			
			assertTrue(network != null);
			
			/*BruseTable.dumpTable(network.getAllNodes().get(0).getTable());
			BruseTable.dumpTable(network.getAllNodes().get(1).getTable());
			BruseTable.dumpTable(network.getAllNodes().get(2).getTable());
			*/
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			/*
			ArrayList<BruseEvidence> soft = new ArrayList<BruseEvidence>();
			BruseEvidence f = new BruseEvidence("K");
			
			ArrayList<BruseSoftEvidenceState> findings = new ArrayList<BruseSoftEvidenceState>();
			BruseSoftEvidenceState state = new BruseSoftEvidenceState("Pure", 0.8);
			findings.add(state);
			state = new BruseSoftEvidenceState("Carrier", 0.2);
			findings.add(state);
			f.setSoftEvidence(findings);
			soft.add(f);
			
			engine.addEvidence(soft);
			*/
			engine.init();
			
			// test adding evidence */
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("John");
			ev.setHardEvidence("Sick");
			evidence.add(ev);
			engine.addEvidence(evidence);
			
			
			engine.propagate();
			
			ArrayList<BruseEvidence> e2 = new ArrayList<BruseEvidence>();
			BruseEvidence ev2 = new BruseEvidence("Gwenn");
			ev2.setHardEvidence("Pure");
			e2.add(ev2);
			engine.addEvidence(e2);
			
			//engine.removeAllEvidence();
			
			engine.propagate();
			
			dumpNetwork(network);
		
			// check results
			BruseNode node = network.getNode("K");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0050539103));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9949460897));
			
			node = network.getNode("John");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Sick")], 1.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0000000000));
			
			node = network.getNode("Gwenn");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 1.0000000000));
			
			node = network.getNode("Irene");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 1.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0000000000));
			
			node = network.getNode("Dorothy");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.9815712566));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0184287434));
			
			node = network.getNode("Henry");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 1.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0000000000));
			
			node = network.getNode("Brian");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.9824079246));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0175920754));
			
			node = network.getNode("Eric");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 1.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0000000000));
			
			node = network.getNode("Ann");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0173002101));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9826997899));
			
			node = network.getNode("Fred");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0324629896));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9675370104));
			
			node = network.getNode("Cecily");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0306473367));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9693526633));
			
			node = network.getNode("L");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0258432221));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9741567779));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	private void dumpNetwork(BruseNetwork network) {
		DecimalFormat formatter = new DecimalFormat("#0.0000000000");
		
		for (int i=0; i < network.getAllNodes().size(); i++) {
			BruseNode node = network.getAllNodes().get(i);
			
			System.out.println("\n" + node.getName());
			
			for (int j=0; j < node.getStates().size(); j++) {
				BruseNodeState state = node.getStates().get(j);
				System.out.println(state.getStateName() + ": " + formatter.format(state.getValue()));
			}
		}
	}

}
