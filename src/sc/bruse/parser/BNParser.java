package sc.bruse.parser;

import java.util.Collection;
import java.util.Hashtable;

import sc.bruse.network.BruseAPIException;

public abstract class BNParser {
	protected String m_filename;
	protected Hashtable<String, Variable> m_variables;
	
	public BNParser(String filename) {
		m_filename = filename;
		m_variables = new Hashtable<String, Variable>();
	}
	
	public Collection<Variable> getVariables() {
		return m_variables.values();
	}
	
	public abstract void parse() throws BruseAPIException;
}
