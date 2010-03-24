package sc.bruse.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.StateIterator;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.engine.propagation.hugin.HuginPropagationEngine;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseNodeState;
import sc.bruse.network.BruseSoftEvidenceState;
import sc.bruse.network.BruseTable;

public class TestRumors3 {
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
			BruseNetwork network = BruseNetworkFactory.create("tests/test-a_b-dependent.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			
			double A_T = 0.2;
			double A_F = 0.8;
			double B_T = 0.3;
			double B_F = 0.7;
		
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev_a = new BruseEvidence("A");
			ArrayList<BruseSoftEvidenceState> beliefs_a = new ArrayList<BruseSoftEvidenceState>();
			beliefs_a.add(new BruseSoftEvidenceState("true", A_T));
			beliefs_a.add(new BruseSoftEvidenceState("false", A_F));
			ev_a.setSoftEvidence(beliefs_a);
			evidence.add(ev_a);
			
			BruseEvidence ev_b = new BruseEvidence("B");
			ArrayList<BruseSoftEvidenceState> beliefs_b = new ArrayList<BruseSoftEvidenceState>();
			beliefs_b.add(new BruseSoftEvidenceState("true", B_T));
			beliefs_b.add(new BruseSoftEvidenceState("false", B_F));
			ev_b.setSoftEvidence(beliefs_b);
			evidence.add(ev_b);
			
			engine.addEvidence(evidence);
			
			engine.init();
			engine.propagate();
			
			ArrayList<String> marg_abc = new ArrayList<String>();
			marg_abc.add("A");
			marg_abc.add("B");
			marg_abc.add("C");
			BruseTable abc = ((HuginPropagationEngine)engine).getCliques().get(1).getTable().getMarginal(marg_abc);
			abc.normalize();
			BruseTable.dumpTable(abc, true);
			
			BookKeepingMgr.dumpBookKeeping();
			dumpNetwork(network);

			
			//////////
			
			network = BruseNetworkFactory.create("tests/test-a_b-independent.net");
			engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
		
			evidence = new ArrayList<BruseEvidence>();
			ev_a = new BruseEvidence("A");
			beliefs_a = new ArrayList<BruseSoftEvidenceState>();
			beliefs_a.add(new BruseSoftEvidenceState("true", A_T));
			beliefs_a.add(new BruseSoftEvidenceState("false", A_F));
			ev_a.setSoftEvidence(beliefs_a);
			evidence.add(ev_a);
			
			ev_b = new BruseEvidence("B");
			beliefs_b = new ArrayList<BruseSoftEvidenceState>();
			beliefs_b.add(new BruseSoftEvidenceState("true", B_T));
			beliefs_b.add(new BruseSoftEvidenceState("false", B_F));
			ev_b.setSoftEvidence(beliefs_b);
			evidence.add(ev_b);
			
			engine.addEvidence(evidence);
			
			engine.init();
			engine.propagate();
			
			ArrayList<String> marg_abd = new ArrayList<String>();
			marg_abd.add("A");
			marg_abd.add("B");
			marg_abd.add("D");
			BruseTable abd = ((HuginPropagationEngine)engine).getCliques().get(0).getTable().getMarginal(marg_abd);
			abd.normalize();
			BruseTable.dumpTable(abd, true);
			
			BookKeepingMgr.dumpBookKeeping();
			dumpNetwork(network);
			
			System.out.println("\nRESULTS:\n");
			
			System.out.println("Belief of A in agent C and D:");
			BruseTable.dumpTable(abc.getMarginal("A"), false);
			BruseTable.dumpTable(abd.getMarginal("A"), false);
			
			System.out.println("Belief of B in agent C and D:");
			BruseTable.dumpTable(abc.getMarginal("B"), false);
			BruseTable.dumpTable(abd.getMarginal("B"), false);
			
			System.out.println("Belief of C in agent C:");
			BruseTable.dumpTable(abc.getMarginal("C"), false);
			
			System.out.println("Belief of D in agent D:");
			BruseTable.dumpTable(abd.getMarginal("D"), false);
			
			ArrayList<String> marg = new ArrayList<String>();
			marg.add("A");
			marg.add("B");
			
			BruseTable ab = abd.getMarginal(marg);
			
			System.out.println("Belief of A,B in agent D:");
			BruseTable.dumpTable(ab, true);
			
			BruseTable tmp = abd.divideBy(ab);
			
			ab = abc.getMarginal(marg);
			
			System.out.println("Belief of A,B in agent C:");
			BruseTable.dumpTable(ab, true);
			
			StateIterator it = new StateIterator(abd.getVariables());
			while (it.hasMoreStates()) {
				Map<String, Integer> state = it.nextState();
				double v1 = tmp.getValue(state);
				double v2 = ab.getValue(state);
				abd.setValue(state, v1*v2);
			}
			
			//BruseTable.dumpTable(abd, true);
			
			System.out.println("Belief of A in agent E:");
			BruseTable.dumpTable(abd.getMarginal("A"), false);
			
			System.out.println("Belief of B in agent E:");
			BruseTable.dumpTable(abd.getMarginal("B"), false);
			
			System.out.println("Belief of D in agent E:");
			BruseTable.dumpTable(abd.getMarginal("D"), false);
			
//			BruseTable abcd = tmp.multiplyBy(abd);
//			abcd.normalize();
//			
//			BruseTable.dumpTable(abcd.getMarginal("C"), false);
//			BruseTable.dumpTable(abcd.getMarginal("D"), false);
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
