package sc.bruse.test;


import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.api.*;
import sc.bruse.engine.*;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.engine.propagation.hugin.*;

public class TestWrapper3 {

double epsilon = 0.00001;
	
	private boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
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
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void testSoftEvidence2() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper3);
			engine.setNetwork(network);
			
			// test adding evidence
			BruseEvidence ev = new BruseEvidence("John");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Sick", 0.8));
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.1));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.1));
			ev.setSoftEvidence(beliefs);
			
			engine.addEvidence(ev);
			
			engine.init();
		
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
			//dumpNetwork(network);
			
			dumpNetwork(network);
			
			// check results
			BruseNode node = network.getNode("K");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0302466228));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9697533772));
			
			node = network.getNode("John");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Sick")], 0.8000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.1000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1000000000));
			
			node = network.getNode("Gwenn");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.5355907813));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.4644092187));
			
			node = network.getNode("Irene");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8577152730));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1422847270));
			
			node = network.getNode("Dorothy");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.6824341723));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.3175658277));
			
			node = network.getNode("Henry");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8529820936));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1470179064));
			
			node = network.getNode("Brian");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3309310461));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6690689539));
			
			node = network.getNode("Eric");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3421887392));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6578112608));
			
			node = network.getNode("Ann");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.5322114058));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.4677885942));
			
			node = network.getNode("Fred");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3936205997));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6063794003));
			
			node = network.getNode("Cecily");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0308736809));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9691263191));
			
			node = network.getNode("L");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0321586989));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9678413011));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSoftEvidence4() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper3);
			engine.setNetwork(network);

			// test adding multiple evidence */
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("Gwenn");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.5));
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("John");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Sick", 0.8));
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.1));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.1));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.init();
			
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
			dumpNetwork(network);
			
//			 check results
			BruseNode node = network.getNode("K");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0280534256));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9719465744));
			
			node = network.getNode("John");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Sick")], 0.7991680363));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.1002280719));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1006038918));
			
			node = network.getNode("Gwenn");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.5000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.5000000000));
			
			node = network.getNode("Irene");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8555637294));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1444362706));
			
			node = network.getNode("Dorothy");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.6909639316));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.3090360684));
			
			node = network.getNode("Henry");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8532443051));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1467556949));
			
			node = network.getNode("Brian");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3622693026));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6377306974));
			
			node = network.getNode("Eric");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3745422851));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6254577149));
			
			node = network.getNode("Ann");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.4997927831));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.5002072169));
			
			node = network.getNode("Fred");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3724794394));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6275205606));
			
			node = network.getNode("Cecily");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0322732183));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9677267817));
			
			node = network.getNode("L");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0332357932));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9667642068));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSoftEvidence5() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper3);
			engine.setNetwork(network);

			// test adding multiple evidence */
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("Gwenn");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.5));
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("John");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Sick", 0.8));
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.1));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.1));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("L");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.2));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.8));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("K");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.1));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.9));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.init();
			
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
			dumpNetwork(network);
			
//			 check results
			BruseNode node = network.getNode("K");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8867164998));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1132835002));
			
			node = network.getNode("John");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Sick")], 0.8011865445));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.1003417645));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.0984716910));
			
			node = network.getNode("Gwenn");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.5078412923));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.4921587077));
			
			node = network.getNode("Irene");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8176623836));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1823376164));
			
			node = network.getNode("Dorothy");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.2324282729));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.7675717271));
			
			node = network.getNode("Henry");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8938520861));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.1061479139));
			
			node = network.getNode("Brian");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.2530836788));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.7469163212));
			
			node = network.getNode("Eric");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.3266157234));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.6733842766));
			
			node = network.getNode("Ann");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0354843594));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9645156406));
			
			node = network.getNode("Fred");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.7447883860));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.2552116140));
			
			node = network.getNode("Cecily");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.0889275896));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.9110724104));
			
			node = network.getNode("L");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Carrier")], 0.8000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("Pure")], 0.2000000000));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}

}
