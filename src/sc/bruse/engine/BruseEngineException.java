package sc.bruse.engine;

/***
 * The main engine exception class
 * 
 * @author langevin
 */
public class BruseEngineException extends Exception {
	
	/***
	 * BruseEngineException construction
	 *
	 */
	public BruseEngineException() {
		super();
	}
	
	/***
	 * BruseEngineException constructor
	 * @param msg is the error message associated with the exception
	 */
	public BruseEngineException(String msg) {
		super(msg);
	}
}
