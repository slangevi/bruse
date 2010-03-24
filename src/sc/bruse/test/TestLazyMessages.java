package sc.bruse.test;

import java.util.ArrayList;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.propagation.IPropagationEngine;
import sc.bruse.engine.propagation.PropagationEngineFactory;
import sc.bruse.network.BruseAPIException;
import sc.bruse.network.BruseEvidence;
import sc.bruse.network.BruseNetwork;
import sc.bruse.network.BruseNetworkFactory;

public class TestLazyMessages {

	public static void main(String[] args) {
		try {
			BruseNetwork network = BruseNetworkFactory.create("tests/diabetes.net");
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Lazy);
			engine.setNetwork(network);
			engine.init();
			
			// test adding evidence */
			/*ArrayList<BruseEvidence> evidence = new ArrayList<BruseEvidence>();
			BruseEvidence ev = new BruseEvidence("John");
			ev.setHardEvidence("Sick");
			evidence.add(ev);
			engine.addEvidence(evidence);
			*/
			engine.propagate();
			
			BookKeepingMgr.dumpBookKeeping();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
