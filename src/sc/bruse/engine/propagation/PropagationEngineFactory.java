package sc.bruse.engine.propagation;

/***********************************
 * Copyright 2008 Scott Langevin
 * 
 * All Rights Reserved.
 *
 * This file is part of BRUSE.
 *
 * BRUSE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BRUSE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BRUSE.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Scott Langevin (scott.langevin@gmail.com)
 *
 */

import sc.bruse.engine.*;
// Import all the propagation engine packages
import sc.bruse.engine.propagation.hugin.HuginPropagationEngine;
import sc.bruse.engine.propagation.lazy.LazyPropagationEngine;
import sc.bruse.engine.propagation.spacesaving.SpaceSavingPropagationEngine;
import sc.bruse.engine.propagation.wrapper.OldWrapperPropagationEngine;
import sc.bruse.engine.propagation.wrapper.WrapperPropagationEngine1;
import sc.bruse.engine.propagation.wrapper.OldWrapperPropagationEngine3;
import sc.bruse.engine.propagation.wrapper.WrapperPropagationEngine2;

/***
 * Creates a propagation engine using the specified propagation type
 * 
 * @author langevin
 *
 */
public class PropagationEngineFactory {
	
	public enum PropagationType { Hugin, Lazy, Wrapper1, Wrapper2, SpaceSaving };
	
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
		else if (type == PropagationType.Wrapper1) {
			return new WrapperPropagationEngine1();
		}
		else if (type == PropagationType.Wrapper2) {
			return new WrapperPropagationEngine2();
		}
		else if (type == PropagationType.SpaceSaving) {
			return new SpaceSavingPropagationEngine();
		}
		
		throw new BruseEngineException("Unsupported propagation type.");
	}
}
