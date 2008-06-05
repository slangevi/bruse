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

public class BookKeepingMgr {

	public static int NumTableAbsoptions = 0;
	public static int NumTableMults = 0;
	public static int NumTableMarginals = 0;
	public static int NumTableAdds = 0;
	public static int NumIPFPIterations = 0;
	public static int IPFPTableSize = 0;
	public static long TriangulationSize = 0;
	public static long NumCliques = 0;
	public static long MaxCliqueSize = 0;
	public static long TimeNetworkLoad = 0;
	public static long TimeCreateCliques = 0;
	public static long TimeCreateJunctionTree = 0;
	public static long TimeTriangulation = 0;
	public static long TimeApplySoftEvidence = 0;
	public static long TimePropagation = 0;
	public static long TMP = 0;
	
	//TODO add bookkeeping for triangulation size, largest clique, number of cliques, etc
	
	public static void reset() {
		NumTableAbsoptions = 0;
		NumTableMults = 0;
		NumTableMarginals = 0;
		NumTableAdds = 0;
		NumIPFPIterations = 0;
		TimeNetworkLoad = 0;
		TimeCreateCliques = 0;
		TimeCreateJunctionTree = 0;
		TimeTriangulation = 0;
		TimeApplySoftEvidence = 0;
		TimePropagation = 0;
		IPFPTableSize = 0;
		TriangulationSize = 0;
		NumCliques = 0;
		MaxCliqueSize = 0;
		TMP = 0;
	}
	
	public static void dumpBookKeeping() {
		System.out.println("************************************************");
		System.out.println("* Number of Table Absorptions: " + NumTableAbsoptions);
		System.out.println("* Number of Table Multiplies: " + NumTableMults);
		System.out.println("* Number of Table Marginalizations: " + NumTableMarginals);
		System.out.println("* Number of Table Additions: " + NumTableAdds);
		System.out.println("* Number of IPFP Iterations: " + NumIPFPIterations);
		System.out.println("* Triangulation Size: " + TriangulationSize);
		System.out.println("* Number of Cliques:" + NumCliques);
		System.out.println("* Max Clique Size: " + MaxCliqueSize);		
		System.out.println("* IPFP Table Size: " + IPFPTableSize);
		System.out.println("* Time to load Network: " + TimeNetworkLoad);
		System.out.println("* Time to Triangulate: " + TimeTriangulation);
		System.out.println("* Time to create Cliques: " + TimeCreateCliques);
		System.out.println("* Time to create JunctionTree: " + TimeCreateJunctionTree);
		System.out.println("* Time to apply Soft Evidence: " + TimeApplySoftEvidence);
		System.out.println("* Time to propagate: " + TimePropagation);
		System.out.println("* TMP: " + TMP);
		System.out.println("************************************************");
	}
	
	public static void dumpBookKeepingTab() {
		System.out.println(IPFPTableSize + "\t" + NumIPFPIterations + "\t" + NumTableMults + "\t" + NumTableAdds + "\t\t" + TimePropagation);
	}
}
