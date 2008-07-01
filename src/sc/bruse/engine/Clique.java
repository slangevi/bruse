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


/**
 * This class represents a clique in a BRUSE network.  
 * 
 * Each member of the clique is a BruseNode belonging to the same BRUSE network and is pairwise connected. 
 * The clique also represents the joint probability of the BruseNode members and contains a BruseTable.
 * 
 * @author langevin
 *
 */
public class Clique {

	Hashtable<String, BruseNode> m_index;
	ArrayList<BruseNode> m_members;
	BruseTable m_table;
	ArrayList<BruseTable> m_initpotentials; // used to quickly roll-back to initial potenials (Lazy Propagation)
	ArrayList<BruseTable> m_potentials;
	
	
	/**
	 * The Clique constructor.
	 * 
	 * @param nodes is an array of BruseNodes that are members of this clique
	 */
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
	
	/**
	 * Method to force the rebuilding of the joint probability table for this clique.
	 */
	public void rebuildTable() {
		createTable();
	}
	
	
	/**
	 * Create the table that represents the joint probability of the members of this clique.
	 * The table is initialized to a unit potential.
	 */
	private void createTable() {
		m_table = new BruseTable(m_members);
		m_table.makeUnit();
	}
	
	
	/**
	 * This method checkpoints the potentials associated with this clique so they can be rolled back for re-initialization.
	 * This is currently used for re-running an inference algorithm.
	 */
	public void setInitPotentials() {
		m_initpotentials.clear();
		m_initpotentials.addAll(m_potentials);
	}
	
	//TEST
	public void addNode(BruseNode node) {
		m_members.add(node);
		m_index.put(node.getName(), node);
	}
	
	/**
	 * This method internally adds nodes to the clique.
	 * 
	 * @param nodes
	 */
	private void addNodes(BruseNode nodes[]) {
		BruseNode node = null;
		
		for (int i=0; i < nodes.length; i++) {
			node = nodes[i];
			m_members.add(node);
			m_index.put(node.getName(), node);
		}
	}
	
	/**
	 * This method returns whether a node with the given name is a member of this clique.
	 * 
	 * @param name is the name of the node to test for membership
	 * @return whether a node with the specified name is a member of this clique.
	 */
	public boolean containsNode(String name) {
		return m_index.containsKey(name);
	}
	
	/**
	 * This method returns whether all the nodes with the given names are members of this clique.
	 * 
	 * @param names is a list of names of nodes to test for membership
	 * @return whether all the nodes with the specified names are members of this clique.
	 */
	public boolean containsNodes(ArrayList<String> names) {
		for (int i=0; i < names.size(); i++) {
			if (m_index.containsKey(names.get(i)) == false) return false;
		}
		return true;
	}
	
	/***
	 * This method returns whether the given clique is a subset of this clique.
	 * 
	 * @param clique is the clique to test as a subset
	 * @return whether the clique is a subset of this clique
	 */
	public boolean isSubSet(Clique clique) {
		// Return whether this clique is a subset of the passed in clique
	
		// if clique contains all of this cliques members then it is a subset
		return clique.getMembers().containsAll(m_members);
	}
	
	/**
	 * Returns the list of BruseNodes that are members of this clique.
	 * 
	 * @return a list of BruseNodes
	 */
	public ArrayList<BruseNode> getMembers() {
		return m_members;
	}
	
	/**
	 * Associate a list of potentials with this clique.
	 * 
	 * @param potentials the list of potentials
	 */
	public void addPotentials(ArrayList<BruseTable> potentials) {
		m_potentials.addAll(potentials);
	}
	
	/**
	 * Associate a potential with this clique.
	 * 
	 * @param potential the potential to associate
	 */
	public void addPotential(BruseTable potential) {
		m_potentials.add(potential);
	}
	
	/**
	 * Remove a potentials from this clique.
	 * 
	 * @param potential is the potential to remove
	 */
	public void removePotential(BruseTable potential) {
		m_potentials.remove(potential);
	}
	
	/**
	 * Remove a list of potentials from this clique.
	 * 
	 * @param potentials is the list of potentials to associate
	 */
	public void removePotentials(ArrayList<BruseTable> potentials) {
		m_potentials.removeAll(potentials);
	}
	
	/**
	 * Remove all potentials from this clique.
	 */
	public void clearPotentials() {
		m_potentials.clear();
	}
	
	/**
	 * Reset the potentials associated with this clique back to the initial potentials.
	 */
	public void resetPotentials() {
		// reset the potentials to the initial potentials
		clearPotentials();
		m_potentials.addAll(m_initpotentials);
	}
	
	/**
	 * Return the list of potentials associated with this clique.
	 * 
	 * @return the list of potentials
	 */
	public ArrayList<BruseTable> getPotentials() {
		return m_potentials;
	}
	
	/**
	 * Combine the potentials associated with this clique to calculate joint probablity table.
	 */
	public void combinePotentials() {
		// init the clique by calculating it's joint prob table using the chain rule
		if (m_table == null) createTable();
		m_table.makeUnit();
		
		// multiply the clique table by each potential
		for (int i=0; i < m_potentials.size(); i++) {
			//m_table.absorb(m_potentials.get(i));
			m_table = m_table.multiplyBy(m_potentials.get(i));
		}
	}
	
	/**
	 * Return the joint probability table associated with this clique.
	 * 
	 * @return the joint probability table
	 */
	public BruseTable getTable() {
		if (m_table == null) createTable();
		return m_table;
	}
	
	/**
	 * Explicitly set the joint probability table associated with this clique.
	 * 
	 * @param table the joint probability table
	 */
	public void setTable(BruseTable table) {
		m_table = table;
	}
}
