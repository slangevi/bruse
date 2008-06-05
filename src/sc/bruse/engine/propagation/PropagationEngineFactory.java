package sc.bruse.engine.propagation;

import sc.bruse.engine.*;
// Import all the propagation engine packages
import sc.bruse.engine.propagation.junctiontree.*;

/***
 * Creates a propagation engine using the specified propagation type
 * 
 * @author langevin
 *
 */
public class PropagationEngineFactory {
	
	public enum PropagationType { Hugin, Lazy, Wrapper, Wrapper2, Wrapper3, Wrapper4 };
	
	/***
	 * Creates a propagation engine based on the specified propagation type
	 * 
	 * @param type is the propagation engine to use (currently only Hugin supported)
	 * @return is a propagation engine
	 * @throws BruseEngineException
	 */
	static public IPropagationEngine create(PropagationType type) throws BruseEngineException {
		if (type == PropagationType.Hugin) {
			return new HuginPropagationEngine();
		}
		else if (type == PropagationType.Lazy) {
			return new LazyPropagationEngine();
		}
		else if (type == PropagationType.Wrapper) {
			return new WrapperPropagationEngine();
		}
		else if (type == PropagationType.Wrapper2) {
			return new WrapperPropagationEngine2();
		}
		else if (type == PropagationType.Wrapper3) {
			return new WrapperPropagationEngine3();
		}
		else if (type == PropagationType.Wrapper4) {
			return new WrapperPropagationEngine4();
		}
		
		throw new BruseEngineException("Unsupported propagation type.");
	}
}
