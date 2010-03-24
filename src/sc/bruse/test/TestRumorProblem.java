package sc.bruse.test;

import java.util.ArrayList;

import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;
import sc.bruse.network.BruseSoftEvidenceState;

public class TestRumorProblem {

	static double epsilon = 0.00001;
	
	private static boolean isEqual(double d1, double d2) {
		return (Math.abs( d1 - d2 ) < epsilon );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/rumorproblem.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			engine.init();
		
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("B");
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("true", 0.8));
			beliefs.add(new BruseSoftEvidenceState("false", 0.1));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			ev = new BruseEvidence("C");
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			beliefs.add(new BruseSoftEvidenceState("true", 0.1));
			beliefs.add(new BruseSoftEvidenceState("false", 0.9));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.propagate();

		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
