package sc.bruse.test;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.*;

public class TestLazyPropagation {
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
	public void testSoftEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
			
			// WARNING: soft evidence must be added before initializing network
			
			// test adding evidence
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("John");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
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
	public void testHardEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);

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
			
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
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
	
	@Test
	public void testHardEvidence2() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/test.net");
			
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);

			engine.init();
			
			// test adding evidence */
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("A");
			ev.setHardEvidence("false");
			evidence.add(ev);
			engine.addEvidence(evidence);
			
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
			dumpNetwork(network);
			
			// check results
			BruseNode node = network.getNode("E");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.8620000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.1380000000));
			
			node = network.getNode("D");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.2380000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.7620000000));
			
			node = network.getNode("C");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.3800000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.6200000000));
			
			node = network.getNode("B");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.6000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.4000000000));
			
			node = network.getNode("A");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 1.0000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.0000000000));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSoftEvidence2() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/test.net");
			
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);

			// test adding evidence */
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("A");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			engine.addEvidence(evidence);
			
			engine.init();
			
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
			dumpNetwork(network);
			
			// check results
			BruseNode node = network.getNode("E");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.8670000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.1330000000));
			
			node = network.getNode("D");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.2330000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.7670000000));
			
			node = network.getNode("C");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.3300000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.6700000000));
			
			node = network.getNode("B");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.6000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.4000000000));
			
			node = network.getNode("A");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.5000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.5000000000));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSoftEvidence3() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/test.net");
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);

			// test adding evidence */
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("D");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			engine.addEvidence(evidence);
			
			engine.init();
			
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			BookKeepingMgr.reset();
			
			dumpNetwork(network);
			
			// check results
			BruseNode node = network.getNode("E");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.8667984190));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.1332015810));
			
			node = network.getNode("D");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.5000000000));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.5000000000));
			
			node = network.getNode("C");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.3320158103));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.6679841897));
			
			node = network.getNode("B");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.6073178995));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.3926821005));
			
			node = network.getNode("A");
			assertTrue(isEqual(node.getStateValues()[node.getStateId("false")], 0.2024392998));
			assertTrue(isEqual(node.getStateValues()[node.getStateId("true")], 0.7975607002));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSoftEvidence4() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			
			assertTrue(network != null);
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
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
			
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
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
