package sc.bruse.engine;

import java.util.*;

import sc.bruse.api.*;
import sc.bruse.engine.propagation.*;

public class Entropy {

	public static double getEntropy(BruseNode node) {
		double entropy = 0;
		double statevals[] = node.getStateValues();
		
		// Entropy forumla: -·p(V=v)lg(p(V=v))
		for (int i=0; i < statevals.length; i++) {
			// if the state value is 0 then skip it, it won't affect entropy
			if (statevals[i] != 0) {
				entropy -= statevals[i] * Math.log(statevals[i]) / Math.log(2);
			}
		}
		
		return entropy;
	}
	
	public static double getExpectedEntropy(IPropagationEngine propEngine, BruseNode node, BruseNode test) {
		int i = 0;
		double entropy = 0;
		double exentropy = 0;
		ArrayList<BruseEvidence> curevidence = propEngine.getEvidence(); // save the current evidence
		double statevals[], tstatevals[] = test.getStateValues();
		LinkedList<BruseNodeState> tstates = test.getStates();
		ListIterator<BruseNodeState> it = tstates.listIterator();
		
		BruseNodeState tstate = null;
		
		// Expected Entropy forumla: ·p(T=t)H(V|t) where H(V|t) = -·p(V=v|T=t)lg(p(V=v|T=t))
		while (it.hasNext()) {
			tstate = it.next();
			
			// if the tstate value is 0 then skip it, it won't affect entropy
			if (tstatevals[i] != 0) {
				// Add the test node state as evidence
				BruseEvidence testevidence = new BruseEvidence(test);
				testevidence.setHardEvidence(tstate.getStateName());
				propEngine.addEvidence(testevidence);
				
				// propagate new evidence
				propEngine.propagate();
				
				// get the updated state values for node
				statevals = node.getStateValues();
				
				// calculate contribution to entropy for this state
				entropy += tstatevals[i] * getEntropy(node);
				
				// reset evidence back to current evidence
				propEngine.removeAllEvidence();
				propEngine.addEvidence(curevidence);
			}
			
			i++;
		}
		
		return entropy;
	}
}
