package sc.bruse.test;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import sc.bruse.engine.BruseEngineException;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.BruseAPIException;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;
import sc.bruse.network.BruseNode;
import sc.bruse.network.BruseNodeState;
import sc.bruse.network.BruseSoftEvidenceState;

public class TestNewParser {

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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/decisiont20090727_d.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			engine.init();
			engine.propagate();
			
			/*
			
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("Active_17");
			ev.setHardEvidence("No");
			evidence.add(ev);
			
			int count = 1;
			String name = "";
			for (int i=1; i < 22; i++) {
				if (i != 17) {
					name = "Active_" + i;
				
					if (i >= 6 || i <= 9) {
						name = "Active_6_" + count;
						count++;
					}
					if (i ==14) { 
						name = "Active_11_1";
					}
					if (i == 15) {
						name = "Active_11_2";
					}
					
					ev = new BruseEvidence(name);
					ev.setHardEvidence("No");
					evidence.add(ev);
				}
			}
			
			engine.addEvidence(evidence);
			
			engine.propagate();
			
			engine.removeAllEvidence();
			//dumpNetwork(network);
			
			evidence = new ArrayList<BruseEvidence>();
			ev = new BruseEvidence("Active_17");
			ev.setHardEvidence("Yes");
			evidence.add(ev);
			
			ev = new BruseEvidence("Network_Size");
			ev.setHardEvidence("Large");
			evidence.add(ev);
			
			count = 1;
			name = "";
			for (int i=1; i < 22; i++) {
				if (i != 17) {
					name = "Active_" + i;
				
					if (i >= 6 || i <= 9) {
						name = "Active_6_" + count;
						count++;
					}
					if (i ==14) { 
						name = "Active_11_1";
					}
					if (i == 15) {
						name = "Active_11_2";
					}
					
					ev = new BruseEvidence(name);
					ev.setHardEvidence("No");
					evidence.add(ev);
				}
			}
			
			engine.addEvidence(evidence);
			
			engine.propagate();
			
			BruseNode node = engine.getNetwork().getNode("IN");
			double[] vals = node.getStateValues();
			
			for (double val: vals) {
				System.out.println(val + ",");
			}
			
			dumpNetwork(network);*/
			
			ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			
			ArrayList<BruseSoftEvidenceState> beliefs = new ArrayList<BruseSoftEvidenceState>();
			BruseEvidence ev = new BruseEvidence("IN");
			beliefs.add(new BruseSoftEvidenceState("0", 0.6));
			beliefs.add(new BruseSoftEvidenceState("1", 0.2));
			beliefs.add(new BruseSoftEvidenceState("2", 0.2));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			ev = new BruseEvidence("NH");
			beliefs.add(new BruseSoftEvidenceState("0", 0.5));
			beliefs.add(new BruseSoftEvidenceState("1", 0.3));
			beliefs.add(new BruseSoftEvidenceState("2", 0.2));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			ev = new BruseEvidence("NP");
			beliefs.add(new BruseSoftEvidenceState("0", 0.55));
			beliefs.add(new BruseSoftEvidenceState("1", 0.25));
			beliefs.add(new BruseSoftEvidenceState("2", 0.2));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			ev = new BruseEvidence("SS");
			beliefs.add(new BruseSoftEvidenceState("0", 0.1));
			beliefs.add(new BruseSoftEvidenceState("1", 0.4));
			beliefs.add(new BruseSoftEvidenceState("2", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			ev = new BruseEvidence("SC");
			beliefs.add(new BruseSoftEvidenceState("0", 0.1));
			beliefs.add(new BruseSoftEvidenceState("1", 0.4));
			beliefs.add(new BruseSoftEvidenceState("2", 0.5));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			beliefs = new ArrayList<BruseSoftEvidenceState>();
			ev = new BruseEvidence("SF");
			beliefs.add(new BruseSoftEvidenceState("0", 0.05));
			beliefs.add(new BruseSoftEvidenceState("1", 0.4));
			beliefs.add(new BruseSoftEvidenceState("2", 0.55));
			ev.setSoftEvidence(beliefs);
			evidence.add(ev);
			
			engine.addEvidence(evidence);
			
			engine.propagate();
			
			dumpNetwork(network);
			
		} catch (BruseAPIException e) {
			JOptionPane.showMessageDialog(null, "Unable to open decision support file: " + e.getMessage());
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (BruseEngineException e) {
			JOptionPane.showMessageDialog(null, "Unable to create propagation engine: " + e.getMessage());
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
