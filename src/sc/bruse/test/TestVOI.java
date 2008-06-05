package sc.bruse.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;

public class TestVOI {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testHuginNetVOIWithNoEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			engine.init();
		
			engine.propagate();

			double voi = engine.getVOI("Dorothy", "Ann");
			assertTrue(voi == 0.02561161125427408);
			
			System.out.println("VOI: " + voi);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testHuginNetVOIWithHardEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			engine.init();
		
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("John");
			ev.setHardEvidence("Sick");
			evidence.add(ev);
			BruseEvidence ev2 = new BruseEvidence("Gwenn");
			ev2.setHardEvidence("Pure");
			evidence.add(ev2);
			engine.addEvidence(evidence);
		
			engine.propagate();

			double voi = engine.getVOI("Dorothy", "Ann");
			assertTrue(voi == 0.010744712259790373);
			
			System.out.println("VOI: " + voi);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testHuginNetVOIWithSoftEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			engine.init();
		
			//TODO
			fail();
			
			engine.propagate();

			double voi = engine.getVOI("Dorothy", "Ann");
			assertTrue(voi == 0.02561161125427408);
			
			System.out.println("VOI: " + voi);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testHuginBIFVOIWithHardEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.xml");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			engine.init();
		
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("John");
			ev.setHardEvidence("Sick");
			evidence.add(ev);
			BruseEvidence ev2 = new BruseEvidence("Gwenn");
			ev2.setHardEvidence("Pure");
			evidence.add(ev2);
			engine.addEvidence(evidence);
		
			engine.propagate();

			double voi = engine.getVOI("Dorothy", "Ann");
			assertTrue(voi == 0.010744712259790373);
			
			System.out.println("VOI: " + voi);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testLazyNetVOIWithNoEvidence() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
			engine.init();
		
			engine.propagate();

			double voi = engine.getVOI("Dorothy", "Ann");
			assertTrue(voi == 0.02561161125427408);
			
			System.out.println("VOI: " + voi);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
}
