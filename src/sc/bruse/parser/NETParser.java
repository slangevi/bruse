package sc.bruse.parser;

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

import java.io.*;
import java.util.*;

import sc.bruse.network.*;

public class NETParser extends BNParser {

	private enum STATE { NODE, POTENTIAL, STATES, PARENTS }
	private STATE m_curState;
	private Variable m_curVar;
	private int m_curRowIdx;
	private int m_curColIdx;
	private int m_curColSize;
	private BufferedReader m_reader = null;

	public NETParser(String filename) {
		super(filename);
		m_curState = STATE.NODE;
	}
	
	public NETParser(BufferedReader reader) {
		super("Stream");
		m_curState = STATE.NODE;
		m_reader = reader;
	}
	
	public void parse() throws BruseAPIException {
		try {
			String line = "";
			if (m_reader == null) m_reader = new BufferedReader(new FileReader(m_filename));
			
			// read file line by line and process each line
			while (m_reader.ready()) {
				line = m_reader.readLine();
				line.trim();
				if (line != "\n") { // ignore blank lines
					processLine(line);
				}
			}
			
			for (Variable var: this.getVariables()) {
				var.normalizeTable();
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
		else if (m_curVar != null) {
			String l = line.trim();
			if (l.startsWith("HR_Desc")) {
				String desc = l.substring(11, l.length()-2);
				m_curVar.setDesc(desc);
			}
			else if (l.startsWith("HR_State")) {
				try {
					int idx = l.indexOf("=");
					int stateIdx = Integer.parseInt(l.substring(9, idx-1));
					String stateDesc = l.substring(idx+3, l.length()-2);
					m_curVar.addStateDesc(m_curVar.getStates().get(stateIdx), stateDesc);
				}
				catch (Exception e) {
					System.err.println("Unable to parse state description: " + l);
				}
			}
		}
	}
	
	private void createStates(String line) {
		String states = line.trim();

		if (states.startsWith("states") || states.startsWith("state_values")) {
			StringBuffer buf = new StringBuffer();
			int idx = states.indexOf("="); //10
			states = states.substring(idx+3, states.length()-2);  // strip out "states = (" and trailing ");"
			states += " "; // HACK: quick fix for state_values parsing so the last state is processed
			char c;
			
			for (int i=0; i < states.length(); i++) {
				c = states.charAt(i);
				if (c != '"' && c != ' ' && c != '\n') {
					buf.append(c);
				}
				else {//if (c != ' ') {
					if (buf.length() > 0) {
						m_curVar.addState(buf.toString());
						buf = new StringBuffer();
					}
				}
			}
			// if states were read then move to reading nodes state, otherwise we are looking for state_values
			if (m_curVar.getStates().size() > 0) 
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
		else if (pot.startsWith("model_nodes")) return; // skip over model_nodes field (not supported)
		else if (pot.startsWith("model_data")) return; // skip over model_data field (not supported)
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
