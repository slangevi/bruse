package sc.bruse.engine;

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
