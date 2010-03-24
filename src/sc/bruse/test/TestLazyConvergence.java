package sc.bruse.test;

import java.util.ArrayList;

import sc.bruse.engine.JunctionTree;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.engine.propagation.hugin.HuginPropagationEngine;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;
import sc.bruse.network.BruseSoftEvidenceState;
import sc.bruse.network.BruseTable;

public class TestLazyConvergence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test6();
	}
	
	private static void testTableMults() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/precisiontest.net");
			
			BruseTable pot1 = network.getNode("A").getTable();
			BruseTable pot2 = network.getNode("D").getTable();
			
			BruseEvidence ev = new BruseEvidence("C");
			ev.setHardEvidence("true");
			
			pot1 = pot1.multiplyBy(ev.getTable(network));
			
			BruseTable result = pot2.multiplyBy(pot1);

			result = pot1.multiplyBy(pot2);
			
			ArrayList<String> vars = new ArrayList<String>();
			vars.add("A");
			result = result.getMarginal(vars);
			
			BruseTable.dumpTable(result, false);
		}
		catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test8() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			
			BruseEvidence ev = new BruseEvidence("John");
			ev.setHardEvidence("Sick");
			evidence.add(ev);
			
			ev = new BruseEvidence("Henry");
			ev.setHardEvidence("Pure");
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test7() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("John");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.33));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.33));
			beliefs.add(new BruseSoftEvidenceState("Sick", 0.33));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("Brian");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.5));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("L");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.5));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("Gwenn");
			ev.setHardEvidence("Pure");
			evidence.add(ev);
			
			ev = new BruseEvidence("Henry");
			ev.setHardEvidence("Pure");
			evidence.add(ev);
			
			ev = new BruseEvidence("Cecily");
			ev.setHardEvidence("Pure");
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	private static void test6() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/studfarm.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper2);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("Ann");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.5));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("Eric");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Pure", 0.5));
			beliefs.add(new BruseSoftEvidenceState("Carrier", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("John");
			ev.setHardEvidence("Sick");
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();

			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
			sc.bruse.engine.BookKeepingMgr.dumpBookKeeping();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test5() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/hiddenmarkovtest.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("A");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("B");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test4() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/example.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("B");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("C");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test3() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/alarm.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("PVSat");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Low", 0.33));
			beliefs.add(new BruseSoftEvidenceState("Normal", 0.33));
			beliefs.add(new BruseSoftEvidenceState("High", 0.33));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("PulmEmbolus");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("True", 0.5));
			beliefs.add(new BruseSoftEvidenceState("False", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("Shunt");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("Normal", 0.5));
			beliefs.add(new BruseSoftEvidenceState("High", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test2() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/test.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("E");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("C");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("false", 0.5));
			beliefs.add(new BruseSoftEvidenceState("true", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void test1() {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/chest_clinic.net");
		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
		
			// BP, VentAlv, Shunt
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("L");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("yes", 0.2));
			beliefs.add(new BruseSoftEvidenceState("no", 0.8));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("B");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("yes", 0.3));
			beliefs.add(new BruseSoftEvidenceState("no", 0.7));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("S");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("yes", 0.5));
			beliefs.add(new BruseSoftEvidenceState("no", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
		
			engine.init();
	
			// remove A potential
			//BruseTable pot = ((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().get(0);
			//((HuginPropagationEngine)engine).getCliques().get(2).getPotentials().remove(0);
			
			engine.propagate();
		
			TestHuginPropagation.dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
