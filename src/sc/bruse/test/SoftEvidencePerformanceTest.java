package sc.bruse.test;

import java.text.DecimalFormat;
import java.util.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	final static double epsilon = 0.00001;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String file = "tests/test61.net";  //hiddenmarkovtest.net //test61.net"; //studfarm.net"; //alarm.net"; //"tests/diabetes.net";
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
			
			System.out.print("HE Nodes: ");
			for (int i=0; i < hevidence.size(); i++) {
				System.out.print(hevidence.get(i).getNodeName() + ", ");
			}
			System.out.println();
			
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
				
				//TestHuginPropagation.dumpNetwork(network);
				
				// Write stats to file
				dumpStats("Hugin", "output.txt", network, sevidence);
				
				// Save the network values so we can compare convergence
				double[] huginvalues = saveValues(network);
				
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);
		
				//TestWrapperHugin();
		
				// temporarily disabled
				
				System.out.println("Test Lazy:");
				TestBigCliqueLazy(network, hevidence, sevidence, debug);
				
				//TestHuginPropagation.dumpNetwork(network);
				
				// Write stats to file
				dumpStats("Lazy", "output.txt", network, sevidence);
				
				// Save the network values so we can compare convergence
				double[] lazyvalues = saveValues(network);
				
				// TEST
				//double[] wrapper2values = saveValues(network);
				//double[] wrapper4values = saveValues(network);
				
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);
		
				System.out.println("Test Wrapper 2:");
				TestWrapperLazy2(network, hevidence, sevidence, debug);
				
				// Write stats to file
				dumpStats("Wrapper2", "output.txt", network, sevidence);
				
				// Save the network values so we can compare convergence
				double[] wrapper2values = saveValues(network);
				
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				// force garbage collection
				System.gc();
				java.lang.Thread.sleep(10);
		
				System.out.println("Test Wrapper 1:");
				TestWrapperLazy1(network, hevidence, sevidence, debug);
				
				// Write stats to file
				dumpStats("Wrapper1", "output.txt", network, sevidence);
				
				// Save the network values so we can compare convergence
				double[] wrapper1values = saveValues(network);
				
				//dumpNetwork(network);
				BookKeepingMgr.dumpBookKeeping();
				BookKeepingMgr.reset();
				
				checkConvergence(huginvalues, lazyvalues, wrapper2values, wrapper1values);
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
	
	public static void TestWrapperLazy1(BruseNetwork network, ArrayList<BruseEvidence> hevidence, ArrayList<BruseEvidence> sevidence, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper1);
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
	
	public static void TestWrapperLazy1(BruseNetwork network, int numhev, int numsev, boolean debug) {
		try {		
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Wrapper1);
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
	
	private static boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	public static double[] saveValues(BruseNetwork network) {
		int idx = 0;
		int size = 0;
		double[] values;
		
		for (int i=0; i < network.getAllNodes().size(); i++) {
			BruseNode node = network.getAllNodes().get(i);
			size += node.getStates().size();
		}
		
		values = new double[size];
		
		for (int i=0; i < network.getAllNodes().size(); i++) {
			BruseNode node = network.getAllNodes().get(i);
			
			for (int j=0; j < node.getStateValues().length; j++) {
				values[idx] = node.getStateValues()[j];
				idx++;
			}
		}
		
		return values;
	}
	
	private static String getEvidenceNames(ArrayList<BruseEvidence> evidence) {
		StringBuilder str = new StringBuilder();
		for (int i=0; i < evidence.size(); i++) {
			str.append(evidence.get(i).getNodeName() + ",");
		}
		
		return str.toString();
	}
	
	private static int getMaxStateSize(BruseNetwork network) {
		int max = 0;
		int states = 0;
		ArrayList<BruseNode> nodes = network.getAllNodes();
		
		for (int i=0; i < nodes.size(); i++) {
			states = nodes.get(i).getStates().size();
			if (states > max) max = states;
		}
		
		return max;
	}
	
	public static void dumpStats(String algorithm, String filename, BruseNetwork network, ArrayList<BruseEvidence> softevidence) {
		String netName = network.getName();
		String ev = getEvidenceNames(softevidence);
		int n = network.getAllNodes().size();
		int r = getMaxStateSize(network);
		long p = BookKeepingMgr.NumCliques;
		long m = BookKeepingMgr.MaxVarsInClique;
		long it = BookKeepingMgr.NumIPFPIterations;
		long ops = BookKeepingMgr.NumTableAdds + BookKeepingMgr.NumTableDivs + BookKeepingMgr.NumTableMults;
		int e = softevidence.size();
		
		long maxops = 0;
		
		if (algorithm.equalsIgnoreCase("HUGIN") || algorithm.equalsIgnoreCase("LAZY")) {
			maxops = (long)(5 * p * Math.pow(r, m) + (2 * Math.pow(r, m) + r) * e * it);
		}
		else if (algorithm.equalsIgnoreCase("WRAPPER")) {
			// iterates over se
			maxops = (long)(10 * p * Math.pow(r, m) + 2 * Math.pow(r, m) + (2 * Math.pow(r, m) + r) * e * it);
		}
		else if (algorithm.equalsIgnoreCase("WRAPPER2")) {
			// iterates over whole network
			maxops = (long)(e * it * (5 * p * Math.pow(r, m) + Math.pow(r, m) + r));
		}
		
		StringBuilder stats = new StringBuilder();
		
		stats.append("\nAlgorithm: " + algorithm + "\n");
		stats.append("Network: " + netName + " (" + n + " nodes)\n");
		stats.append("Evidence: " + ev + "\n");
		stats.append("r = " + r + ", p = " + p + ", m = " + m + ", it = " + it + ", e = " + e + "\n");
		stats.append("Max Ops = " + maxops + "\n");
		stats.append("Total Ops = " + ops + "\n\n");
		
		// Append to data file
		try {
			File f = new File(filename);
			if (!f.exists()) f.createNewFile();
			
			FileOutputStream file = new FileOutputStream(f, true); //(filename, true);
			DataOutputStream out   = new DataOutputStream(file);
			out.writeBytes(stats.toString());
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Output file " + filename + " not found.");
			e1.printStackTrace();
		} catch (IOException e1) {
			System.err.println("Error appending to " + filename + ": " + e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	public static void checkConvergence(double[] huginvals, double[] lazyvals, double[] wrapper2vals, double[] wrapper4vals) {
		
		// All arrays should be the same size and ordered the same
		for (int i=0; i < huginvals.length; i++) {
			if ((!isEqual(huginvals[i], lazyvals[i])) || (!isEqual(lazyvals[i], wrapper2vals[i])) || (!isEqual(wrapper2vals[i], wrapper4vals[i]))) {
				System.out.println("Hugin: " + huginvals[i] + " Lazy: " + lazyvals[i] + " Wrapper2: " + wrapper2vals[i] + " Wrapper4: " + wrapper4vals[i]);
				System.err.println("Methods did not converge to same value!");
				return;
			}
		}
		System.out.println("Successful convergence!");
	}

}
