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
import sc.bruse.network.BruseTable;

public class TestRumors2 {
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
			BruseNetwork network = BruseNetworkFactory.create("tests/rumor3.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("O");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("TTT", 0.1));
			beliefs.add(new BruseSoftEvidenceState("TTF", 0.05));
			beliefs.add(new BruseSoftEvidenceState("TFT", 0.12));
			beliefs.add(new BruseSoftEvidenceState("TFF", 0.05));
			beliefs.add(new BruseSoftEvidenceState("FTT", 0.08));
			beliefs.add(new BruseSoftEvidenceState("FTF", 0.2));
			beliefs.add(new BruseSoftEvidenceState("FFT", 0.3));
			beliefs.add(new BruseSoftEvidenceState("FFF", 0.1));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.init();
			engine.propagate();
			BookKeepingMgr.dumpBookKeeping();
			dumpNetwork(network);
			
			//////////
			
			network = BruseNetworkFactory.create("tests/rumor2.net");
			engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			evidence = new ArrayList<BruseEvidence>();
			ev = new BruseEvidence("O1");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("TT", 0.18));
			beliefs.add(new BruseSoftEvidenceState("TF", 0.25));
			beliefs.add(new BruseSoftEvidenceState("FT", 0.42));
			beliefs.add(new BruseSoftEvidenceState("FF", 0.15));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("O2");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("TT", 0.15));
			beliefs.add(new BruseSoftEvidenceState("TF", 0.17));
			beliefs.add(new BruseSoftEvidenceState("FT", 0.28));
			beliefs.add(new BruseSoftEvidenceState("FF", 0.4));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.init();
			engine.propagate();
			BookKeepingMgr.dumpBookKeeping();
			dumpNetwork(network);
			
			////////////////////////
			
			network = BruseNetworkFactory.create("tests/rumor4.net");
			engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			evidence = new ArrayList<BruseEvidence>();
			ev = new BruseEvidence("B");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("true", 0.6));
			beliefs.add(new BruseSoftEvidenceState("false", 0.4));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("C");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("true", 0.32));
			beliefs.add(new BruseSoftEvidenceState("false", 0.68));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.init();
			engine.propagate();
			BookKeepingMgr.dumpBookKeeping();
			dumpNetwork(network);
			
			
			////////////////////////
			
			network = BruseNetworkFactory.create("tests/rumor5.net");
			engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			evidence = new ArrayList<BruseEvidence>();
			ev = new BruseEvidence("O");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("TT", 0.22));
			beliefs.add(new BruseSoftEvidenceState("TF", 0.1));
			beliefs.add(new BruseSoftEvidenceState("FT", 0.38));
			beliefs.add(new BruseSoftEvidenceState("FF", 0.3));
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
