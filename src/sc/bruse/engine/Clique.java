package sc.bruse.engine;

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

public class Clique {

	Hashtable<String, BruseNode> m_index;
	ArrayList<BruseNode> m_members;
	BruseTable m_table;
	ArrayList<BruseTable> m_initpotentials; // used to quickly roll-back to initial potenials (Lazy Propagation)
	ArrayList<BruseTable> m_potentials;
	
	public Clique(BruseNode nodes[]) {
		super();
		m_index = new Hashtable<String, BruseNode>();
		m_members = new ArrayList<BruseNode>();
		
		addNodes(nodes);
		
		m_initpotentials = new ArrayList<BruseTable>();
		m_potentials = new ArrayList<BruseTable>();
		//m_table = new BruseTable(m_members);
		//m_table.makeUnit();
	}
	
	public void rebuildTable() {
		createTable();
	}
	
	private void createTable() {
		m_table = new BruseTable(m_members);
		m_table.makeUnit();
	}
	
	public void setInitPotentials() {
		m_initpotentials.clear();
		m_initpotentials.addAll(m_potentials);
	}
	
	//TEST
	public void addNode(BruseNode node) {
		m_members.add(node);
		m_index.put(node.getName(), node);
	}
	
	private void addNodes(BruseNode nodes[]) {
		BruseNode node = null;
		
		for (int i=0; i < nodes.length; i++) {
			node = nodes[i];
			m_members.add(node);
			m_index.put(node.getName(), node);
		}
	}
	
	public boolean containsNode(String name) {
		return m_index.containsKey(name);
	}
	
	public boolean containsNodes(ArrayList<String> names) {
		for (int i=0; i < names.size(); i++) {
			if (m_index.containsKey(names.get(i)) == false) return false;
		}
		return true;
	}
	
	public boolean isSubSet(Clique clique) {
		// Return whether this clique is a subset of the passed in clique
	
		// if clique contains all of this cliques members then it is a subset
		return clique.getMembers().containsAll(m_members);
	}
	
	public ArrayList<BruseNode> getMembers() {
		return m_members;
	}
	
	public void addPotentials(ArrayList<BruseTable> potentials) {
		m_potentials.addAll(potentials);
	}
	
	public void addPotential(BruseTable potential) {
		m_potentials.add(potential);
	}
	
	public void removePotential(BruseTable potential) {
		m_potentials.remove(potential);
	}
	
	public void removePotentials(ArrayList<BruseTable> potentials) {
		m_potentials.removeAll(potentials);
	}
	
	public void clearPotentials() {
		m_potentials.clear();
	}
	
	public void resetPotentials() {
		// reset the potentials to the initial potentials
		clearPotentials();
		m_potentials.addAll(m_initpotentials);
	}
	
	public ArrayList<BruseTable> getPotentials() {
		return m_potentials;
	}
	
	public void combinePotentials() {
		// init the clique by calculating it's joint prob table using the chain rule
		if (m_table == null) createTable();
		m_table.makeUnit();
		
		// multiply the clique table by each potential
		for (int i=0; i < m_potentials.size(); i++) {
			m_table.absorb(m_potentials.get(i));
			//m_table = m_table.multiplyBy(m_potentials.get(i));
		}
	}
	
	public BruseTable getTable() {
		if (m_table == null) createTable();
		return m_table;
	}
	
	public void setTable(BruseTable table) {
		m_table = table;
	}
}
