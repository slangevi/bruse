package sc.bruse.test;

import java.text.DecimalFormat;
import java.util.*;

import java.lang.management.*;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseNodeState;
import sc.bruse.network.BruseSoftEvidenceState;

public class SoftEvidencePerformanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String file = "tests/test61.net";  //test61.net"; //studfarm.net"; //alarm.net"; //"tests/diabetes.net";
		int numhev = 0;
		int numsev = 1;
		int numtests = 1;
		boolean debug = false;

		try {
			BruseNetwork network = BruseNetworkFactory.create(file);
			
			System.out.println("Network: " + file + " (" + network.getAllNodes().size() + " nodes)");
			System.out.println("Hard Evidence: " + numhev);
			System.out.println("Soft Evidence: " + numsev);
			System.out.println("------------------");
			
			int numnodes = network.getAllNodes().size();
			
			// keep track of nodes selected for evidence
			boolean selectedNodes[] = new boolean[numnodes];
			
			// Randomly generate hard evidence
			ArrayList<BruseEvidence> hevidence = createHardEvidence(numhev, selectedNodes, network);
			
			// Randomly generate soft evidence
			ArrayList<BruseEvidence> sevidence = createSoftEvidence(numsev, selectedNodes, network);
			
			System.out.print("SE Nodes: ");
			for (int i=0; i < sevidence.size(); i++) {
				System.out.print(sevidence.get(i).getNodeName() + ", ");
			}
			System.out.println();
		
			for (int i=0; i < numtests; i++) {
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);
				
				/*int numnodes = network.getAllNodes().size();
				
				// keep track of nodes selected for evidence
				boolean selectedNodes[] = new boolean[numnodes];
				
				// Randomly generate hard evidence
				ArrayList<BruseEvidence> hevidence = createHardEvidence(numhev, selectedNodes, network);
				
				// Randomly generate soft evidence
				ArrayList<BruseEvidence> sevidence = createSoftEvidence(numsev, selectedNodes, network);*/

				System.out.println("Test Hugin:");
				TestBigCliqueHugin(network, hevidence, sevidence, debug);
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);
		
				//TestWrapperHugin();
		
				// temporarily disabled
				/*
				System.out.println("Test Lazy:");
				TestBigCliqueLazy(network, hevidence, sevidence, debug);
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);*/
		
				System.out.println("Test Wrapper:");
				TestWrapperLazy(network, hevidence, sevidence, debug);
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);
		
				System.out.println("Test Wrapper 2:");
				TestWrapperLazy2(network, hevidence, sevidence, debug);
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void dumpNetwork(BruseNetwork network) {
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
	
	public static void TestBigCliqueHugin(BruseNetwork network, ArrayList<BruseEvidence> hevidence, ArrayList<BruseEvidence> sevidence, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
						
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
		
			engine.propagate();
			
			/*MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heap = mbean.getHeapMemoryUsage();
			MemoryUsage stack = mbean.getNonHeapMemoryUsage();
			System.out.println((heap.getUsed() + stack.getUsed())/1024);*/
			
			if (debug) dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void TestBigCliqueLazy(BruseNetwork network, ArrayList<BruseEvidence> hevidence, ArrayList<BruseEvidence> sevidence, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
						
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
			
			engine.propagate();
			
			/*MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heap = mbean.getHeapMemoryUsage();
			MemoryUsage stack = mbean.getNonHeapMemoryUsage();
			System.out.println((heap.getUsed() + stack.getUsed())/1024);*/
			
			if (debug) dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void TestWrapperLazy(BruseNetwork network, ArrayList<BruseEvidence> hevidence, ArrayList<BruseEvidence> sevidence, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper4);
			engine.setNetwork(network);
						
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
		
			engine.propagate();
			
			/*MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heap = mbean.getHeapMemoryUsage();
			MemoryUsage stack = mbean.getNonHeapMemoryUsage();
			System.out.println((heap.getUsed() + stack.getUsed())/1024);*/
			
			if (debug) dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void TestWrapperLazy2(BruseNetwork network, ArrayList<BruseEvidence> hevidence, ArrayList<BruseEvidence> sevidence, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper2);
			engine.setNetwork(network);
						
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
		
			engine.propagate();
			
			/*MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heap = mbean.getHeapMemoryUsage();
			MemoryUsage stack = mbean.getNonHeapMemoryUsage();
			System.out.println((heap.getUsed() + stack.getUsed())/1024);*/
			
			if (debug) dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void TestBigCliqueHugin(BruseNetwork network, int numhev, int numsev, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			
			int numnodes = network.getAllNodes().size();
			
			// keep track of nodes selected for evidence
			boolean selectedNodes[] = new boolean[numnodes];
			
			// Randomly generate hard evidence
			ArrayList<BruseEvidence> hevidence = createHardEvidence(numhev, selectedNodes, network);
			
			// Randomly generate soft evidence
			ArrayList<BruseEvidence> sevidence = createSoftEvidence(numsev, selectedNodes, network);
			
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
		
			// TIMING TEST
			long StartTime = System.currentTimeMillis();
			
			engine.propagate();
			
			long EndTime = System.currentTimeMillis();
			
			if (debug) dumpNetwork(network);
			
			System.out.println("*** Hugin TIME: " + (EndTime - StartTime));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void TestWrapperHugin() {
		
	}
	
	public static ArrayList<BruseEvidence> createHardEvidence(int numhard, boolean selectedNodes[], BruseNetwork network) {
		int numnodes = network.getAllNodes().size();
		
		// Create the evidence container
		ArrayList<BruseEvidence> hevidence = new ArrayList<BruseEvidence>();
		
		for (int i=0; i < numhard; i++) {
			Random r = new Random();
			int idx = r.nextInt(numnodes);
			
			while (selectedNodes[idx]) {
				idx = r.nextInt(numnodes);
			}
			
			// mark this node as selected
			selectedNodes[idx] = true;
			
			// get the selected node
			BruseNode node = network.getAllNodes().get(idx);
			
			// create hard evidence for this node
			BruseEvidence ev = new BruseEvidence(node.getName());
			// Randomly pick a state for evidence
			idx = r.nextInt(node.getStates().size());
			ev.setHardEvidence(node.getStateName(idx));
			
			// Add the evidence to the container
			hevidence.add(ev);
		}
		
		return hevidence;
	}
	
	public static ArrayList<BruseEvidence> createSoftEvidence(int numsoft, boolean selectedNodes[], BruseNetwork network) {
		int numnodes = network.getAllNodes().size();
		
		// Create the evidence container
		ArrayList<BruseEvidence> sevidence = new ArrayList<BruseEvidence>();
		
		for (int i=0; i < numsoft; i++) {
			Random r = new Random();
			int idx = r.nextInt(numnodes);
			
			while (selectedNodes[idx]) {
				idx = r.nextInt(numnodes);
			}

			// mark this node as selected
			selectedNodes[idx] = true;
			
			// get the selected node
			BruseNode node = network.getAllNodes().get(idx);
			
			// create soft evidence for this node
			BruseEvidence ev = new BruseEvidence(node.getName());

			// Create the beliefs 
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			
			for (int j=0; j < node.getStates().size(); j++) {
				String stateName = node.getStateName(j);
				// evenly distribute belief among states
				double val = 1.0/node.getStates().size();
				beliefs.add(new BruseSoftEvidenceState(stateName, val));
			}
			// add the soft evidence to the evidence
			ev.setSoftEvidence(beliefs);
			
			// Add the evidence to the container
			sevidence.add(ev);
		}
		
		return sevidence;
	}
	
	public static void TestBigCliqueLazy(BruseNetwork network, int numhev, int numsev, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
			
			int numnodes = network.getAllNodes().size();
			
			// keep track of nodes selected for evidence
			boolean selectedNodes[] = new boolean[numnodes];
			
			// Randomly generate hard evidence
			ArrayList<BruseEvidence> hevidence = createHardEvidence(numhev, selectedNodes, network);
			
			// Randomly generate soft evidence
			ArrayList<BruseEvidence> sevidence = createSoftEvidence(numsev, selectedNodes, network);
			
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
		
			// TIMING TEST
			long StartTime = System.currentTimeMillis();
			
			engine.propagate();
			
			long EndTime = System.currentTimeMillis();
			
			if (debug) dumpNetwork(network);
			
			System.out.println("*** Lazy TIME: " + (EndTime - StartTime));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void TestWrapperLazy(BruseNetwork network, int numhev, int numsev, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper);
			engine.setNetwork(network);
			
			int numnodes = network.getAllNodes().size();
			
			// keep track of nodes selected for evidence
			boolean selectedNodes[] = new boolean[numnodes];
			
			// Randomly generate hard evidence
			ArrayList<BruseEvidence> hevidence = createHardEvidence(numhev, selectedNodes, network);
			
			// Randomly generate soft evidence
			ArrayList<BruseEvidence> sevidence = createSoftEvidence(numsev, selectedNodes, network);
			
			// add the hard evidence
			engine.addEvidence(hevidence);
			
			// add the soft evidence
			engine.addEvidence(sevidence);
			
			// initialy (compile) the network
			engine.init();
		
			// TIMING TEST
			long StartTime = System.currentTimeMillis();
			
			engine.propagate();
			
			long EndTime = System.currentTimeMillis();
			
			if (debug) dumpNetwork(network);
			
			System.out.println("*** Wrapper TIME: " + (EndTime - StartTime));
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
