package sc.bruse.api;

/***
 * The API exception class
 * 
 * @author langevin
 */
public class BruseAPIException extends Exception {
	
	/***
	 * BruseAPIException construction
	 *
	 */
	public BruseAPIException() {
		super();
	}
	
	/***
	 * BruseAPIException constructor
	 * @param msg is the error message associated with the exception
	 */
	public BruseAPIException(String msg) {
		super(msg);
	}
}
