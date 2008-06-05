package sc.bruse.engine.propagation;

import sc.bruse.api.BruseNetwork;
import sc.bruse.api.BruseNetworkFactory;

public class PerformanceTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BruseNetwork network = BruseNetworkFactory.create("C:\\farm.net");
			// TIMING TEST
			long StartTime = System.currentTimeMillis();
		   
			IPropagationEngine engine = PropagationEngineFactory.create(PropagationEngineFactory.PropagationType.Hugin);
			engine.setNetwork(network);
			
			engine.init();
			engine.propagate();
			
			long EndTime = System.currentTimeMillis();
			System.out.println("***** Initial network built in " + (EndTime - StartTime) + " ms");
		}
		catch (Exception e) {
			//
		}
	}

}
