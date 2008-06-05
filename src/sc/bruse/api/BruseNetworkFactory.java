package sc.bruse.api;

import sc.bruse.engine.BookKeepingMgr;
import sc.bruse.engine.MoralGraphNode;
import sc.bruse.parser.*;

import java.text.DecimalFormat;
import java.util.*;

/***
 * The Bruse Network Factory class is used to create BruseNetwork objects
 * 
 * @author langevin
 */
public class BruseNetworkFactory {
	
	/***
	 * Creates BruseNetwork objects that load in the Bayesian network file
	 * 
	 *  The BruseNetwork can load the following file types:
	 *  XMLBIF (.xml), NET (.net) files
	 * 
	 * @param filename the Bayesian network file
	 * @return a BruseNetwork 
	 */
	public static BruseNetwork create(String filename) throws BruseAPIException {
		// load the network from the file
		long StartTime = System.currentTimeMillis();
		BNParser parser = BNParserFactory.create(filename);
		parser.parse();
		
		BruseNetwork net = new BruseNetwork();
		
		createNetwork(net, parser);
		
		long EndTime = System.currentTimeMillis();
		BookKeepingMgr.TimeNetworkLoad = (EndTime - StartTime);
		
		//dumpNetwork(net);
	
		return net;
	}
	
	private static void dumpNetwork(BruseNetwork network) {
		DecimalFormat formatter = new DecimalFormat("#0.0000000000");
		
		for (int i=0; i < network.getAllNodes().size(); i++) {
			BruseNode node = network.getAllNodes().get(i);
			
			System.out.println("\n" + node.getName());
			
			for (int j=0; j < node.getStates().size(); j++) {
				BruseNodeState state = node.getStates().get(j);
				System.out.println(state.getStateName() + ": " + formatter.format(state.getValue()));	
			}
			
			System.out.print("PARENTS: ");
			for (int k=0; k < node.getParents().size(); k++) {
				BruseNode parent = node.getParents().get(k);
				System.out.print(parent.getName() + ", ");
			}
			System.out.println();
			System.out.print("TABLE: ");
			double vals[] = node.getTable().getTableValues();
			for (int k=0; k < vals.length; k++) {
				System.out.print(vals[k] + ", ");
			}
			System.out.println();
		}
	}
	
	private static void createNetwork(BruseNetwork net, BNParser parser) {
		try {
			Variable var = null;
			BruseNode node = null, parent = null;
			Iterator<Variable> it = parser.getVariables().iterator();
			
			while (it.hasNext()) {
				var = it.next();
				node = net.getNode(var.getName());
				if (node == null) {	// node hasn't already been created and add to net
					node = new BruseNode(var.getName());
					net.addNode(node);
					createNodeStates(node, var.getStates());
				}
				
				// create parents and children
				createEdges(net, node, var);
				
				createTable(node, var);
			}
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void createTable(BruseNode node, Variable var) {
		ArrayList<BruseNode> vars = getTableVars(node);
		BruseTable table = new BruseTable(vars);
		
		table.setTableValues(var.getTable());
		
		// associate the table with the bruse node
		node.setTable(table);
	}
	
	private static void createEdges(BruseNetwork net, BruseNode node, Variable var) throws BruseAPIException {
		BruseNode parent = null;
		Variable v = null;
		ListIterator<Variable> it = var.getParents().listIterator();
		
		while (it.hasNext()) {
			v = it.next();
			parent = net.getNode(v.getName());
			if (parent == null) { // parent hasn't been created and add to net
				parent = new BruseNode(v.getName());
				net.addNode(parent);
				createNodeStates(parent, v.getStates());
			}
			
			// create edges
			node.addParent(parent);
			parent.addChild(node);
		}
	}
	
	private static void createNodeStates(BruseNode node, LinkedList<String> stateNames) {
		ListIterator<String> it = stateNames.listIterator();

		while (it.hasNext()) {
			node.addState(it.next(), 0);
		}
	}
	
	private static ArrayList<BruseNode> getTableVars(BruseNode node) {
		ArrayList<BruseNode> nodes = new ArrayList<BruseNode>();
		nodes.addAll(node.getParents());
		
		// Add the node this table is associated with to end of list of parents
		nodes.add(node);
		
		return nodes;
	}
	
}
