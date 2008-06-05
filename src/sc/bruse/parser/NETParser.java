package sc.bruse.parser;

import java.io.*;
import java.util.*;
import sc.bruse.api.*;

public class NETParser extends BNParser {

	private enum STATE { NODE, POTENTIAL, STATES, PARENTS }
	private STATE m_curState;
	private Variable m_curVar;
	private int m_curRowIdx;
	private int m_curColIdx;
	private int m_curColSize;

	public NETParser(String filename) {
		super(filename);
		m_curState = STATE.NODE;
	}
	
	public void parse() throws BruseAPIException {
		try {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(m_filename));
			
			// read file line by line and process each line
			while (reader.ready()) {
				line = reader.readLine();
				line.trim();
				if (line != "\n") { // ignore blank lines
					processLine(line);
				}
			}
		}
		catch (FileNotFoundException e) {
			System.err.println("File " + m_filename + " not found.");
			throw new BruseAPIException("Unable to parse file");
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
			throw new BruseAPIException("Unable to parse file");
		}
	}
	
	private void processLine(String line) {
		switch (m_curState) {
		case NODE:
			createNode(line);
			break;
		case STATES:
			createStates(line);
			break;
		case PARENTS:
			createParents(line);
			break;
		case POTENTIAL:
			createPotential(line);
			break;
		}
	}
	
	private void createNode(String line) {
		if (line.startsWith("node")) {
			String name = line.substring(4).trim(); // strip out "node " and trailing blanks
			m_curVar = new Variable(name);
			m_variables.put(name, m_curVar);
			m_curState = STATE.STATES; // move to reading the variable states
		}
		else if (line.startsWith("potential")) {
			m_curState = STATE.PARENTS; // transition to parents state - we are done reading nodes
			createParents(line);
		}
	}
	
	private void createStates(String line) {
		String states = line.trim();

		if (states.startsWith("states")) {
			StringBuffer buf = new StringBuffer();
			states = states.substring(10, states.length()-2);  // strip out "states = (" and trailing ");"
			char c;
			
			for (int i=0; i < states.length(); i++) {
				c = states.charAt(i);
				if (c != '"' && c != ' ' && c != '\n') {
					buf.append(c);
				}
				else if (c != ' ') {
					if (buf.length() > 0) {
						m_curVar.addState(buf.toString());
						buf = new StringBuffer();
					}
				}
			}
			m_curState = STATE.NODE; // move to reading nodes
		}
	}
	
	private void createParents(String line) {
		String str = line.trim();
		
		if (str.startsWith("potential")) {
			StringBuffer buf = new StringBuffer();
			Variable parent = null;
			int idx = str.indexOf("|");
			
			if (idx != -1) { // there are parent nodes
				String node = str.substring(11, idx-1).trim();	// find the node name
				String parents = str.substring(idx+1, str.length()).trim();	// strip out "potential ("
				m_curVar = m_variables.get(node);	// set the current variable
				char c;
			
				for (int i=0; i < parents.length(); i++) {
					c = parents.charAt(i);
					if (c != ' ' && c != '\n' && c != ')') {
						buf.append(c);
					}
					else {
						if (buf.length() > 0) {
							parent = m_variables.get(buf.toString());
							m_curVar.addParent(parent);
							buf = new StringBuffer();
						}
					}
				}
			}
			else {
				String node = str.substring(11, str.length()-1).trim();	// find the node name
				m_curVar = m_variables.get(node);	// set the current variable
			}
			
			m_curVar.createTable();	// all parents are known so size of table is known - create table
			m_curColSize = m_curVar.getStates().size(); // keep track of the current col size
			m_curState = STATE.POTENTIAL;	// move to reading the variable potentials
		}
	}
	
	private void createPotential(String line) {
		int idx = -1;
		String pot = line.trim();
		
		if (pot.startsWith("data =") || pot.startsWith("=")) {
			idx = pot.indexOf('=');
			m_curRowIdx = 0; // initialize cur Row Index to first row
			m_curColIdx = 0; // initialize cur Col Index to first col
			if (pot.length() == 6) return; // if the line only contains "data =" then return
			pot = pot.substring(idx+2); // strip out "= " and everything left of
		}
		else if (pot.startsWith("data")) return; // if the line only contains "data" then return
		
		StringBuffer buf = new StringBuffer();
		double val = 0;
		char c;
		
		for (int i=0; i < pot.length(); i++) {
			c = pot.charAt(i);
			if (c != '(' && c != ' ' && c != ')' && c != '{' && c != ';' && c != '%' && c != '\t') {
				buf.append(c);
			}
			else if (c == ' ') {
				// add val and move to next row
				if (buf.length() > 0) {
					idx = m_curRowIdx * m_curColSize + m_curColIdx;  //TODO change to column major order
					val = Double.parseDouble(buf.toString());
					m_curVar.putTableEntry(idx, val);
					m_curColIdx++;	// move to next row
					buf = new StringBuffer();
				}
			}
			else if (c == '%')  {
				// done with this row - move to next row
				m_curRowIdx++;
				// move back to the first col
				m_curColIdx = 0;
				break;	// nothing else is needed from this line so break out of read loop
			}
			else if (c == ';') {
				// done with this potential
				m_curState = STATE.PARENTS;
				break;
			}
		}
	}
}
