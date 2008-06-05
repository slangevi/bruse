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

/***
 * This graph node can be used as a directed or undirected graph node
 * 
 * @author langevin
 *
 */
public class GraphNode {

	private static int uniqueId = 0;
	protected String				m_name;
	protected ArrayList<GraphNode> 	m_parents;
	protected ArrayList<GraphNode> 	m_children;
	protected ArrayList<GraphNode> 	m_neighbors;
	protected ArrayList<Integer> 	m_edgeWeights;
	
	public GraphNode() {
		// assign a unique id
		m_name = new String(Integer.toString(uniqueId++));
		m_parents = new ArrayList<GraphNode>();
		m_children = new ArrayList<GraphNode>();
		m_neighbors = new ArrayList<GraphNode>();
		m_edgeWeights = new ArrayList<Integer>();
	}
	
	public GraphNode(String name) {
		m_name = name;
		m_parents = new ArrayList<GraphNode>();
		m_children = new ArrayList<GraphNode>();
		m_neighbors = new ArrayList<GraphNode>();
		m_edgeWeights = new ArrayList<Integer>();
	}
	
	public void addParent(GraphNode node) {
		if(m_parents.contains(node) == false) {
			m_parents.add(node);
		}
		addNeighbor(node);
	}
	
	public void addChild(GraphNode node) {
		if (m_children.contains(node) == false) {
			m_children.add(node);
		}
		addNeighbor(node);
	}
	
	public void addNeighbor(GraphNode node) {
		if (m_neighbors.contains(node) == false) {
			m_neighbors.add(node);
			m_edgeWeights.add(new Integer(0));  // create a 0 edge weight
		}
	}
	
	public void removeChild(GraphNode node) {
		if (m_children.contains(node)) {
			m_children.remove(node);
		}
		removeNeighbor(node);
	}
	
	public void removeParent(GraphNode node) {
		if(m_parents.contains(node)) {
			m_parents.remove(node);
		}
		removeNeighbor(node);
	}
	
	public void removeNeighbor(GraphNode node) {
		if (m_neighbors.contains(node)) {
			int index = m_neighbors.indexOf(node);
			m_neighbors.remove(node);
			m_edgeWeights.remove(index);	// remove the edge weight also
		}
	}
	
	public ArrayList<GraphNode> getParents() {
		return m_parents;
	}
	
	public ArrayList<GraphNode> getChildren() {
		return m_children;
	}
	
	public ArrayList<GraphNode> getNeighbors() {
		return m_neighbors;
	}
	
	public String getName() {
		return m_name;
	}
	
	public void addEdgeWeight(GraphNode neighbor, int weight) {
		int index = m_neighbors.indexOf(neighbor);
		
		m_edgeWeights.set(index, new Integer(weight));
	}
	
	public void removeEdgeWeight(GraphNode neighbor) {
		int index = m_neighbors.indexOf(neighbor);
		
		// set the edge weight to 0
		m_edgeWeights.set(index, new Integer(0));
	}
	
	public int getEdgeWeight(GraphNode neighbor) {
		int index = m_neighbors.indexOf(neighbor);
		Integer weight = m_edgeWeights.get(index);
		return weight.intValue();
	}
	
	public GraphNode clone() {
		//NOTE: Will not clone neighbors and edge weights...use graph clone for this
		GraphNode clone = new GraphNode(m_name);
		return clone;
	}
}
