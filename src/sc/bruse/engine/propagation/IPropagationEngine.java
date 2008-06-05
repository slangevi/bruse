package sc.bruse.engine.propagation;

import java.util.*;
import sc.bruse.api.*;

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
