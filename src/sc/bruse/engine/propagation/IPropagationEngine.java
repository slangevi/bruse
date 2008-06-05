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

import java.util.*;

import sc.bruse.network.*;

/***
 * The propagation engine interface.
 * 
 * All propagation engines must implement this interface.  
 * Typically propagation engines will not implement this interface directly but instead will
 * extend the PropagationEngine abstract class which implements this interface.
 * 
 * @author langevin
 *
 */
public interface IPropagationEngine {

	public BruseNetwork getNetwork();
	public void setNetwork(BruseNetwork network);
	public void init();
	public void propagate();
	public void addEvidence(BruseEvidence evidence);
	public void addEvidence(ArrayList<BruseEvidence> evidence);
	public ArrayList<BruseEvidence> getEvidence();
	public void removeAllEvidence();
	public double getVOI(String targetNodeName, String testNodeName);
}
