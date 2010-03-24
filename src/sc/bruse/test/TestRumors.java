package sc.bruse.test;

import java.text.DecimalFormat;
import java.util.ArrayList;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseNodeState;
import sc.bruse.network.BruseSoftEvidenceState;

public class TestRumors {
	static double epsilon = 0.00001;
	
	private static boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	public static void dumpNetwork(BruseNetwork network) {
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/rumor1.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("A_B");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("TT", 0.15));
			beliefs.add(new BruseSoftEvidenceState("TF", 0.2));
			beliefs.add(new BruseSoftEvidenceState("FT", 0.3));
			beliefs.add(new BruseSoftEvidenceState("FF", 0.35));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.init();
			engine.propagate();
			BookKeepingMgr.dumpBookKeeping();
			dumpNetwork(network);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
